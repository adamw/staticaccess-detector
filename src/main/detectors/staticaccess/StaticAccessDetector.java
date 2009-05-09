package detectors.staticaccess;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticAccessDetector implements Detector {
	private final BugReporter bugReporter;

	public StaticAccessDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	public void visitClassContext(ClassContext classContext) {
		System.out.println("Visiting " + classContext.getXClass().getClassDescriptor().getClassName());
	}

	public void report() {
		bugReporter.logError("StaticAccess error!");
	}
}
