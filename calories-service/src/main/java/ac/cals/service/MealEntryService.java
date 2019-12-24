package ac.cals.service;

import ac.cals.dto.MealEntryDTO;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.data.domain.Page;

public interface MealEntryService {

	void saveEntry(MealEntryDTO mealEntry);
	Page<MealEntryDTO> getEntries(QueryFilteredPageRequest request);
}
