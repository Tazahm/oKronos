package tz.okronos.scene.control;

/**
 *  A formatted text field that contains an integer representing a time.
 *  @see TimeFieldFormatter
 */
public class TimeField extends AbstractField<Integer, TimeFieldFormatter> {
	
	public TimeField() {
		super(new TimeFieldFormatter());
		setPrefColumnCount(6);
	}

}
