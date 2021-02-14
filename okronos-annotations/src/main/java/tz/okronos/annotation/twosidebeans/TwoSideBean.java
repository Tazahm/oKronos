package tz.okronos.annotation.twosidebeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Equivalent to Spring {@code Bean} but for a {@link TwoSideConfiguration} container.
 *  Each method annotated such a way is called twice, one time for each instance of {@link TwoSideConfiguration} container.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TwoSideBean {

}
