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
 *  A controller linked to a position.
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

	protected abstract Node getNodeForInitialization();

	protected void postInit() throws Exception {
		// nop
	}

}
