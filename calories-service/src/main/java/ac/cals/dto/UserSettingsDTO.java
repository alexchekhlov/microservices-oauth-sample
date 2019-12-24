package ac.cals.dto;

import ac.common.auth.UserRole;

public class UserSettingsDTO {
	private Long userId;
	private String username;
	private UserRole role;
	private Integer caloriesPerDay;

	public UserSettingsDTO() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getCaloriesPerDay() {
		return caloriesPerDay;
	}

	public void setCaloriesPerDay(Integer caloriesPerDay) {
		this.caloriesPerDay = caloriesPerDay;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
