package ac.cals.service;

import ac.cals.dto.UserSettingsDTO;
import ac.common.auth.BaseUser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.springframework.data.domain.Page;

public interface UserSettingsService {
	void updateUserSettings(UserSettingsDTO userSettings);
	Page<UserSettingsDTO> getUserSettings(QueryFilteredPageRequest request);
	UserSettingsDTO getUserSettings(Long userId);
	void deleteUserSettings(Long userId);
	void saveOrUpdateAuthUserInfo(BaseUser authUser);
}
