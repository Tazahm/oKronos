package tz.okronos.core;

/**
 * A lateralized instance knows if it is assigned at the left or at the right side of the ground.
 */
public interface Lateralized {
	public PlayPosition getSide();
}
