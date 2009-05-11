package pl.net.mamut.staticaccess;

import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * <p>
 * Annotation for marking methods which should be static independent, that is, shouldn't read or write {@code static}
 * variables (with the exception of {@code static final} immutable objects, like {@code String}), or call methods
 * which aren't static-indepdenent. 
 * </p>
 * @author Adam Warski (adam at warski dot org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@TypeQualifier
public @interface StaticIndependent {
}
