package pl.net.mamut.staticaccess;

/**
 * Reading a static variable test.
 * @author Adam Warski (adam at warski dot org)
 */
public class ReadStaticVariableTest {
	@StaticIndependent
	public void m1() {
	    String a = OtherClass.b;		// error
		method(OtherClass.b);			// error
	}

	@StaticIndependent
	public static void method(String param) { }

	public static void m2() {
		String a = OtherClass.b;
		method(OtherClass.b);	
	}

	private static class OtherClass {
		static String b = "10";
	}
}