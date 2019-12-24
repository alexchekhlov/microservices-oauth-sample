package ac.cals.service;

import ac.cals.domain.UserSettingsEntity;
import ac.cals.jpa.UserSettingsSpecification;
import ac.cals.repository.MealEntryRepository;
import ac.cals.repository.UserSettingRepository;
import ac.cals.dto.UserSettingsDTO;
import ac.cals.exception.UserNotFountException;
import ac.common.auth.BaseUser;
import ac.common.jpa.converter.PageRequestToPageableConverter;
import ac.common.jpa.criteria.QueryParser;
import ac.common.jpa.dto.QueryFilteredPageRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

	@Autowired
	private UserSettingRepository userSettingRepository;
	@Autowired
	private MealEntryRepository mealEntryRepository;
	@Autowired
	private PageRequestToPageableConverter requestToPageableConverter;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private QueryParser filterQueryParser;

	@Override
	public void updateUserSettings(UserSettingsDTO userSettings) {

		UserSettingsEntity settings = userSettingRepository.findById(userSettings.getUserId()).orElseThrow(() -> new UserNotFountException(userSettings.getUserId().toString()));
		settings.setCaloriesPerDay(userSettings.getCaloriesPerDay());

		userSettingRepository.save(settings);
	}

	@Override
	public Page<UserSettingsDTO> getUserSettings(QueryFilteredPageRequest request) {

		String filter = request == null ? null : request.getFilter();
		Pageable pageable = requestToPageableConverter.convert(request, Sort.by("username"));
		Specification spec = filterQueryParser.parse(filter, UserSettingsSpecification::new);
		Page<UserSettingsEntity> entitiesPage = userSettingRepository.findAll(spec, pageable);
		Page<UserSettingsDTO> result = entitiesPage.map(userSettingsEntity -> modelMapper.map(userSettingsEntity, UserSettingsDTO.class));
		return result;
	}

	@Override
	public UserSettingsDTO getUserSettings(Long userId) {
		UserSettingsEntity entity = userSettingRepository.findById(userId).orElseThrow(() -> new UserNotFountException(String.valueOf(userId)));
		return modelMapper.map(entity, UserSettingsDTO.class);
	}

	@Override
	@Transactional
	public void deleteUserSettings(Long userId) {
		mealEntryRepository.deleteAllByUserId(userId);
		userSettingRepository.deleteById(userId);
	}

	@Override
	public void saveOrUpdateAuthUserInfo(BaseUser authUser) {
		UserSettingsEntity entity = userSettingRepository.findById(authUser.getId()).orElse(new UserSettingsEntity(authUser.getId(), null, null, null));
		entity.setUsername(authUser.getUsername());
		entity.setRole(authUser.getRole());
		userSettingRepository.save(entity);
	}
}
