package detectors.staticaccess;

import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.Hierarchy;
import edu.umd.cs.findbugs.ba.XMethod;

import java.util.regex.Pattern;

/**
 * Based on {@link edu.umd.cs.findbugs.ba.bcp.Invoke}. Most of the classes/code there is private, hence the copying ...
 * The class as a whole couldn't have been reused, as the matching there is used to look for invocations fo methods,
 * not for matching methods themselves.
 * @author Adam Warski (adam at warski dot org)
 */
public class MethodMatcher {
	/**
	 * Match ordinary (non-constructor) instance invocations.
	 */
	public static final int INSTANCE = 1;

	/**
	 * Match static invocations.
	 */
	public static final int STATIC = 2;

	/**
	 * Match object constructor invocations.
	 */
	public static final int CONSTRUCTOR = 4;

	/**
	 * Match ordinary methods (everything except constructors).
	 */
	public static final int ORDINARY_METHOD = INSTANCE | STATIC;

	/**
	 * Match both static and instance invocations.
	 */
	public static final int ANY = INSTANCE | STATIC | CONSTRUCTOR;
	
	private static interface StringMatcher {
		public boolean match(String s);
	}

	private static class ExactStringMatcher implements StringMatcher {
		private String value;

		public ExactStringMatcher(String value) {
			this.value = value;
		}

		public boolean match(String s) {
			return s.equals(value);
		}
	}

	private static class RegexpStringMatcher implements StringMatcher {
		private Pattern pattern;

		public RegexpStringMatcher(String re) {
			pattern = Pattern.compile(re);
		}

		public boolean match(String s) {
			return pattern.matcher(s).matches();
		}
	}

	private static class SubclassMatcher implements StringMatcher {
		private String className;

		public SubclassMatcher(String className) {
			this.className = className;
		}

		public boolean match(String s) {
			try {
				return Hierarchy.isSubtype(s, className);
			} catch (ClassNotFoundException e) {
				AnalysisContext.reportMissingClass(e);
				return false;
			}
		}
	}

	private final StringMatcher classNameMatcher;
	private final StringMatcher methodNameMatcher;
	private final StringMatcher methodSigMatcher;
	private final int mode;

	public MethodMatcher(String className, String methodName, String methodSig, int mode) {
		this.classNameMatcher = createClassMatcher(className);
		this.methodNameMatcher = createMatcher(methodName);
		this.methodSigMatcher = createMatcher(methodSig);
		this.mode = mode;
	}

	private StringMatcher createClassMatcher(String s) {
		return s.startsWith("+")
				? new SubclassMatcher(s.substring(1))
				: createMatcher(s);
	}

	private StringMatcher createMatcher(String s) {
		return s.startsWith("/")
				? new RegexpStringMatcher(s.substring(1))
				: new ExactStringMatcher(s);
	}

	public boolean matches(XMethod method) {
		String methodName = method.getName();
		boolean isStatic = method.isStatic();
		boolean isCtor = methodName.equals("<init>");

		int actualMode = 0;

		if (isStatic) actualMode |= STATIC;
		if (isCtor) actualMode |= CONSTRUCTOR;
		if (!isStatic && !isCtor) actualMode |= INSTANCE;

		// Intersection of actual and desired modes must be nonempty.
		if ((actualMode & mode) == 0) {
			return false;
		}

		// Check class name, method name, and method signature.
		if (!methodNameMatcher.match(methodName) ||
				!methodSigMatcher.match(method.getSignature()) ||
				!classNameMatcher.match(method.getClassName())) {
			return false;
		}

		// It's a match!
		return true;
	}
}
