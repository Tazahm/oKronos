package tz.okronos.core;

/** The side of the field : right or left. */
public enum PlayPosition {
	LEFT,
	RIGHT;
	
	/**
	 * An index (0 for LEFT, 1 for RIGHT) matching the side.
	 * @return the index.
	 */
	public int getIndex() {
		return ordinal();
	}
}
