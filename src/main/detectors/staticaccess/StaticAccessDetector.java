package detectors.staticaccess;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierApplications;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierValue;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierAnnotation;
import org.apache.bcel.classfile.Method;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticAccessDetector extends OpcodeStackDetector {
	private static final String DETECTOR_ANNOTATION_CLASS_NAME = "detectors/staticaccess/StaticIndependent";

	private final BugReporter bugReporter;
	private final TypeQualifierValue staticIndependentTypeQualifier;
	private boolean visitingStaticIndependentMethod;

	public StaticAccessDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
		this.staticIndependentTypeQualifier = TypeQualifierValue.getValue(
				DescriptorFactory.createClassDescriptor(DETECTOR_ANNOTATION_CLASS_NAME), null);
	}

	public void visitClassContext(ClassContext classContext) {
		System.out.println("Visiting " + classContext.getXClass().getClassDescriptor().getClassName());
		super.visitClassContext(classContext);
	}

	@Override
	public void visitMethod(Method obj) {

		System.out.println("Visit method " + getXMethod().getName() + ", " + isMethodStaticIndependent());

		visitingStaticIndependentMethod = isMethodStaticIndependent();

		super.visitMethod(obj);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public void report() {
		super.report();
	}

	public void sawOpcode(int seen) {
		if (visitingStaticIndependentMethod) {
			switch (seen) {
				// Static field read/write
				case GETSTATIC:
				case GETSTATIC_QUICK:
				case GETSTATIC2_QUICK:
				case PUTSTATIC:
				case PUTSTATIC_QUICK:
				case PUTSTATIC2_QUICK:
					// Reporting a bug - this is not allowed
					BugInstance bugInstance = new BugInstance(this, "STATIC_ACCESS_NOT_ALLOWED", HIGH_PRIORITY)
							.addClassAndMethod(this)
							.addReferencedField(this)
							.addSourceLine(this);
					bugReporter.reportBug(bugInstance);
					break;

				// Method invocation
				case INVOKEINTERFACE:
				case INVOKEINTERFACE_QUICK:
				case INVOKENONVIRTUAL: // INVOSPECIAL
				case INVOKENONVIRTUAL_QUICK:
				case INVOKESTATIC:
				case INVOKESTATIC_QUICK:
				case INVOKESUPER_QUICK:
				case INVOKEVIRTUAL:
				case INVOKEVIRTUAL_QUICK:
				case INVOKEVIRTUAL_QUICK_W:
				case INVOKEVIRTUALOBJECT_QUICK:
					// Checking if the method is also static independent
					System.out.println("getXMethodOperand().getMethodDescriptor().getName() = " + getXMethodOperand().getMethodDescriptor().getName());
					break;

				default:
					// Doing nothing
			}
		}
	}

	private boolean isMethodStaticIndependent() {
		TypeQualifierAnnotation tqa = TypeQualifierApplications.getEffectiveTypeQualifierAnnotation(getXMethod(),
				staticIndependentTypeQualifier);
		return tqa != null;
	}
}
