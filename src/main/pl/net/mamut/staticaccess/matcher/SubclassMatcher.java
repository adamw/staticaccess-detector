package pl.net.mamut.staticaccess.matcher;

import edu.umd.cs.findbugs.ba.Hierarchy;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.util.StringMatcher;

/**
 * @author Adam Warski (adam at warski dot org)
*/
public class SubclassMatcher implements StringMatcher {
	private String className;

	public SubclassMatcher(String className) {
		this.className = className;
	}

	public boolean matches(String s) {
		try {
			return Hierarchy.isSubtype(s, className);
		} catch (ClassNotFoundException e) {
			AnalysisContext.reportMissingClass(e);
			return false;
		}
	}
}
