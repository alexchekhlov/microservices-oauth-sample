package ac.cals.jpa;

import ac.cals.domain.MealEntryEntity;
import ac.cals.domain.UserSettingsEntity;
import ac.common.auth.UserRole;
import ac.common.jpa.criteria.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MealEntrySpecification implements Specification<MealEntryEntity> {
	private final SearchCriteria searchCriteria;

	public MealEntrySpecification(SearchCriteria searchCriteria) {
		super();
		this.searchCriteria = searchCriteria;
	}

	@Override
	public Predicate toPredicate(Root<MealEntryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		Comparable value = null;
		String key = searchCriteria.getKey();
		if (key.equals("username") || key.equals("meal")) {
			value = searchCriteria.getValue();
		} else if (key.equals("id") || key.equals("userId")) {
			value = Long.valueOf(searchCriteria.getValue());
		} else if (key.equals("calories")) {
			value = Integer.valueOf(searchCriteria.getValue());
		} else if (key.equals("date")) {
			value = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(searchCriteria.getValue()));
		} else if (key.equals("time")) {
			value = LocalTime.from(DateTimeFormatter.ISO_TIME.parse(searchCriteria.getValue()));
		} else if (key.equals("exceedDayCount")) {
			value = Boolean.valueOf(searchCriteria.getValue());
		}
		else if (key.equals("role")) {
			value = UserRole.valueOf(searchCriteria.getValue());
		}

		Expression exp = getExpression(root, key);

		switch (searchCriteria.getOperator()) {
			case EQUAL:
				return criteriaBuilder.equal(exp, value);
			case NOT_EQUAL:
				return criteriaBuilder.notEqual(exp, value);
			case GREATER:
				return criteriaBuilder.greaterThan(exp, value);
			case LESS:
				return criteriaBuilder.lessThan(exp, value);
			default:
				return null;
		}
	}

	private Expression getExpression(Root<MealEntryEntity> root, String key) {
		if ("userId".equals(key) || "username".equals(key) || "role".equals(key)) {
			if ("userId".equals(key)) {
				key = "id";
			}
			Join<MealEntryEntity, UserSettingsEntity> userJoin = root.join("user");
			return userJoin.get(key);
		}
		return root.get(key);
	}
}
