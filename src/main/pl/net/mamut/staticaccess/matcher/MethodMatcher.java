package pl.net.mamut.staticaccess.matcher;

import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.util.StringMatcher;

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

	private final StringMatcher classNameMatcher;
	private final StringMatcher methodNameMatcher;
	private final StringMatcher methodSigMatcher;
	private final int mode;

	public MethodMatcher(String className, String methodName, String methodSig, int mode) {
		this.classNameMatcher = StringMatcherFactory.createClassMatcher(className);
		this.methodNameMatcher = StringMatcherFactory.createMatcher(methodName);
		this.methodSigMatcher = StringMatcherFactory.createMatcher(methodSig);
		this.mode = mode;
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
		if (!methodNameMatcher.matches(methodName) ||
				!methodSigMatcher.matches(method.getSignature()) ||
				!classNameMatcher.matches(method.getClassName())) {
			return false;
		}

		// It's a match!
		return true;
	}
}
