package tz.okronos.core;

import lombok.Setter;

/**
 * A read-only wrapper for a {@link TwoSide} instance.
 *
 * @param <T> the type of the items.
 */
public class TwoSideReadOnly<T> {
	@Setter private TwoSide<T> delegate;
	
	public TwoSideReadOnly() {}
	
	public TwoSideReadOnly(TwoSide<T> delegate) {
		this.delegate = delegate;
	}
	
	public T getLeft() {
		return delegate.getLeft();
	}
	
	public T getPosition(PlayPosition position) {
		return delegate.getPosition(position);
	}
	
	public T getRight() {
		return delegate.getRight();
	}
}
