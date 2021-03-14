package tz.okronos.core;

public interface LateralizedPair<T> {
	public T getLeft();
	public T getRight();
	public T getFromPosition(PlayPosition position);
}
