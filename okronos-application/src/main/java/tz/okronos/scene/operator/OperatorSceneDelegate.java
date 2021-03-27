package tz.okronos.scene.operator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.AudioClip;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.animation.event.request.AnimationRequest;
import tz.okronos.controller.animation.event.request.AnimationStartRequest;
import tz.okronos.controller.animation.event.request.AnimationStopRequest;
import tz.okronos.controller.music.event.request.MusicStartRequest;
import tz.okronos.controller.music.event.request.MusicStopRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStartOrStopRequest;
import tz.okronos.core.KronoContext;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.scene.operator.media.MediaButtonHandler;
import tz.okronos.scene.operator.media.MediaItem;

/**
 *  Handles operation common to some controllers of the operator scene.
 */
@Component
public class OperatorSceneDelegate {
	@Autowired protected KronoContext context;
	@Autowired @Qualifier("playTimeRunningProperty")
    private ReadOnlyBooleanProperty playTimeRunningProperty;
	@Autowired
	@Qualifier("phaseDurationProperty")
	private ReadOnlyIntegerProperty phaseDurationProperty;
	@Autowired @Qualifier("forwardTimeProperty")
    private ReadOnlyIntegerProperty forwardTimeProperty;
	
	private ReadOnlyBooleanWrapper correctionPropertyWrapper = new ReadOnlyBooleanWrapper();
	private AudioClip beeper;
	
	@PostConstruct
	public void init() throws Exception {
		beeper = new AudioClip(context.getResource("beep.wav", ResourceType.CONFIG).toString());
		context.registerEventListener(this);
	}
	
	@FxSubscribe public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}
	
	@Bean
	public ReadOnlyBooleanProperty correctionProperty() {
		return correctionPropertyWrapper.getReadOnlyProperty();
	}

	public void buildMusicHandler(String listName, ToggleButton button) {
    	MediaButtonHandler.builder()
    		.context(context)
		    .startAction(i->context.postEvent(new MusicStartRequest().setUrl(i.getUrl())))
		    .stopAction(i->context.postEvent(new MusicStopRequest().setUrl(i.getUrl())))
	   	    .listName(listName)
    	    .mediaButton(button)
    	    .build()
    	    .init();
    }
	
	public void buildAnimationHandler(String listName, ToggleButton button) {
    	MediaButtonHandler.builder()
    	    .context(context)
		    .startAction(i->animationAction(i, true))
		    .stopAction(i->animationAction(i, false))
	   	    .listName(listName)
    	    .mediaButton(button)
    	    .build()
    	    .init();
    }
	
	public void beep() {
		beeper.play();	
	}
	
    private void animationAction(MediaItem item, boolean start) {
    	if (playTimeRunningProperty.get()) return;
    	
    	AnimationRequest request = start
    		? new AnimationStartRequest()
    		: new AnimationStopRequest();    		
    	context.postEvent(request.setUrl(item.getUrl()));
    }

    public void startOrStopPlayTime() {
		if (correctionPropertyWrapper.get())
			return;
		if (forwardTimeProperty.get() >= phaseDurationProperty.get() * 60)
			return;

		context.postEvent(new PlayTimeStartOrStopRequest());
	}

    public void toggleCorrectionMode() {
    	correctionPropertyWrapper.set(! correctionPropertyWrapper.get());
    }
    
    private void reset() {
		correctionPropertyWrapper.set(false);
	}
}
