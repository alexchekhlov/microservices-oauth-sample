package ac.common.jpa.criteria;

import org.springframework.data.jpa.domain.Specification;

public interface CriteriaToSpecificationConverter<T> {
	Specification<T> convert(SearchCriteria criteria);
}
