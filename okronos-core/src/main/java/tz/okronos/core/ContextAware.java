package tz.okronos.core;

/**
 * A context aware class knows the context of the application.
 */
public interface ContextAware {
	public void setContext(KronoContext context);
}
