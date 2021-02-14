package tz.okronos.controller.animation;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Setter;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.animation.event.request.AnimationStartRequest;
import tz.okronos.controller.animation.event.request.AnimationStopRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.scene.score.AnimationSceneController;
import tz.okronos.scene.score.ScoreSceneController;

/**
 *  Displays an animation clip into the score stage.
 *  Each time the animation is requested, replaces the score scene by the animation scene and
 *  starts a new media player. Each time the animation is stop, stops the media player and restores
 *  the score scene.
 */
@Component
public class AnimationActionController extends AbstractController {
   private MediaPlayer player;
   
   @Setter private AnimationSceneController animationSceneController;
   @Setter private ScoreSceneController scoreSceneController;
   
   
   @PostConstruct 
   public void init() {
	    context.registerEventListener(this);
    }
 
 	@FxSubscribe public void onAnimationStartRequest(AnimationStartRequest event) {
  		startAnimation(event.getUrl());
  	}
  	
  	@FxSubscribe public void onAnimationStopRequest(AnimationStopRequest event) {
  		stopAnimation(event.getUrl());
  	}
  	
    public MediaPlayer getPlayer() {
    	return player;
    }
    
    private void startAnimation(URL url) {
    	if (player != null) return;
 	
		Media media =  new Media(url.toExternalForm());
		player = new MediaPlayer(media);
		animationSceneController.getMediaView().setMediaPlayer(player);

		animationSceneController.toggleScene(animationSceneController.getScene());
		player.cycleCountProperty().set(MediaPlayer.INDEFINITE);
    	player.play();
    }
    
    private void stopAnimation(URL url) {
    	if (player == null) return;
    	
    	player.stop();
    	animationSceneController.toggleScene(scoreSceneController.getScene());
    	player = null;
    }

    

	
}
