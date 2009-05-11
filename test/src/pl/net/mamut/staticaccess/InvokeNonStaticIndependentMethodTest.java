package pl.net.mamut.staticaccess;

/**
 * Invoking a non-SI method from a SI method.
 * @author Adam Warski (adam at warski dot org)
 */
public class InvokeNonStaticIndependentMethodTest {
	@StaticIndependent
	public static void m1() {
		method();			// error
	}

	public static void method() { }
}