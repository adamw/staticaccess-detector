package detectors.staticaccess;

/**
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class PrimitiveWrapperMethodsTest {
	@StaticIndependent
	public void test() {
		Integer.valueOf(10);
		String.format("%d", 10);
		Long.decode("0xA");
		Boolean.valueOf("false");
	}
}
