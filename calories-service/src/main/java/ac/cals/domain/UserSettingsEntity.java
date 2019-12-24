package ac.cals.domain;

import ac.common.auth.UserRole;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserSettingsEntity {

	@Id
	private Long id;

	@Column(unique = true)
	private String username;

	@Column
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(name = "cals_per_day")
	private Integer caloriesPerDay;

	public UserSettingsEntity() {
	}

	public UserSettingsEntity(Long id, String username, UserRole role, Integer caloriesPerDay) {
		this.id = id;
		this.username = username;
		this.role = role;
		this.caloriesPerDay = caloriesPerDay;
	}

	public Integer getCaloriesPerDay() {
		return caloriesPerDay;
	}

	public void setCaloriesPerDay(Integer caloriesPerDay) {
		this.caloriesPerDay = caloriesPerDay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
