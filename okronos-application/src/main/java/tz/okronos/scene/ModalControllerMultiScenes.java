package tz.okronos.scene;

/**
 * Base class for controllers that can chain many scenes into the same stage.
 * Only on second scene is handled.
 */
public class ModalControllerMultiScenes extends ModalController {
	
	// TODO as used only once with implementation problem, remove this class.
	
	protected ModalController secondaryController;

	public void toggleScene() {
		stage.setScene(secondaryController.getScene());
	}
	
	public ModalController getSecondaryController() {
		return secondaryController;
	}

	public void setSecondaryController(ModalController secondaryController) {
		this.secondaryController = secondaryController;
		secondaryController.setStage(stage);
	}
	
	protected void doShowModal() {
		super.doShowModal();
		secondaryController.doShowModal();
	}
	
	protected void doHideModal() {
		super.doHideModal();
		secondaryController.doHideModal();
	}
	
}
