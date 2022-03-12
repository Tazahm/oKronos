package tz.okronos.controller.music;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.music.event.request.MusicStartRequest;
import tz.okronos.controller.music.event.request.MusicStopRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoContext.ResourceType;

/**
 * Allows music tracks start and stop.
 */
@Component
@Slf4j
public class MusicActionController extends AbstractController {
	private MediaPlayer musicPlayer;
	
    @PostConstruct 
    public void init()  {
    	context.registerEventListener(this);
	}
	
	@Subscribe public void onMusicStartRequest(MusicStartRequest request) {
		String fileName = request.getFileName();
    	File target = context.getLocalFile(fileName, ResourceType.MEDIA);
		if (target == null || ! target.canRead() || ! target.isFile()) { 
			log.warn("Cannot read : '{}'", fileName);
			return;
		}
 
		Media track = new Media(target.toURI().toASCIIString());
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
