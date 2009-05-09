package example;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;
import detectors.staticaccess.StaticIndependent;

@DefaultAnnotationForMethods(StaticIndependent.class)
public class Test3 {
	static int c;

	static void b() {
		c = 10;
	}
}