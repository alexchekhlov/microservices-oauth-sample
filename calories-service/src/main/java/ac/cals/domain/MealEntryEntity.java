package ac.cals.domain;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meal_entry")
public class MealEntryEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private UserSettingsEntity user;

	private String meal;

	private LocalDate date;

	private LocalTime time;

	@Column(name = "cals_count")
	private int calories;

	@Formula(value = "(cals_count > select u.cals_per_day from users u where u.id=user_id)")
	private Boolean exceedDayCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public UserSettingsEntity getUser() {
		return user;
	}

	public void setUser(UserSettingsEntity user) {
		this.user = user;
	}

	public Boolean isExceedDayCount() {
		return exceedDayCount;
	}

	public void setExceedDayCount(boolean exceedDayCount) {
		this.exceedDayCount = exceedDayCount;
	}
}
