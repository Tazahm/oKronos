package tz.okronos.scene.operator;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import tz.okronos.controller.penalty.model.PenaltyVolatile;

/**
 *  Allows to modify the remaining time of a penaltyVolatile.
 */
public class PenaltyTimeInputController extends TimeInputAbstractController {
	
	private PenaltyVolatile penalty;
	private PenaltyInputController penaltyInputController;
	
	public PenaltyTimeInputController() {}
	
	public void setPenalty(PenaltyVolatile input) {
		this.penalty = input;
	}
	
	protected void doHide() {
		stage.setScene(penaltyInputController.getScene());
	}
	
	protected void postValidateAction(ActionEvent event) {
		stage.setScene(penaltyInputController.getScene());
	}
	
	protected void postCancelAction(ActionEvent event) {
		stage.setScene(penaltyInputController.getScene());
	}
	
	@Override
	protected SimpleIntegerProperty getSourceProperty() {
		return penalty.remainderProperty();
	}

	public void setPenaltyInputController(PenaltyInputController penaltyInputController) {
		this.penaltyInputController = penaltyInputController; 
	}

}
