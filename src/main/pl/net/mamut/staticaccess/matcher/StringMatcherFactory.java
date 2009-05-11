package pl.net.mamut.staticaccess.matcher;

import edu.umd.cs.findbugs.util.StringMatcher;
import edu.umd.cs.findbugs.util.ExactStringMatcher;
import edu.umd.cs.findbugs.util.RegexStringMatcher;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StringMatcherFactory {
	public static StringMatcher createClassMatcher(String s) {
		return s.startsWith("+")
				? new SubclassMatcher(s.substring(1))
				: createMatcher(s);
	}

	public static StringMatcher createMatcher(String s) {
		return s.startsWith("/")
				? new RegexStringMatcher(s.substring(1))
				: new ExactStringMatcher(s);
	}
}
