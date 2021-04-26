package tz.okronos.scene.operator;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.ActionEvent;
import lombok.Getter;
import lombok.Setter;

public abstract class TimeInputAbstractDelegate {
	@Setter @Getter protected TimeInputController timeInputController;
	
	/**
	 * Provides the source of the time.
	 * @return the source.
	 */
	public abstract ReadOnlyIntegerProperty getSourceProperty();

	protected void postValidateAction(ActionEvent event) {}
	protected void postCancelAction(ActionEvent event) {}
	protected void preShowModal() {}
	protected void preHideModal() {}
}
