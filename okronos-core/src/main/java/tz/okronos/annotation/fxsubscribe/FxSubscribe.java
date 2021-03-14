package tz.okronos.annotation.fxsubscribe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extends {@link com.google.common.eventbus.Subscribe} annotations by calling the target method
 * into the FX main loop. 
 * Each classes that uses this annotation shall be registered with the help of the 
 * {@link FxSubcriberController.register} method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxSubscribe {
}

