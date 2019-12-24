package ac.cals.controller;

import ac.cals.dto.UserSettingsDTO;
import ac.cals.service.UserSettingsService;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class UserSettingsController {

	@Autowired
	private UserSettingsService userSettingsService;

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || #userSettingsDTO.userId == authentication.principal.id")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateUserSettings(@RequestBody UserSettingsDTO userSettingsDTO) {
		userSettingsService.updateUserSettings(userSettingsDTO);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	@RequestMapping("/list")
	public Page<UserSettingsDTO> getUserSettings(@RequestBody QueryFilteredPageRequest request) {
		return userSettingsService.getUserSettings(request);
	}

	@GetMapping
	@RequestMapping("/{userId}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') || #userId == authentication.principal.id")
	public UserSettingsDTO getUserSettings(@PathVariable("userId") Long userId) {
		return userSettingsService.getUserSettings(userId);
	}
}
