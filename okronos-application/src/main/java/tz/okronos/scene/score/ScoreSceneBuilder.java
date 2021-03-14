package tz.okronos.scene.score;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tz.okronos.controller.animation.AnimationActionController;
import tz.okronos.core.KronoContext;
import tz.okronos.scene.SceneBuilderHelper;
import tz.okronos.scene.control.PenaltyControl;

/**
 * Builds all scene controllers of the application.
 */
@Configuration
public class ScoreSceneBuilder {
	@Autowired private KronoContext context;
	@Autowired AnimationActionController animationActionController;
	@Autowired SceneBuilderHelper sceneBuilderHelper;
    
	@Bean
 	@Scope("prototype")
     public AnimationSceneController animationSceneController() 
     		throws Exception {
		 return sceneBuilderHelper.buildSceneAndController("animation.fxml");
	}
	
	@PostConstruct public void init() throws Exception {
    	ScoreSceneController scoreSceneController = scoresSceneController();
    	scoreSceneController.getStage().show();
	}

    /**
 	 * Builds the score view.
 	 * @param stage the main stage.
 	 * @return the controller.
 	 * @throws Exception on any errors.
 	 */
    @Bean
 	@Scope("prototype")
     public ScoreSceneController scoresSceneController() 
     		throws Exception {
         
     	// Creates a fake score scene only to compute the penalty control minimum sizes
     	ScoreSceneController fakeController = sceneBuilderHelper.buildStageAndController("scores.fxml", "score.title");
     	PenaltyControl.initMinimunSize(fakeController.getScene().getRoot().getStylesheets());
     	fakeController = null;
     	
     	// Creates the true controller
     	final ScoreSceneController controller = sceneBuilderHelper.buildStageAndController("scores.fxml", "score.title");
     	
     	// Set the stage on the screen required by the 'scoreLocation' property (if set).
        final Stage stage = controller.getStage();
     	int scoreLocation = context.getIntProperty("scoreLocation", 1);
     	Screen scoreScreen = findScoreScreen(scoreLocation);
     	if (scoreScreen != Screen.getPrimary() && scoreLocation > 1) {
         	Rectangle2D bounds = scoreScreen.getBounds();  
         	stage.setX(bounds.getMinX());  
         	stage.setY(bounds.getMinY());
     		stage.setFullScreen(true);
     	}
     	stage.setOnCloseRequest(e -> sceneBuilderHelper.handleCloseWindowEvent(e, stage));
        
     	// Links the animation action controller with the scene controllers.
     	AnimationSceneController animationSceneController = animationSceneController();
     	animationSceneController.setStage(controller.getStage());	
     	animationActionController.setAnimationSceneController(animationSceneController);
     	animationActionController.setScoreSceneController(controller);

     	return controller;
     }


    /**
     * Searches for the screen to use for the score view. Default to screen 1 if the no screen
     * matches the number.
     * @param screenNumber the number of the physical display as given by the operating system.
     * @return the screen.
     */
    private Screen findScoreScreen(int screenNumber) {
    	Screen scoreScreen = Screen.getPrimary();
        final List<Screen> allScreens = Screen.getScreens();
        if (allScreens.size() >= screenNumber) {
        	scoreScreen = allScreens.get(screenNumber - 1);
        }
        return scoreScreen;
    }

}
