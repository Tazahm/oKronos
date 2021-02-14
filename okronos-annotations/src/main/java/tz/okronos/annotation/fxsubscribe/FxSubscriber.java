package tz.okronos.annotation.fxsubscribe;

/**
 * Each proxy class that handles the {@link FxSubscribe} annotations implements
 * this interface.
 * @param <T> the type of the target class.
 */
public interface FxSubscriber<T>  {
	public void setTarget(T target);
	public T getTarget();
}
