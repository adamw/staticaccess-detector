package pl.net.mamut.staticaccess;

import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * <p>
 * Annotation for marking methods which can be static dependent, that is, can read or write {@code static}
 * variables and invoke any methods. This annotation can be used to override a default @{@link StaticIndependent}
 * annotation.
 * </p>
 *
 * <p>
 * <b>Warning:</b> If this annotation is placed on a method, or as a default class/package annotation, it
 * <b>always</b> overrides @{@link StaticIndependent}, even if it's placed on a narrower scope.
 * </p>
 * @author Adam Warski (adam at warski dot org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@TypeQualifier
public @interface StaticDependent {
}