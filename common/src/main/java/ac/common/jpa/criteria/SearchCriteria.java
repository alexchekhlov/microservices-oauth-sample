package ac.common.jpa.criteria;

public class SearchCriteria {

	private final String key;
	private final QueryOperator operator;
	private final String value;

	public SearchCriteria(String key, String operator, String value) {
		this.key = key;
		this.operator = QueryOperator.getOperator(operator);
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public QueryOperator getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
}
