package tz.okronos.scene.operator;

import java.util.function.IntSupplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tz.okronos.core.KronoHelper;
import tz.okronos.scene.ModalController;
import tz.okronos.scene.control.TimeField;
import tz.okronos.scene.control.TimeKeyboard;

/**
 *  Allows to modify a time. Display 3 fields : the time field, the modified field
 *  and the diff field, and uses 2 modes : DIRECT of DIFF.
 *  <p>
 *  In DIRECT mode the time is modified directly and the user uses the modified field.
 *  Therefore the diff field displays the differential between the time and the modified
 *  fields.
 *  <p>
 *  IN DIFF mode the time is added or subtracted by the amount given by the diff field,
 *  that receives the input of the user. Therefore the modified field displays the time
 *  incremented or decremented by the content of the diff field.
 *  <p>
 *  The field that have the input uses the style "selected-field".
 */
@Component
public abstract class TimeInputAbstractController extends ModalController {
	public enum InputMode {
		DIRECT,
		DIFF;
	}

	protected InputMode inputMode;

	@FXML protected TimeField timeField;
	@FXML protected TimeField diffField;
	@FXML protected TimeField modifiedField;
	@FXML protected TimeKeyboard timeKeyboard;
	
	@Autowired @Qualifier("playTimeRunningProperty") protected ReadOnlyBooleanProperty playTimeRunningProperty;
	
	
    @PostConstruct 
    public void init()  {
    	context.registerEventListener(this);
	}
	
	private InvalidationListener timeListener;
	private InvalidationListener readOnlyListener;
	private ChangeListener<String> contentListener;
	private TimeField inputField;
	private TimeField readOnlyField;
	
	/**
	 * Provides the source of the time.
	 * @return the source.
	 */
	protected abstract ReadOnlyIntegerProperty getSourceProperty();

	private void bind() {		
		unbind();
		
		final ReadOnlyIntegerProperty timeSourceProperty = getSourceProperty();
		timeListener = o->timeField.setValue(timeSourceProperty.get());
		contentListener = (o,p,n)->inputField.commitValue();
		
		final IntSupplier readOnlyFunction;
		if (inputMode == InputMode.DIRECT) {
			inputField = modifiedField;
			readOnlyField = diffField;
			readOnlyFunction = () -> 
			    (modifiedField.getValue() == null ? 0 :  modifiedField.getValue())
			    - getSourceProperty().get();
		} else {
			inputField = diffField;
			readOnlyField = modifiedField;
			readOnlyFunction = () -> 
				(diffField.getValue() == null ? 0 : diffField.getValue())
				+ timeSourceProperty.get();
		}
		
		timeSourceProperty.addListener(timeListener);
		timeKeyboard.bindWithField(inputField);
		readOnlyListener = o->readOnlyField.setValue(readOnlyFunction.getAsInt());
		inputField.textProperty().addListener(readOnlyListener);
		timeSourceProperty.addListener(readOnlyListener);
		inputField.addContentListener(contentListener);

		int initialTime = inputMode == InputMode.DIRECT ? getSourceProperty().get() : 0;
		timeField.setInitialValue(initialTime);
		inputField.setInitialValue(initialTime);
		readOnlyListener.invalidated(null);
		
		timeField.setEditable(false);
		readOnlyField.setEditable(false);
		inputField.setEditable(true);
	}

	private void unbind() {
		if (timeListener == null) {
			return;
		}
		
		final ReadOnlyIntegerProperty timeSourceProperty = getSourceProperty();
		timeSourceProperty.removeListener(timeListener);
		timeKeyboard.unbindWithField(inputField);
		inputField.textProperty().removeListener(readOnlyListener);
		timeSourceProperty.removeListener(readOnlyListener);
		inputField.removeContentListener(contentListener);
		
		timeListener = null;
		readOnlyListener = null;
		inputField = null;
		readOnlyField = null;
	}

	
	public int getModifiedTime() {
		return modifiedField.getValue();
	}

	public InputMode getInputMode() {
		return inputMode;
	}	

	protected void postCancelAction(ActionEvent event) {
		// timeKeyboard.setTime(0);
	}

	public void preShowModal() {
		inputMode = playTimeRunningProperty.get() ? InputMode.DIFF : InputMode.DIRECT;
		KronoHelper.setStyle(modifiedField, inputMode == InputMode.DIRECT, "selected-field");
		KronoHelper.setStyle(diffField, inputMode == InputMode.DIFF, "selected-field");
		
		bind();
	}
	
//	protected void postValidateAction(ActionEvent event) {
//		super.postValidateAction(event);
//	}
	
	protected void preHideModal() {
		unbind();
	}
}
