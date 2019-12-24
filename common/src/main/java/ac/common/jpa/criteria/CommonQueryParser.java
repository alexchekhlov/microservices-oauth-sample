package ac.common.jpa.criteria;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonQueryParser implements QueryParser {

	private static Map<String, Operator> ops;

	private final static String LEFT_PARENTHESIS = "(";
	private final static String RIGHT_PARENTHESIS = ")";

	private enum Operator {
		OR(1), AND(2);
		final int priority;

		Operator(int p) {
			priority = p;
		}
	}

	static {
		Map<String, Operator> tempMap = new HashMap();
		tempMap.put(QueryOperator.OR_STR, Operator.OR);
		tempMap.put(QueryOperator.AND_STR, Operator.AND);

		ops = Collections.unmodifiableMap(tempMap);
	}

	@Override
	public Specification parse(String filterQuery, Function<SearchCriteria, Specification> converter) {

		if (StringUtils.isEmpty(filterQuery)) {
			return null;
		}

		filterQuery = filterQuery.trim();
		Deque criteriasStack = getSearchCriteriaStack(filterQuery);
		Deque<Specification> resultStack = new LinkedList<>();

		while (!criteriasStack.isEmpty()) {
			Object element = criteriasStack.pollLast();
			if (!(element instanceof String)) {
				resultStack.push(converter.apply((SearchCriteria) element));
			} else {
				Specification spec1 = resultStack.pop();
				Specification spec2 = resultStack.pop();
				if (element.equals(QueryOperator.AND_STR))
					resultStack.push(Specification.where(spec1)
							.and(spec2));
				else if (element.equals(QueryOperator.OR_STR))
					resultStack.push(Specification.where(spec1)
							.or(spec2));
			}
		}
		return resultStack.pop();
	}

	;

	private Deque getSearchCriteriaStack(String filterQuery) {

		Deque<Object> output = new LinkedList();
		Deque<String> stack = new LinkedList();

		String key = "";
		String operator = "";
		boolean keyAdded = false;
		boolean operatorAdded = false;

		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(filterQuery);
		while (m.find()) {
			String token = m.group(1);
			if (ops.containsKey(token.toUpperCase())) {
				while (!stack.isEmpty() && priorityIsHigher(token.toUpperCase(), stack.peek()))
					output.push(stack.pop()
							.equalsIgnoreCase(QueryOperator.OR_STR) ? QueryOperator.OR_STR : QueryOperator.AND_STR);
				stack.push(token.equalsIgnoreCase(QueryOperator.OR_STR) ? QueryOperator.OR_STR : QueryOperator.AND_STR);
			} else if (token.equals(LEFT_PARENTHESIS)) {
				stack.push(LEFT_PARENTHESIS);
			} else if (token.equals(RIGHT_PARENTHESIS)) {
				while (!stack.peek()
						.equals(LEFT_PARENTHESIS))
					output.push(stack.pop());
				stack.pop();
			} else {
				if (!keyAdded) {
					key = token.replace("\"", "");
					keyAdded = true;
				} else if (!operatorAdded) {
					operator = token;
					operatorAdded = true;
				} else {
					output.push(new SearchCriteria(key, operator, token.replace("\"", "")));
					keyAdded = false;
					operatorAdded = false;
				}
			}
		}

		while (!stack.isEmpty())
			output.push(stack.pop());

		return output;
	}

	private boolean priorityIsHigher(String currOp, String prevOp) {
		return (ops.containsKey(prevOp) && ops.get(prevOp).priority >= ops.get(currOp).priority);
	}

	private List<String> splitFilterQuery(String query) {

		LinkedList<String> list = new LinkedList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query);

		//split by spaces except inside quotes
		String item = "";
		boolean operatorAdded = false;

		while (m.find()) {
			String value = m.group(1);
			QueryOperator operator = QueryOperator.getOperator(value);
			if (operator == null && !operatorAdded) {
				list.add(value);
				continue;
			}

			if (operator != null) {
				item = list.pollLast() + " " + value;
				operatorAdded = true;
			} else {
				item = item + " " + value;
				list.add(item);
				operatorAdded = false;
				item = "";
			}
		}
//			list.add(m.group(1).replace("\"", ""));

		return list;
	}
}
