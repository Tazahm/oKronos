package tz.okronos.scene.operator;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import tz.okronos.core.KronoContext;
import tz.okronos.scene.SceneBuilderHelper;

/**
 * Builds all scene controllers of the application.
 */
@Configuration
public class OperatorSceneBuilder {
	@Autowired private KronoContext context;
	@Autowired SceneBuilderHelper sceneBuilderHelper;
	

	/**
	 * Builds the operator view.
	 * @param stage the main stage.
	 * @return the controller.
	 * @throws Exception on any errors.
	 */
	@Bean
	@Scope("prototype")
    public OperatorSceneController operatorSceneController() throws Exception {
    	final OperatorSceneController controller = sceneBuilderHelper.buildStageAndController(context.getPrimaryStage(), "operator.fxml", "operator.title");
    	context.getPrimaryStage().setOnCloseRequest(e -> sceneBuilderHelper.handleCloseWindowEvent(e, context.getPrimaryStage()));
        return controller;
    }
	
	// Causes Circular reference
	//	@PostConstruct public void init() throws Exception {
	//		OperatorSceneController operatorSceneController = operatorSceneController();
	//		operatorSceneController.getStage().show();
	//	}
}
