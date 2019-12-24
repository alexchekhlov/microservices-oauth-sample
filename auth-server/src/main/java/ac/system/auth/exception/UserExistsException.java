package ac.system.auth.exception;

public class UserExistsException extends RuntimeException {

	public UserExistsException(String username) {
		super("User already exists: " + username);
	}
}
