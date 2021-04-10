package tz.okronos.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Window;

/**
 *  Base class for controllers of modal scenes. 
 */
public class ModalController extends AbstractSceneController {	
	protected boolean cancelled = false;
	protected boolean validated = false;
	protected boolean hide = true;
	
	@FXML protected void validateAction(ActionEvent event) {
		validated = true;
		cancelled = false;
		postValidateAction(event);
//		if (hide) {
		preHideModal();
		doHide();
//		}
	}

	@FXML protected void cancelAction(ActionEvent event) {
		cancelled = true;
		validated = false;
		postCancelAction(event);
//		if (hide) {
		preHideModal();
		doHide();
//		}
	}

	protected void doHide() {
		stage.hide();
	}
	
	public void showModal() {
		init();
		preShowModal();
		Platform.runLater(() -> center());		
		stage.showAndWait();
	}

	private void center() {
		stage.sizeToScene();
		stage.centerOnScreen();

		Window owner = stage.getOwner();
		if (owner == null) return;
		stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
		stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);		
	}

	protected void postValidateAction(ActionEvent event) {}
	protected void postCancelAction(ActionEvent event) {}
	protected void preShowModal() {}
	protected void preHideModal() {}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isValidated() {
		return validated;
	}
	
	public void init() {
		validated = false;
		cancelled = false;
	}
}
