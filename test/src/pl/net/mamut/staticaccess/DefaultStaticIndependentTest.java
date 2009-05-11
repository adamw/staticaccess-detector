package pl.net.mamut.staticaccess;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;

/**
 * A method that is SI by default.
 * @author Adam Warski (adam at warski dot org)
 */
@DefaultAnnotationForMethods(StaticIndependent.class)
public class DefaultStaticIndependentTest {
	private static String x;

	public static void m1() {
	    String a = x;		// error
	}

	public static void m2() {
		x = "c";			// error
	}
}