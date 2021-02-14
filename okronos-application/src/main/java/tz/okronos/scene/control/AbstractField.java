package tz.okronos.scene.control;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

/**
 * A base class for formatted text fields. Displays the value of a type T in a implementation 
 * dependent way. Uses a AbstractFieldFormatter to display and control the input of the user.
 * 
 * The field contains an initial value. To restore the field to its initial value use the
 * method resetValue().
 *
 * @param <T> the type of the object to display.
 * @param <F> the type of the formatter.
 */
public class AbstractField<T, F extends AbstractFieldFormatter<T>> extends TextField {
	protected F formatter;

	public AbstractField (F formatter) {
		setTextFormatter(formatter);
		this.formatter = formatter;
	}
	
	public T getValue() {
		return formatter.getValue();
	}

	public void setValue(final T value) {
		formatter.setValue(value);
	}
	
	public F getFormatter() {
		return formatter;
	}
	
	/**
	 * Sets the initial value.
	 * @param value the initial value.
	 */
	public void setInitialValue(final T value) {
		formatter.setInitialValue(value);
	}
	
	/** Resets to the initial value. */
	public void resetValue() {
		// The formatter cannot handle a null value for reset.
		if (formatter.getInitialValue() == null) clear();
		else formatter.resetValue();
	}

	/** Resets (erase) the display. */
	public void clear() {
		setText("");
	}

	/** Is null value allowed ? */
	public void setAllowNull(boolean value) {
		formatter.setAllowNull(value);
	}
	
	/** Negates the value, for example, for an integer : new value = - old value. */
	public void negateValue() {
		commitValue();
		formatter.negateValue();
	}
	
	/**
	 * Adds a display listener. The listener will be called each time the input is 
	 * changed, even if the value is not commit and the value still unchanged.
	 * @param listener the listener.
	 */
	public void addContentListener(ChangeListener<String> listener) {
		getContent().addListener(listener);
	}
	
	/**
	 * Removes the display listener.
	 * @param listener the listener.
	 */
	public void removeContentListener(ChangeListener<String> listener) {
		getContent().removeListener(listener);
	}

}
