package ac.system.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ac.common.auth.UserRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(description = "All details about the User. ")
public class UserDTO {

	@ApiModelProperty(notes = "The database generated user ID")
	private Long id;
	@ApiModelProperty(notes = "Username. Unique for each user")
	private String username;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@ApiModelProperty(notes = "User's Role.")
	private UserRole role;

	public UserDTO() {
	}

	public UserDTO(String username, String password, UserRole role) {
		this.username = username;
		this.password = password;
		this.role = role;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserDTO userDTO = (UserDTO) o;
		return id.equals(userDTO.id) &&
				username.equals(userDTO.username) &&
				password.equals(userDTO.password) &&
				role == userDTO.role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, role);
	}

	@Override
	public String toString() {
		return "UserDTO{" +
				"id=" + id +
				", username='" + username + '\'' +
				", role=" + role +
				'}';
	}
}
