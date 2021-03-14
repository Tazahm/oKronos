package tz.okronos.controller.period;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.period.event.notif.PeriodDurationNotif;
import tz.okronos.controller.period.event.notif.PeriodEndNotif;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.period.event.notif.PeriodNotif;
import tz.okronos.controller.period.event.request.PeriodCurrentDurationModifiationcRequest;
import tz.okronos.controller.period.event.request.PeriodDecrementRequest;
import tz.okronos.controller.period.event.request.PeriodDurationsModificationRequest;
import tz.okronos.controller.period.event.request.PeriodIncrementRequest;
import tz.okronos.controller.period.event.request.PeriodPhaseRequest;
import tz.okronos.controller.period.model.PeriodModel;
import tz.okronos.controller.period.model.PeriodReport;
import tz.okronos.controller.playtime.event.notif.PlayTimeChangeNotif;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.PhaseOfPlay;
import tz.okronos.core.PlayPosition;


@Component
public class PeriodModelController extends AbstractModelController<PeriodModel> {
	public static final String ReportId = "period";
	
    @Autowired @Qualifier("forwardTimeProperty")
    private ReadOnlyIntegerProperty forwardTimeProperty;
    @Autowired @Qualifier("playTimeRunningProperty")
    private ReadOnlyBooleanProperty playTimeRunningProperty;
    @Autowired @Qualifier("cumulativeTimeProperty")
    private ReadOnlyIntegerProperty cumulativeTimeProperty;
    private @Autowired PeriodModel periodModel;
    
    private boolean isPhaseIncremented = false;
	private boolean isPhaseDecremented = false;
	private int previousPhaseDuration;
	private PlayPosition requester;
	

	private class PhaseLabelBinding extends StringBinding {
		public PhaseLabelBinding(Observable... dependancies)
        {
            super.bind(dependancies);
        }

        @Override
        protected String computeValue() {
 			return computePhaseLabelValue();
        }
    };

    private class PhaseOfPlayListener implements ChangeListener<PhaseOfPlay>  {

