package ac.cals.bootstrap;

import ac.cals.domain.MealEntryEntity;
import ac.cals.domain.UserSettingsEntity;
import ac.cals.repository.MealEntryRepository;
import ac.cals.repository.UserSettingRepository;
import ac.common.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DataPopulator implements CommandLineRunner {

	@Autowired
	private UserSettingRepository userSettingRepository;

	@Autowired
	private MealEntryRepository mealEntryRepository;

	@Override
	public void run(String... args) throws Exception {

		List<UserSettingsEntity> userSettings = new ArrayList();
		userSettings.add(new UserSettingsEntity(1L, "admin", UserRole.ADMIN, 2500));
		userSettings.add(new UserSettingsEntity(2L, "manager", UserRole.MANAGER, 1800));

		List<UserSettingsEntity> list = IntStream.range(1, 11)
				.mapToObj(ind -> new UserSettingsEntity(ind + 2L, "user" + ind, UserRole.USER, 1900 + 85 * ind))
				.collect(Collectors.toList());

		userSettings.addAll(list);
		userSettingRepository.saveAll(userSettings);

		String[] meals = {"Chicken", "Beer ,5 packs of Lays and cheesecake", "Beef"};
		int[] calories = {150, 2500, 250};
		List mealsList = new ArrayList();

		userSettings.forEach(setting -> {
			int count = (int) (Math.random() * 10) + 1;

			IntStream.range(1, count).forEach(ind ->
					{
						LocalDate localDate = LocalDate.of(2019, 10, ind * 2);
						LocalTime time = LocalTime.of(20 / ind + ind % 3, 5 + ind % 4 * 12);

						MealEntryEntity mealEntryEntity = new MealEntryEntity();
						mealEntryEntity.setMeal(meals[ind % 3]);
						mealEntryEntity.setCalories(calories[ind % 3]);
						mealEntryEntity.setTime(time);
						mealEntryEntity.setDate(localDate);
						mealEntryEntity.setUser(setting);
						mealsList.add(mealEntryEntity);
					}
			);
		});

		mealEntryRepository.saveAll(mealsList);
	}
}
