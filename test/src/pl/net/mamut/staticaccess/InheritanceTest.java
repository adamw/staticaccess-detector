package pl.net.mamut.staticaccess;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class InheritanceTest {
	private static interface Base {
		@StaticIndependent
		void compute();
	}

	private static class Impl implements Base {
		private static String a;

		public void compute() {
			a = "z";		// error
			other();		// error
		}

		private void other() { }
	}
}
