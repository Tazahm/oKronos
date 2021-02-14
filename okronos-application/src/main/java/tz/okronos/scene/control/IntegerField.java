package tz.okronos.scene.control;

import javafx.beans.NamedArg;

/**
 *  A formatted field that contains an integer. The length of the integer is limited to
 *  n digits.
 *  @see IntegerFieldFormatter.
 */
public class IntegerField extends AbstractField<Integer, IntegerFieldFormatter> {
	
	public IntegerField() {
		this(2);
	}
	
	public IntegerField(@NamedArg("digits") final int digits) {
		super(new IntegerFieldFormatter(digits));
		setPrefColumnCount(digits);
	}
}
