package tz.okronos.core;

import java.util.Optional;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;


public class PropertyItem<T> implements PropertySheet.Item {
	 private Class<?> type;
	 private String category;
	 private String name;
	 private String description;
	 private Property<T> property;
	 private Optional<Class<? extends PropertyEditor<?>>> propertyEditorClass;
	 
	 
	public PropertyItem(String name, String category, Property<T> property,
			Class<? extends PropertyEditor<?>> propertyEditorClass) {
		super();
		this.name = name;
		this.description = name;
		this.category = category;
		this.property = property;
		this.propertyEditorClass = propertyEditorClass == null ? Optional.empty() : Optional.of(propertyEditorClass);
		this.type = property.getValue().getClass();
	}
	
	public PropertyItem(String name, String category, Property<T> property) {
		this(name, category, property, null);
	}
	
	public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return propertyEditorClass;
    }
	
	public Class<?> getType() {
		return type;
	}
	
	public String getCategory() {
		return category;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Object getValue() {
		return property.getValue();
	}
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		this.property.setValue((T)value);
	}
	public Optional<ObservableValue<? extends Object>> getObservableValue() {
		return Optional.of(property);
	}
}