package detectors.staticaccess;

/**
 * Reading a constant.
 * @author Adam Warski (adam at warski dot org)
 */
public class ReadConstantTest {
	private static final String c1 = "x";
	private static final Long c2 = 10l;
	// This is not a constant, it can contain mutable state.
	private static final OtherClass nc1 = new OtherClass();

	@StaticIndependent
	public void m1() {
	    String a = c1;
		Long b = c2;
		OtherClass c = nc1;		// error
		String d = OtherClass2.c3;
	}

	private static class OtherClass { }

	private static class OtherClass2 {
		static final String c3 = "y";
	}
}