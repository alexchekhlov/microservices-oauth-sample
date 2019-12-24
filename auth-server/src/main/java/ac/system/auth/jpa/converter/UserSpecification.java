package ac.system.auth.jpa.converter;

import ac.system.auth.domain.UserEntity;
import ac.common.auth.UserRole;
import ac.common.jpa.criteria.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecification implements Specification<UserEntity> {

	private final SearchCriteria searchCriteria;

	public UserSpecification(SearchCriteria searchCriteria) {
		super();
		this.searchCriteria = searchCriteria;
	}

	@Override
	public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		Comparable value = null;
		String key = searchCriteria.getKey();
		if (key.equals("username")) {
			value = searchCriteria.getValue();
		} else if (key.equals("id")) {
			value = Long.valueOf(searchCriteria.getValue());
		} else if (key.equals("role")) {
			value = UserRole.valueOf(searchCriteria.getValue());
		}
		switch (searchCriteria.getOperator()) {
			case EQUAL:
				return criteriaBuilder.equal(root.get(key), value);
			case NOT_EQUAL:
				return criteriaBuilder.notEqual(root.get(key), value);
			case GREATER:
				return criteriaBuilder.greaterThan(root.get(key), value);
			case LESS:
				return criteriaBuilder.lessThan(root.get(key), value);
			default:
				return null;
		}
	}
}
