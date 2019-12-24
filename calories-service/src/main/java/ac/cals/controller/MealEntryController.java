package ac.cals.controller;

import ac.cals.dto.MealEntryDTO;
import ac.cals.service.MealEntryService;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mealentry")
public class MealEntryController {

	@Autowired
	private MealEntryService mealEntryService;

	@PostMapping("/list")
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	public Page<MealEntryDTO> getMealEntries(@RequestBody QueryFilteredPageRequest request) {
		return mealEntryService.getEntries(request);
	}

	@PostMapping("/list/{userId}")
	@PreAuthorize("hasAnyAuthority('ADMIN') || (#userId == authentication.principal.id)")
	public Page<MealEntryDTO> getMealEntriesForUser(@PathVariable("userId") Long userId, @RequestBody QueryFilteredPageRequest request) {
		String userFilter = "userId eq " + userId;
		String filter = StringUtils.isEmpty(request.getFilter()) ? userFilter : "( " + request.getFilter() + " ) and " + userFilter;
		request.setFilter(filter);
		return mealEntryService.getEntries(request);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveMealEntry(@RequestBody MealEntryDTO entry) {
		mealEntryService.saveEntry(entry);
	}

	@PostMapping("/{userId}")
	@PreAuthorize("(#userId == authentication.principal.id)")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveMealEntryForUser(@PathVariable("userId") Long userId, @RequestBody MealEntryDTO entry) {
		entry.setUserId(userId);
		mealEntryService.saveEntry(entry);
	}
}
