package tz.okronos.core;

/**
 * Contains an instance for the right side of the field and another for the left.
 *
 * @param <T> the type of the items.
 */
public class TwoSide<T> {
	private T left;
	private T right;
	
	public TwoSide() {
		// nop
	}
	
	public TwoSide(T left, T right) {
		super();
		this.left = left;
		this.right = right;
	}
	
	public T getLeft() {
		return left;
	}
	
	public T getPosition(PlayPosition position) {
		return PlayPosition.LEFT == position ? left : right;
	}
	
	public void setPosition(T value, PlayPosition position) {
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
	
	public TwoSide<T> clone() {
		return new TwoSide<>(left, right);
	}
	
	public TwoSideReadOnly<T> buildReadOnlyWrapper() {
		return new TwoSideReadOnly<>(this); 
	}
}
