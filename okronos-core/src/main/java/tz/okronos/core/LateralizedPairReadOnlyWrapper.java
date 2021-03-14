package tz.okronos.core;

import lombok.Setter;

/**
 * A read-only wrapper for a {@link SimpleLateralizedPair} instance.
 *
 * @param <T> the type of the items.
 */
public class LateralizedPairReadOnlyWrapper<T> implements LateralizedPair<T> {
	@Setter private SimpleLateralizedPair<T> delegate;
	
	public LateralizedPairReadOnlyWrapper() {}
	
	public LateralizedPairReadOnlyWrapper(SimpleLateralizedPair<T> delegate) {
		this.delegate = delegate;
	}
	
	public T getLeft() {
		return delegate.getLeft();
	}
	
	public T getPosition(PlayPosition position) {
		return delegate.getFromPosition(position);
	}
	
	public T getRight() {
		return delegate.getRight();
	}

	@Override
	public T getFromPosition(PlayPosition position) {
		return delegate.getFromPosition(position);
	}
}
