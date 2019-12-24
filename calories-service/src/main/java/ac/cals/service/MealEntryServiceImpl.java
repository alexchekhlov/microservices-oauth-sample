package ac.cals.service;

import ac.cals.domain.UserSettingsEntity;
import ac.cals.repository.UserSettingRepository;
import ac.cals.domain.MealEntryEntity;
import ac.cals.dto.MealEntryDTO;
import ac.cals.exception.MealEntryNotFoundException;
import ac.cals.exception.UserNotFountException;
import ac.cals.jpa.MealEntrySpecification;
import ac.cals.repository.MealEntryRepository;
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

@Service
public class MealEntryServiceImpl implements MealEntryService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserSettingRepository userSettingRepository;

	@Autowired
	private MealEntryRepository mealEntryRepository;

	@Autowired
	private PageRequestToPageableConverter requestToPageableConverter;

	@Autowired
	private NutritionProvider nutritionProvider;

	@Autowired
	private QueryParser filterQueryParser;

	@Override
	public void saveEntry(MealEntryDTO mealEntry) {
		UserSettingsEntity entity = userSettingRepository.findById(mealEntry.getUserId()).orElseThrow(() -> new UserNotFountException(mealEntry.getUserId().toString()));

		MealEntryEntity mealEntryEntity = new MealEntryEntity();
		if (mealEntry.getId() != null) {
			mealEntryEntity = mealEntryRepository.findById(mealEntry.getId()).orElseThrow(MealEntryNotFoundException::new);
		}

		if (mealEntry.getCalories() == null) {
			mealEntry.setCalories(nutritionProvider.getCalories(mealEntry.getMeal()));
		}

		modelMapper.map(mealEntry, mealEntryEntity);

		mealEntryEntity.setUser(entity);

		mealEntryRepository.save(mealEntryEntity);
	}

	@Override
	public Page<MealEntryDTO> getEntries(QueryFilteredPageRequest request) {
		Pageable pageable = requestToPageableConverter.convert(request, Sort.by("user.username"));
		Specification spec = filterQueryParser.parse(request.getFilter(), MealEntrySpecification::new);

		Page<MealEntryEntity> entitiesPage = mealEntryRepository.findAll(spec, pageable);

		Page<MealEntryDTO> result = entitiesPage.map(entity -> modelMapper.map(entity, MealEntryDTO.class));
		return result;
	}
}
