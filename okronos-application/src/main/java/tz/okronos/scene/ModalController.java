package tz.okronos.scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 *  Base class for controllers of modal scenes. 
 */
public class ModalController extends AbstractSceneController {	
	protected boolean cancelled;
	protected boolean hide = true;
	
	@FXML protected void validateAction(ActionEvent event) {
		doValidateAction(event);
		if (hide) {
			doHideModal();
			stage.hide();
		}
	}

	@FXML protected void cancelAction(ActionEvent event) {
		cancelled = true;
		doCancelAction(event);
		if (hide) {
			doHideModal();
			stage.hide();
		}
	}

	public void showModal() {
		cancelled = false;
		doShowModal();
		stage.sizeToScene();
		stage.showAndWait();
	}

	protected void doValidateAction(ActionEvent event) {}
	protected void doCancelAction(ActionEvent event) {}
	protected void doShowModal() {}
	protected void doHideModal() {}
	
	public boolean isCancelled() {
		return cancelled;
	}	
}
