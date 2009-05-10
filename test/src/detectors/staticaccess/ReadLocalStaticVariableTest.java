package detectors.staticaccess;

/**
 * Reading a static variable test.
 * @author Adam Warski (adam at warski dot org)
 */
public class ReadLocalStaticVariableTest {
	private static String x = "b";

	@StaticIndependent
	public static void m1() {
	    String a = x;		// error
		method(x);			// error
	}

	@StaticIndependent
	public static void method(String param) { }

	public static void m2() {
		String a = x;
	}
}