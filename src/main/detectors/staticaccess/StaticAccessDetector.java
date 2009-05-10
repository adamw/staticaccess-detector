package detectors.staticaccess;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.util.StringMatcher;
import edu.umd.cs.findbugs.util.ExactStringMatcher;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.ba.bcp.Invoke;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierApplications;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierValue;
import edu.umd.cs.findbugs.ba.jsr305.TypeQualifierAnnotation;
import org.apache.bcel.classfile.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import detectors.staticaccess.matcher.MethodMatcher;

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
		super.visitClassContext(classContext);
	}

	@Override
	public void visitMethod(Method obj) {
		visitingStaticIndependentMethod = isMethodStaticIndependent(getXMethod()) && !ignoreMethod(getXMethod());
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
					// Checking if we aren't getting a constant (static final immutable)
					XField referencedField = getXFieldOperand();
					// The field may be null if the class is missing
					if (referencedField != null && referencedField.isStatic() && referencedField.isFinal()) {
						String referencedFieldSignature = referencedField.getSignature();
						// Checking if it's immutable
						for (StringMatcher immutableClassSignature : immutableClassSignatures) {
							if (immutableClassSignature.matches(referencedFieldSignature)) {
								return;
							}
						}
					}

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
		boolean result = dependentTqa == null && independentTqa != null;
		if (!result) {
			// Additionally checking the implicit list
			for (MethodMatcher methodMatcher : implicitIndependentMethodMatchers) {
				if (methodMatcher.matches(method)) {
					return true;
				}
			}
		}

		return result;
	}

	private boolean ignoreMethod(XMethod method) {
		for (MethodMatcher methodMatcher : ignoreMethodMatchers) {
			if (methodMatcher.matches(method)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A list of method matchers, which are implicitly static-independent.
	 */
	private static final List<MethodMatcher> implicitIndependentMethodMatchers;
	/**
	 * A list of ignored methods, which aren't checked for being static-independent, regardless of the annotations.
	 */
	private static final List<MethodMatcher> ignoreMethodMatchers = new ArrayList<MethodMatcher>();
	/**
	 * A list of patterns for matching immutable classes.
	 */
	private static final List<StringMatcher> immutableClassSignatures;

	static {
		MethodMatcher[] implicitMethods = new MethodMatcher[] {
				new MethodMatcher("java.lang.String", "/.*", "/.*", Invoke.ANY),
				new MethodMatcher("java.lang.Integer", "/.*", "/.*", Invoke.ANY),
				new MethodMatcher("java.lang.Float", "/.*", "/.*", Invoke.ANY),
				new MethodMatcher("java.lang.Double", "/.*", "/.*", Invoke.ANY),
				new MethodMatcher("java.lang.Long", "/.*", "/.*", Invoke.ANY),
				new MethodMatcher("java.lang.Boolean", "/.*", "/.*", Invoke.ANY),
		};

		implicitIndependentMethodMatchers = Arrays.asList(implicitMethods);
	}

	static {
		ignoreMethodMatchers.add(new MethodMatcher("/.*", "<clinit>", "()V", Invoke.STATIC));
	}

	static {
		StringMatcher[] immutableClasses = new StringMatcher[] {
				new ExactStringMatcher("Ljava/lang/String;"),
				new ExactStringMatcher("Ljava/lang/Integer;"),
				new ExactStringMatcher("Ljava/lang/Float;"),
				new ExactStringMatcher("Ljava/lang/Double;"),
				new ExactStringMatcher("Ljava/lang/Long;"),
				new ExactStringMatcher("Ljava/lang/Boolean;")
		};

		immutableClassSignatures = Arrays.asList(immutableClasses);
	}
}

