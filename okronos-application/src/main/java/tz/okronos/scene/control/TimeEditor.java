package tz.okronos.scene.control;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

/**
 * A wrapper for Time field usable by the property sheets.
 */
public class TimeEditor extends  AbstractPropertyEditor<Number, TimeField> {

	SimpleIntegerProperty integerProperty;
	
	
	public TimeEditor(Item property, TimeField control) {
		super(property, control);
		bindProperty(control);
	}
	
	public TimeEditor(Item property, TimeField control, boolean readonly) {
		super(property, control, readonly);
		bindProperty(control);
	}

	public TimeEditor(Item property) {
		this(property, new TimeField());
	}
	
	@Override
	public void setValue(Number value) {
		getEditor().setValue(value.intValue());
	}

	@Override
	protected ObservableValue<Number> getObservableValue() {
		// The getter is called by the super constructor, thus the instance variable
		// shall be initialized here (cannot initialized into the constructor as it
		// would be null for the super constructor).
		if (integerProperty == null)
		{
			integerProperty = new SimpleIntegerProperty();
		}
		return integerProperty;
	}
	
	private void bindProperty(TimeField control) {
		integerProperty.bind(getEditor().getFormatter().valueProperty());
	}

}
