package detectors.staticaccess;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;

/**
 * Methods are by default static independent, but static initialization should be of course allowed. 
 * @author Adam Warski (adam at warski dot org)
 */
@DefaultAnnotationForMethods(StaticIndependent.class)
public class StaticInitializorWithDefaultTest {
	public static String a = "a";
	public static Integer b = 10;
	public static Float c = 10.2f;
	public static long d = 123l;
	public static double e = 12.34;
}
