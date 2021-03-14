package tz.okronos.controller.timeout;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import tz.okronos.controller.period.event.notif.PeriodEndNotif;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.period.event.request.PeriodPhaseRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeModifyRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStartRequest;
import tz.okronos.controller.timeout.event.request.TimeoutStartRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.PhaseOfPlay;
import tz.okronos.core.PlayPosition;

/**
 *  Handles a timeout request.
 */
@Component
public class TimeoutActionController extends AbstractController{
	private int timeBackup;

	@Autowired @Qualifier("playTimeRunningProperty")
	private ReadOnlyBooleanProperty playTimeRunningProperty;
    @Autowired @Qualifier("forwardTimeProperty")
    private ReadOnlyIntegerProperty forwardTimeProperty;
    @Autowired @Qualifier("phaseDurationProperty")
    private ReadOnlyIntegerProperty phaseDurationProperty;
	@Autowired @Qualifier("phaseProperty")
	private ReadOnlyObjectProperty<PhaseOfPlay> phaseProperty;
	
	
	@PostConstruct 
	public void init() {
		context.registerEventListener(this);
	}

	@Subscribe public void onTimeoutStartRequest(TimeoutStartRequest request) {
  		startTimeout(request.getPosition());
  	}
	
	
	@Subscribe public void onPeriodEndNotif(PeriodEndNotif event) {
  		handlePhaseEndNotif(event);
  	}
	
	@Subscribe public void onPeriodModificationNotif(PeriodModificationNotif event) {
  		handlePhaseModificationNotif(event);
  	}
	
	public void startTimeout(PlayPosition requester) {
		if (phaseProperty.get() != PhaseOfPlay.PLAYTIME) return;
		if (playTimeRunningProperty.get()) return;
		
		timeBackup = forwardTimeProperty.get();
		context.postEvent(new PeriodPhaseRequest().setNewPhase(PhaseOfPlay.TIMEOUT)
				.setRequester(requester));
		context.postEvent(new PlayTimeStartRequest());
	}

	private void handlePhaseEndNotif(PeriodEndNotif event) {
		if (phaseProperty.get() != PhaseOfPlay.TIMEOUT) return;
		if (forwardTimeProperty.get() != phaseDurationProperty.get() * 60) return;
		
		context.postEvent(new PeriodPhaseRequest().setNewPhase(PhaseOfPlay.PLAYTIME));
		
	}

	private void handlePhaseModificationNotif(PeriodModificationNotif event) {
		if (event.getPreviousPhaseOfPlay() != PhaseOfPlay.TIMEOUT) return;
		if (event.getPhaseOfPlay() != PhaseOfPlay.PLAYTIME) return;

		context.postEvent(new PlayTimeModifyRequest().setPeriodTime(timeBackup)
				.setAggregate(false));
		timeBackup = 0;
	}

}
