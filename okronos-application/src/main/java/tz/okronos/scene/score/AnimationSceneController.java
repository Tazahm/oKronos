package tz.okronos.scene.score;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.media.MediaView;
import lombok.Getter;
import tz.okronos.scene.AbstractSceneController;

/**
 *  Displays an animation clip into the score stage.
 */
@Component
@Scope("prototype")
public class AnimationSceneController extends AbstractSceneController {    
   @FXML @Getter private MediaView mediaView;
    

    public void toggleScene(Scene target) {
    	// Dimension a saved and next restored, otherwise the preferred 
    	// dimension of the media would be used.
    	double x = stage.getX();
    	double y = stage.getY();
    	double width = stage.getWidth();
    	double height = stage.getHeight();
    	boolean isMaximized =  stage.isMaximized();
    	boolean isFullScreen = stage.isFullScreen();
    	String hint = stage.getFullScreenExitHint();
    	
    	// Do not display the message that explains how to exit the full screen mode.
    	stage.setFullScreenExitHint("");
    	stage.setScene(target);
    	
    	stage.setX(x);
    	stage.setY(y);
    	stage.setWidth(width);
    	stage.setHeight(height);
    	stage.setMaximized(isMaximized);
    	stage.setFullScreen(isFullScreen);
    	stage.setFullScreenExitHint(hint);
    }
}
