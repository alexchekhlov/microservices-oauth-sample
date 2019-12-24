package ac.common.jpa.criteria;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public interface QueryParser {
	Specification parse(String filterQuery, Function<SearchCriteria, Specification> converter);
}