		@Override
		public void changed(ObservableValue<? extends PhaseOfPlay> observable, PhaseOfPlay oldValue,
				PhaseOfPlay newValue) {
			synchronizePhaseDuration();
			
			PeriodModificationNotif notif = buildPhaseNotif(new PeriodModificationNotif());
			notif.setPreviousPhaseOfPlay(oldValue);
			notif.setPreviousDuration(previousPhaseDuration);
			notif.setIncremented(isPhaseIncremented);
			notif.setDecremented(isPhaseDecremented);
			notif.setCumulativeTime(cumulativeTimeProperty.get());
			notif.setRequester(requester);
	    	context.postEvent(notif);
	    	isPhaseIncremented = false;
	    	isPhaseDecremented = false;
	    	requester = null;
		}
    }
    
    
    private class PhaseDurationListener implements ChangeListener<Number>  {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			PeriodDurationNotif phaseEvent = buildPhaseNotif(new PeriodDurationNotif());
			phaseEvent.setPreviousDuration(oldValue.intValue());
	    	context.postEvent(phaseEvent);
		}
    }
    
    public PeriodModelController() {
	}
	
	@PostConstruct 
	public void init() {
		periodModel.getPeriodLabelWrapper().bind(new PhaseLabelBinding(periodModel.getPhaseWrapper(), periodModel.getPeriodCountWrapper()));
		// Cannot use binding : PhaseDuration shall be computed before phase modification
		// phaseDurationWrapper.bind(new PhaseDurationBinding());
		periodModel.getPhaseDurationWrapper().addListener(new PhaseDurationListener());
		periodModel.getPhaseWrapper().addListener(new PhaseOfPlayListener());

		// Read configuration properties
		periodModel.getPlayTimeDurationWrapper().set(context.getIntProperty("playTimeDuration", 25));
		periodModel.getHalfTimeDurationWrapper().set(context.getIntProperty("halfTimeDuration", 10));
		periodModel.getTimeoutDurationWrapper().set(context.getIntProperty("timeoutDuration", 1));
		periodModel.getWarmupDurationWrapper().set(context.getIntProperty("warmupDuration", 10));	

		context.registerEventListener(this);
	}
  	
	

  	@Subscribe public void onPeriodPhaseRequest(PeriodPhaseRequest request) {
  		requester = request.getRequester();
  		periodModel.getPhaseWrapper().set(request.getNewPhase());
  	}
	
  	@Subscribe public void onPeriodIncrementRequest(PeriodIncrementRequest request) {
  		incPhaseOfPlay();
  	}
	
  	@Subscribe public void onPeriodDecrementRequest(PeriodDecrementRequest request) {
		decPhaseOfPlay();
  	}
	
  	@Subscribe public void onPeriodCurrentDurationModifiationcRequest(PeriodCurrentDurationModifiationcRequest request) {
  		changeCurrentPhaseDuration(request.getDuration());
  	}
  	
  	@Subscribe public void onPeriodDurationsModificationRequest(PeriodDurationsModificationRequest request) {
  		changeDurations(request);
  	}

	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}
	
  	@Subscribe public void onPlayTimeChangeNotif(PlayTimeChangeNotif notif) {  		
  		if (notif.isRunning() && notif.getPeriodTime() == periodModel.getPhaseDurationWrapper().get() * 60) {
  			context.postEvent(buildPhaseNotif(new PeriodEndNotif()));
  		}
  	}
	
 	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! ReportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	private void buildReport(ReportBuildRequest request) {
		PeriodReport content = new PeriodReport();
		content.setPhase(periodModel.getPhaseWrapper().get());
		content.setPlayTimeDuration(periodModel.getPlayTimeDurationWrapper().get());
		content.setHalfTimeDuration(periodModel.getHalfTimeDurationWrapper().get());
		content.setTimeoutDuration(periodModel.getTimeoutDurationWrapper().get());
		content.setWarmupDuration(periodModel.getWarmupDurationWrapper().get());
		content.setPhaseDuration(periodModel.getPhaseDurationWrapper().get());
		content.setPeriodCount(periodModel.getPeriodCountWrapper().get());

		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(ReportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

	private void reinit(ReportReinitRequest request) {
		PeriodReport snapshot = (PeriodReport) request.getContent();
		
		periodModel.getPhaseWrapper().set(snapshot.getPhase());
		periodModel.getPlayTimeDurationWrapper().set(snapshot.getPlayTimeDuration());
		periodModel.getHalfTimeDurationWrapper().set(snapshot.getHalfTimeDuration());
		periodModel.getTimeoutDurationWrapper().set(snapshot.getTimeoutDuration());
		periodModel.getWarmupDurationWrapper().set(snapshot.getWarmupDuration());
		periodModel.getPhaseDurationWrapper().set(snapshot.getPhaseDuration());
		periodModel.getPeriodCountWrapper().set(snapshot.getPeriodCount());
	}

	private void incPhaseOfPlay() {
		isPhaseIncremented = true;
		PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
		PhaseOfPlay newPhase = phase;
		if (phase == PhaseOfPlay.TIMEOUT) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.HALFTIME) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.WARMUP) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.PLAYTIME) newPhase = PhaseOfPlay.HALFTIME;
		
		if (newPhase == PhaseOfPlay.PLAYTIME) {
			periodModel.getPeriodCountWrapper().set(periodModel.getPeriodCountWrapper().get() + 1);
		}
		periodModel.getPhaseWrapper().set(newPhase);
	}
	
    private void decPhaseOfPlay() {
    	if (periodModel.getPhaseWrapper().get() == PhaseOfPlay.WARMUP) return;
    	
    	isPhaseDecremented = true;
    	PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
		PhaseOfPlay newPhase = phase;
		if (phase == PhaseOfPlay.TIMEOUT) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.HALFTIME) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.WARMUP) newPhase = PhaseOfPlay.PLAYTIME;
		else if (phase == PhaseOfPlay.PLAYTIME) {
			if (periodModel.getPeriodCountWrapper().get() == 1) newPhase = PhaseOfPlay.WARMUP;
			else newPhase = PhaseOfPlay.HALFTIME;
		}

		if (phase == PhaseOfPlay.PLAYTIME) {
			periodModel.getPeriodCountWrapper().set(periodModel.getPeriodCountWrapper().get() - 1);
		}
		periodModel.getPhaseWrapper().set(newPhase);
	}

	private void reset() {
		periodModel.getPeriodCountWrapper().set(0);
		periodModel.getPhaseWrapper().set(PhaseOfPlay.WARMUP); 
	}

    private <T extends PeriodNotif> T buildPhaseNotif(T input) {
    	input.setPeriod(periodModel.getPeriodCountWrapper().get());
    	input.setPhaseDuration(periodModel.getPhaseDurationWrapper().get());
    	input.setPhaseOfPlay(periodModel.getPhaseWrapper().get());
    	input.setPhaseIncAllowed(true);
    	input.setPhaseDecAllowed(periodModel.getPhaseWrapper().get() != PhaseOfPlay.WARMUP);

    	input.setPeriodEnd(forwardTimeProperty.get() == periodModel.getPhaseDurationWrapper().get() * 60 
    		&& playTimeRunningProperty.get());
    	return input;
    }

    private void synchronizePhaseDuration() {
    	PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
    	previousPhaseDuration = periodModel.getPhaseDurationWrapper().get();
    	int duration = phase == PhaseOfPlay.TIMEOUT ? periodModel.getTimeoutDurationWrapper().get()
				: phase == PhaseOfPlay.HALFTIME ? periodModel.getHalfTimeDurationWrapper().get()
				: phase == PhaseOfPlay.WARMUP ? periodModel.getWarmupDurationWrapper().get()
				: phase == PhaseOfPlay.PLAYTIME ? periodModel.getPlayTimeDurationWrapper().get()
				: 0;
				periodModel.getPhaseDurationWrapper().set(duration);
    }
    
 	private void changeDurations(PeriodDurationsModificationRequest request) {
 		PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
 		
 		if (request.getTimeoutDuration() >= 0) {
 			periodModel.getTimeoutDurationWrapper().set(request.getTimeoutDuration());
 		    if (phase == PhaseOfPlay.TIMEOUT) periodModel.getPhaseDurationWrapper().set(request.getTimeoutDuration());
 		}
 		if (request.getHalfTimeDuration() >= 0) {
 			periodModel.getHalfTimeDurationWrapper().set(request.getHalfTimeDuration());
 		    if (phase == PhaseOfPlay.HALFTIME) periodModel.getPhaseDurationWrapper().set(request.getHalfTimeDuration());
 		}
 		if (request.getWarmupDuration() >= 0) {
 			periodModel.getWarmupDurationWrapper().set(request.getWarmupDuration());
 		    if (phase == PhaseOfPlay.WARMUP) periodModel.getPhaseDurationWrapper().set(request.getWarmupDuration());
 		}
 		if (request.getPlayTimeDuration() >= 0) {
 			periodModel.getPlayTimeDurationWrapper().set(request.getPlayTimeDuration());
 		    if (phase == PhaseOfPlay.PLAYTIME) periodModel.getPhaseDurationWrapper().set(request.getPlayTimeDuration());
 		}
	}

	private void changeCurrentPhaseDuration(int duration) {
		if (duration <= 0) return;
		
		PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
		if (phase == PhaseOfPlay.TIMEOUT) periodModel.getTimeoutDurationWrapper().set(duration);
		else if (phase == PhaseOfPlay.HALFTIME) periodModel.getHalfTimeDurationWrapper().set(duration);
		else if (phase == PhaseOfPlay.WARMUP) periodModel.getWarmupDurationWrapper().set(duration);
		else if (phase == PhaseOfPlay.PLAYTIME) periodModel.getPlayTimeDurationWrapper().set(duration);
		
		periodModel.getPhaseDurationWrapper().set(duration);
	}
	
	// TODO parameterize
    private String computePhaseLabelValue() {
    	PhaseOfPlay phase = periodModel.getPhaseWrapper().get();
    	String txt = phase == PhaseOfPlay.TIMEOUT ? "T"
				: phase == PhaseOfPlay.HALFTIME ? "M"
				: phase == PhaseOfPlay.WARMUP ? "E"
				: Integer.toString(periodModel.getPeriodCountWrapper().get());
		return txt;
    }

	@Override
	protected PeriodModel getModel() {
		return periodModel;
	} 

}
