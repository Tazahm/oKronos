package tz.okronos.scene;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import lombok.Getter;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;

/**
 *  A controller linked to a position. This controller is intended to be referenced in a fxml
 *  file includes into a main file. The controller assesses its position (left or right) by walking
 *  up into the control tree. The first parent whose id begins with "left" or "right" determines
 *  the position.
 */
public abstract class AbstractSceneControllerLateralized extends AbstractSceneController
		implements Initializable, Lateralized {
	@Getter
	protected PlayPosition side;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			try {
				initSide();
				postInit();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * Walk up inside parent to find the side the control belong to.
	 */
	protected void initSide() {
		final Node node = getNodeForInitialization();
		Parent parent = node.getParent();
		while (parent != null && side == null) {
			String id = parent.getId();
			if (id != null && id.startsWith("right")) {
				side = PlayPosition.RIGHT;
			} else if (id != null && id.startsWith("left")) {
				side = PlayPosition.LEFT;
			}
			parent = parent.getParent();
		}

		if (side == null) {
			throw new RuntimeException("Cannot init side of " + this.getClass().getName());
		}
	}

	/**
	 * Gives the node used to walk up into the tree of controls.
	 * @return the node.
	 */
	protected abstract Node getNodeForInitialization();

	/**
	 * Used to initialized the controllers once build.
	 * @throws Exception any initialization error.
	 */
	protected void postInit() throws Exception {
		// nop
	}

}
