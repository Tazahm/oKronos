package tz.okronos.scene;

import javafx.scene.Scene;
import javafx.stage.Stage;
import tz.okronos.core.AbstractController;

/**
 *  A controller for a javafx stage.
 */
public class AbstractSceneController extends AbstractController {
	protected Stage stage;
	protected Scene scene;

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public Stage getStage() {
		return stage;
	}
	public Scene getScene() {
		return scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
