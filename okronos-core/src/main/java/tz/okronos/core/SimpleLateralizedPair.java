package tz.okronos.core;

/**
 * Contains an instance for the right side of the field and another for the left.
 *
 * @param <T> the type of the items.
 */
public class SimpleLateralizedPair<T> implements LateralizedPair<T> {
	private T left;
	private T right;
	
	public SimpleLateralizedPair() {
		// nop
	}
	
	public SimpleLateralizedPair(T left, T right) {
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
	
	public void setFromPosition(T value, PlayPosition position) {
		if (position == PlayPosition.LEFT) setLeft(value);
		else setRight(value);
	}
	
	public void setLeft(T left) {
		this.left = left;
	}
	
	public T getRight() {
		return right;
	}
	
	public void setRight(T right) {
		this.right = right;
	}
	
	public SimpleLateralizedPair<T> clone() {
		return new SimpleLateralizedPair<>(left, right);
	}
	
	public LateralizedPairReadOnlyWrapper<T> buildReadOnlyWrapper() {
		return new LateralizedPairReadOnlyWrapper<>(this); 
	}
}
