package tz.okronos.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.core.KronoContext;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.core.KronoHelper;

/**
 * Helper to build the scene controllers of the application.
 */
@Component
public class SceneBuilderHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(SceneBuilderHelper.class);
	@Autowired private KronoContext context;
	
    /**
     * Utility to build a stage, scene and controller from a fxml file.
     * @param <T> the type of the controller.
     * @param stage the main stage.
     * @param rscr the fxml file.
     * @param titleKey the key for the title of the stage.
     * @return the controller.
     * @throws Exception on any errors.
     */
    public <T extends AbstractSceneController> T buildStageAndController(Stage stage, String rscr, String titleKey) 
    		throws Exception {
    	T controller = buildSceneAndController(rscr);
    	
    	if (stage == null) stage = new Stage();
        stage.setScene(controller.getScene());    
        stage.setTitle(context.getItString(titleKey));
    	controller.setStage(stage);
    	
        return controller;
    }

    /**
     * Utility to build a scene and controller.
     * @param <T> the type of the controller.
     * @param rscr the fxml file.
     * @return the controller.
     * @throws Exception on any errors.
     */
    public <T extends AbstractSceneController> T buildSceneAndController(String rscr) 
    		throws Exception {
    	
    	final FXMLLoader fxmlLoader = new FXMLLoader();
    	fxmlLoader.setLocation(context.getResource(rscr, ResourceType.CONFIG));
    	fxmlLoader.setResources(context.getResourceBundle());
    	final Parent root = fxmlLoader.load();
    	final T ctrl = fxmlLoader.getController();
    	
    	Scene scene = new Scene(root);
    	ctrl.setScene(scene);
        return ctrl;
    }
    
    /**
     * Shortcut when no stage exists.
     * @param <T> the type of the controller.
     * @param rscr the fxml file.
     * @param titleKey the key for the title.
     * @return the controller.
     * @throws Exception on any errors.
     */
    public <T extends AbstractSceneController> T buildStageAndController(String rscr, String titleKey) 
    		throws Exception {
    	return buildStageAndController(null, rscr, titleKey);
    }
    
    /**
     * Utility to builds a modal stage.
     * @param <T> the type of the controller.
     * @param primary the primary stage.
     * @param rscr the fxml file.
     * @param titleKey the key for the title of the stage.
     * @return the controller.
     * @throws Exception on any errors.
     */
    public <T extends ModalController> T buildModalStage(Stage primary, String rscr, String titleKey) 
    		throws Exception {
    	final T ctrl = buildStageAndController(rscr, titleKey);
    	
        Stage stage = ctrl.getStage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(primary.getOwner());

        return ctrl;
    }

    /**
     * Handles a close request - requests confirmation and next sends a shutdown event.
     * @param event the event.
     * @param stage the stage.
     */
    public void handleCloseWindowEvent(final WindowEvent event, final Stage stage) {
        if (KronoHelper.requestUser(context, "operator.onExitAction.title", 
        		"operator.onExitAction.message", stage)) {
        	LOGGER.info("Stop oKronos");
        	context.postEvent(new ShutdownRequest());
        } else {
        	event.consume();
        }
    }

}
