package ac.common.jpa.converter;

import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageRequestToPageableConverter {
	Pageable convert(QueryFilteredPageRequest request, Sort defaultSort);
}
