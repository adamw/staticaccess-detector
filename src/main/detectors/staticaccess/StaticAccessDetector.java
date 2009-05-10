package detectors.staticaccess;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierApplications;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierValue;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierAnnotation;
import org.apache.bcel.classfile.Method;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StaticAccessDetector extends OpcodeStackDetector {
	private final BugReporter bugReporter;
	private final TypeQualifierValue staticIndependentTypeQualifier;
	private final TypeQualifierValue staticDependentTypeQualifier;
	private boolean visitingStaticIndependentMethod;

	public StaticAccessDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
		this.staticIndependentTypeQualifier = TypeQualifierValue.getValue(
				DescriptorFactory.createClassDescriptor(StaticIndependent.class), null);
		this.staticDependentTypeQualifier = TypeQualifierValue.getValue(
				DescriptorFactory.createClassDescriptor(StaticDependent.class), null);
	}

	public void visitClassContext(ClassContext classContext) {
		System.out.println("Visiting " + classContext.getXClass().getClassDescriptor().getClassName());
		super.visitClassContext(classContext);
	}

	@Override
	public void visitMethod(Method obj) {

		System.out.println("Visit method " + getXMethod().getName());

		visitingStaticIndependentMethod = isMethodStaticIndependent(getXMethod());
		super.visitMethod(obj);
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
					BugInstance sanaBugInstance = new BugInstance(this, "STATIC_ACCESS_NOT_ALLOWED", HIGH_PRIORITY)
							.addClassAndMethod(this)
							.addReferencedField(this)
							.addSourceLine(this);
					bugReporter.reportBug(sanaBugInstance);
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
					if (!isMethodStaticIndependent(getXMethodOperand())) {
						// Reporting a bug
						BugInstance nsimiBugInstance = new BugInstance(this, "NON_STATIC_INDEPENDENT_METHOD_INVOKED", HIGH_PRIORITY)
								.addClassAndMethod(this)
								.addCalledMethod(this)
								.addSourceLine(this);
						bugReporter.reportBug(nsimiBugInstance);
					}
					break;

				default:
					// Doing nothing
			}
		}
	}

	private boolean isMethodStaticIndependent(XMethod method) {
		TypeQualifierAnnotation independentTqa = TypeQualifierApplications.getEffectiveTypeQualifierAnnotation(method,
				staticIndependentTypeQualifier);
		TypeQualifierAnnotation dependentTqa = TypeQualifierApplications.getEffectiveTypeQualifierAnnotation(method,
				staticDependentTypeQualifier);
		// A method is static-independent if it's not annotated with @SD and is annotated with @SI
		return dependentTqa == null && independentTqa != null;
	}
}
