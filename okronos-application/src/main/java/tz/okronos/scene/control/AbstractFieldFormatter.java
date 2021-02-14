package tz.okronos.scene.control;

import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 * Base class to control the input of the user and display a value.
 * Used by each class implementing the AbstractField class.
 *
 * @param <T> the type of the object representing by the text field.
 */
public abstract class AbstractFieldFormatter<T> extends TextFormatter<T> {
	
	public AbstractFieldFormatter(StringConverter<T> valueConverter, T defaultValue, UnaryOperator<Change> filter) {
		super(valueConverter, defaultValue, filter);
	}

	private T initialValue;

	public void resetValue() {
		setValue(initialValue);
	}
	
	public void setInitialValue(T value) {
		initialValue = value;
		setValue(value);
	}
	
	public T getInitialValue() {
		return initialValue;
	}
	
	public abstract void setAllowNull(boolean value);
	public abstract void negateValue();
	public abstract void clear();
}
