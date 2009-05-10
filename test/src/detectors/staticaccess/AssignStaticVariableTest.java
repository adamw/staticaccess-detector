package detectors.staticaccess;

/**
 * Assigning a static variable test.
 * @author Adam Warski (adam at warski dot org)
 */
public class AssignStaticVariableTest {
	private static String x;

	@StaticIndependent
	public static void m1() {
	    x = "a";		// error
	}

	public static void m2() {
		x = "b";
	}
}
