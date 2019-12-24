package ac.cals.controller;

import ac.cals.service.UserSettingsService;
import ac.common.auth.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authuser")
public class AuthUserController {

	@Autowired
	private UserSettingsService userSettingsService;

	@PostMapping
	public void updateAuthUserInfo(@RequestBody BaseUser user) {
		userSettingsService.saveOrUpdateAuthUserInfo(user);
	}

	@DeleteMapping
	@RequestMapping("/{id}")
	public void deleteUser(@PathVariable("id") Long userId) {
		userSettingsService.deleteUserSettings(userId);
	}
}
