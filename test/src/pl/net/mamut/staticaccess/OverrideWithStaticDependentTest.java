package pl.net.mamut.staticaccess;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;

/**
 * Overriding a default @SI with @SD.
 * @author Adam Warski (adam at warski dot org)
 */
@DefaultAnnotationForMethods(StaticIndependent.class)
public class OverrideWithStaticDependentTest {
	private static String x;

	@StaticDependent
	public static void m1() {
	    String a = x;
	}

	@StaticDependent
	public static void m2() {
		x = "c";
	}
}