package tz.okronos.controller.animation;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.animation.event.request.AnimationStartRequest;
import tz.okronos.controller.animation.event.request.AnimationStopRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.scene.score.AnimationSceneController;
import tz.okronos.scene.score.ScoreSceneController;

/**
 *  Displays an animation clip into the score stage.
 *  Each time the animation is requested, replaces the score scene by the animation scene and
 *  starts a new media player. Each time the animation is stop, stops the media player and restores
 *  the score scene.
 */
@Component
@Slf4j
public class AnimationActionController extends AbstractController {
   private MediaPlayer player;
   
   @Setter private AnimationSceneController animationSceneController;
   @Setter private ScoreSceneController scoreSceneController;
   
   
   @PostConstruct 
   public void init() {
	    context.registerEventListener(this);
    }
 
 	@FxSubscribe public void onAnimationStartRequest(AnimationStartRequest event) {
  		startAnimation(event.getFileName());
  	}
  	
  	@FxSubscribe public void onAnimationStopRequest(AnimationStopRequest event) {
  		stopAnimation();
  	}
  	
    public MediaPlayer getPlayer() {
    	return player;
    }
    
    private void startAnimation(String fileName) {
    	if (player != null) return;
 	
    	File target = context.getLocalFile(fileName, ResourceType.MEDIA);
		if (target == null || ! target.canRead() || ! target.isFile()) { 
			log.warn("Cannot read : '{}'", fileName);
			return;
		}
		
		Media media = new Media(target.toURI().toASCIIString());
		player = new MediaPlayer(media);
		animationSceneController.getMediaView().setMediaPlayer(player);

		animationSceneController.toggleScene(animationSceneController.getScene());
		player.cycleCountProperty().set(MediaPlayer.INDEFINITE);
    	player.play();
    }
    
    private void stopAnimation() {
    	if (player == null) return;
    	
    	player.stop();
    	animationSceneController.toggleScene(scoreSceneController.getScene());
    	player = null;
    }

    

	
}
