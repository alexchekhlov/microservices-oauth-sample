package ac.common.jpa.criteria;

public enum QueryOperator {
	EQUAL, NOT_EQUAL, GREATER, LESS;

	public static final String OR_STR = "OR";
	public static final String AND_STR = "AND";

	public static QueryOperator getOperator(String str) {

		if ("eq".equals(str)) {
			return EQUAL;
		} else if ("ne".equals(str)) {
			return NOT_EQUAL;
		} else if ("gt".equals(str)) {
			return GREATER;
		} else if ("lt".equals(str)) {
			return LESS;
		}
		return null;
	}
}
