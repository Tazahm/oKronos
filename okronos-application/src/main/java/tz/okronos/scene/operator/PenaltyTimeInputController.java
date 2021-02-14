package tz.okronos.scene.operator;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tz.okronos.controller.penalty.model.PenaltyVolatile;

/**
 *  Allows to modify the remaining time of a penaltyVolatile.
 */
public class PenaltyTimeInputController extends TimeInputAbstractController {
	
	private PenaltyVolatile penaltyVolatile;
	
	
	public void setPenalty(PenaltyVolatile input) {
		this.penaltyVolatile = input;
	}
	
	@Override
	public void doShowModal() {
		super.doShowModal();
	    hide = false;
	}
	
	@FXML protected void validateAction(ActionEvent event) {
		cancelled = false;
		doValidateAction(event);
		toggleScene();
	}

	@FXML protected void cancelAction(ActionEvent event) {
		cancelled = true;
		doCancelAction(event);
		toggleScene();
	}
	
	@Override
	protected SimpleIntegerProperty getSourceProperty() {
		return penaltyVolatile.remainderProperty();
	}

}
