package tz.okronos.core;

/**
 * Contains an instance for the right side of the field and another for the left.
 *
 * @param <T> the type of the items.
 */
public class ImmutableLateralizedPair<T> implements LateralizedPair<T> {
	private T left;
	private T right;
	
	public ImmutableLateralizedPair(T left, T right) {
		super();
		this.left = left;
		this.right = right;
		
		if (left instanceof SideAware) {
			((SideAware) left).setSide(PlayPosition.LEFT);
		}
		
		if (right instanceof SideAware) {
			((SideAware) right).setSide(PlayPosition.RIGHT);
		}
	}
	
	public T getLeft() {
		return left;
	}
	
	public T getFromPosition(PlayPosition position) {
		return PlayPosition.LEFT == position ? left : right;
	}
	
	public void setLeft(T left) {
		this.left = left;
	}
	
	public T getRight() {
		return right;
	}
}
