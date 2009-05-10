package detectors.staticaccess;

/**
 * Assigning a static variable test.
 * @author Adam Warski (adam at warski dot org)
 */
public class ReadStaticVariableTest {
	private static String x = "b";

	@StaticIndependent
	public static void m1() {
	    String a = x;
	}

	public static void m2() {
		String a = x;
	}
}