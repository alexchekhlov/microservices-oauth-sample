package ac.cals.exception;

public class UserNotFountException extends RuntimeException {

	public UserNotFountException(String id) {
		super("User not found, Id= " + id);
	}
}
