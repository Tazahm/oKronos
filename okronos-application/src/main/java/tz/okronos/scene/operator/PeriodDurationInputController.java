package tz.okronos.scene.operator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tz.okronos.scene.ModalController;
import tz.okronos.scene.control.IntegerField;
import tz.okronos.scene.control.NumberKeyboard;

/**
 *  Allows to modify the duration of the current period.
 */
public class PeriodDurationInputController extends ModalController {
	@FXML private IntegerField durationField;
	@FXML private NumberKeyboard keyboard;
	
	
	public PeriodDurationInputController() {
		
	}
	
	public int getDuration() {
		init();
		return durationField.getValue();
	}

	
	public void setDuration(int duration) {
		init();
		durationField.setValue(duration);
	}

	
	protected void preShowModal() {
		init();
	}
	
	protected void postCancelAction(ActionEvent event) {
	}
	
	public void init() {
		super.init();
		if (! keyboard.isBound()) {
			keyboard.bindWithField(durationField);
			keyboard.setShowSign(false);
		}
	}

}
