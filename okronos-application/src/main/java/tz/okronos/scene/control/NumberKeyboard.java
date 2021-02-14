package tz.okronos.scene.control;

import javafx.beans.NamedArg;

/**
 * A keyboard linked with an text field.
 */
public class NumberKeyboard extends AbstractKeyboard<Integer> {
	public NumberKeyboard() {
		super();
	}
	
	public NumberKeyboard(@NamedArg("columnCount") final int columnCount) {
		super(columnCount);
	}
}
