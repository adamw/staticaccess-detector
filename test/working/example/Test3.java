package example;

import detectors.staticaccess.StaticIndependent;

public class Test3 {
	private static interface Base {
		@StaticIndependent
		void compute();
	}

	private static String a;

	private static class Impl implements Base {
		public void compute() {
			a = "z";		// error
			other();		// error
		}

		private void other() { }
	}
}