package tz.okronos.scene.operator;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.ActionEvent;
import tz.okronos.controller.penalty.model.PenaltyVolatile;

/**
 *  Allows to modify the remaining time of a penaltyVolatile.
 */
public class TimeInputPenaltyDelegate extends TimeInputAbstractDelegate {
	
	private PenaltyVolatile penalty;
	private PenaltyInputController penaltyInputController;
	
	public TimeInputPenaltyDelegate() {}
	
	@Override
	public ReadOnlyIntegerProperty getSourceProperty() {
		return penalty.remainderProperty();
	}

	public void setPenalty(PenaltyVolatile input) {
		this.penalty = input;
	}
	
	protected void doHide() {
		timeInputController.getStage().setScene(penaltyInputController.getScene());
	}
	
	protected void postValidateAction(ActionEvent event) {
		timeInputController.getStage().setScene(penaltyInputController.getScene());
	}
	
	protected void postCancelAction(ActionEvent event) {
		timeInputController.getStage().setScene(penaltyInputController.getScene());
	}
	
	public void setPenaltyInputController(PenaltyInputController penaltyInputController) {
		this.penaltyInputController = penaltyInputController; 
	}

}
