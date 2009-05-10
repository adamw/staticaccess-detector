package example;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;
import detectors.staticaccess.StaticIndependent;
import detectors.staticaccess.StaticDependent;

@DefaultAnnotationForMethods(StaticIndependent.class)
public class Test3 {
	static int c;
	static final Integer z = 12;

	@StaticDependent
	static void b() {
		c = 10;
	}

	static void d() {
		c = 12;
	}

	static void e() {
		compute(z);
	}

	static void compute(Integer a) { }
}

interface Z {
	@StaticIndependent
	void i();
}

class ZZ implements Z {
	public void i() {
		Test3.c = 102;
	}
}