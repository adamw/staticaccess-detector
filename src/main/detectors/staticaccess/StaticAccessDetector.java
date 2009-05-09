package detectors.staticaccess;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.ba.ClassContext;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticAccessDetector extends OpcodeStackDetector {
	private final BugReporter bugReporter;

	public StaticAccessDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	public void visitClassContext(ClassContext classContext) {
		System.out.println("Visiting " + classContext.getXClass().getClassDescriptor().getClassName());
		super.visitClassContext(classContext);
	}

	public void report() {
		super.report();
	}

	public void sawOpcode(int seen) {
		System.out.println("seen = " + seen);
		if (seen == GETSTATIC || seen == PUTSTATIC) {
			System.out.println("REPORTING BUG");

			BugInstance bugInstance = new BugInstance(this, "STATIC_ACCESS_NOT_ALLOWED", HIGH_PRIORITY)
					.addClassAndMethod(this)
					.addReferencedField(this)
					.addSourceLine(this);
			bugReporter.reportBug(bugInstance);
		}
	}
}
