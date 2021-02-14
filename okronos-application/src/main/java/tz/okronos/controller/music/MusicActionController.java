package tz.okronos.controller.music;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import tz.okronos.controller.music.event.request.MusicStartRequest;
import tz.okronos.controller.music.event.request.MusicStopRequest;
import tz.okronos.core.AbstractController;

/**
 * Allows music tracks start and stop.
 */
@Component
public class MusicActionController extends AbstractController {
	private MediaPlayer musicPlayer;
	
    @PostConstruct 
    public void init()  {
    	context.registerEventListener(this);
	}
	
	@Subscribe public void onMusicStartRequest(MusicStartRequest request) {
		Media track = new Media(request.getUrl().toExternalForm());
		musicPlayer = new MediaPlayer(track);
    	musicPlayer.cycleCountProperty().set(MediaPlayer.INDEFINITE);
    	musicPlayer.play();
  	}
	
	@Subscribe public void onMusicStopRequest(MusicStopRequest request) {
		if (musicPlayer == null) return;
		musicPlayer.stop();
		musicPlayer = null;
  	}
}
