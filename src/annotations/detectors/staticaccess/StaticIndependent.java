package detectors.staticaccess;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation for marking methods which should be static independent, that is, shouldn't read or write {@code static}
 * variables. (With the exception of {@code static final} immutable objects, like {@code String}.)
 * @author Adam Warski (adam at warski dot org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StaticIndependent {
}
