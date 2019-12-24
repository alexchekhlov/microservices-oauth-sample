package ac.common.jpa.converter;

import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestToPageableConverterImpl implements PageRequestToPageableConverter {

	@Override
	public Pageable convert(QueryFilteredPageRequest request, Sort defaultSort) {
		if (request == null) {
			return Pageable.unpaged();
		}

		Sort sort = defaultSort;
		int page = request.getPage();
		int limit = request.getLimit() > 0 ? request.getLimit() : Integer.MAX_VALUE;
		if (request.getSort() != null) {
			Sort.Direction dir = QueryFilteredPageRequest.SORT_ORDER.DESC.equals(request.getSort().getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
			sort = Sort.by(dir, request.getSort().getField());
		}
		return PageRequest.of(page, limit, sort);
	}
}
