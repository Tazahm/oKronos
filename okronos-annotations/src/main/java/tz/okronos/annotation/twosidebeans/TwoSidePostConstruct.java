package tz.okronos.annotation.twosidebeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method that is annotated with this element is a post initializer method equivalent to Spring {@code PostConstruct}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TwoSidePostConstruct {

}
