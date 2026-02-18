package io.github.ysdaeth.jmodularcrypt.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotated field or fields with {@link Module} is a final field, then
 * public all args constructor with this annotation must be present. Constructor parameters
 * order must be the same as {@link Module#order()}. All args constructor means a constructor
 * with parameters matching with annotated fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface SerializerCreator {
}
