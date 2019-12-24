package ac.common.auth;

public enum UserRole {
	USER(3), MANAGER(2), ADMIN(1);

	private int priority;

	UserRole(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
