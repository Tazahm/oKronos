package tz.okronos.controller.playtime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import tz.okronos.controller.period.event.notif.PeriodEndNotif;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeChangeNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.playtime.event.request.PlayTimeModifyRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeResetPeriodRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStartOrStopRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStartRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStopRequest;
import tz.okronos.controller.playtime.model.PlayTimeModel;
import tz.okronos.controller.playtime.model.PlayTimeReport;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.TimerEnabler;
import tz.okronos.event.request.ResetPlayRequest;
import tz.okronos.model.container.PhaseOfPlay;


@Component
public class PlayTimeModelController extends AbstractModelController<PlayTimeModel> {
	public static final String ReportId = "time";
	
    @Autowired @Qualifier("phaseDurationProperty")
    private ReadOnlyIntegerProperty phaseDurationProperty;
    @Autowired @Qualifier("phaseProperty")
    private ReadOnlyObjectProperty<PhaseOfPlay> phaseProperty;
    @Autowired @Qualifier("loadReportInProgressProperty")
    private ReadOnlyBooleanProperty loadReportInProgressProperty;
    @Autowired PlayTimeModel model;
    
	private TimerEnabler timer;

    
    private class TimeModificationListener implements ChangeListener<Number>  {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			context.postEvent(initTimeEvent(new PlayTimeChangeNotif()));
		}
    }
    
    public PlayTimeModelController() {
    }

	
	@PostConstruct public void init() {
		model.getBackwardTimeWrapper().bind(phaseDurationProperty.multiply(60).subtract(model.getForwardTimeWrapper()));
		model.getForwardTimeWrapper().addListener(new TimeModificationListener());
		
		context.registerEventListener(this);
	}

  	@Subscribe public void onPlayTimeStartRequest(PlayTimeStartRequest request) {
  		start();
  	}

  	@Subscribe public void onPlayTimeStopRequest(PlayTimeStopRequest request) {
  		stop();
  	}

  	@Subscribe public void onPlayTimeStartOrStopRequest(PlayTimeStartOrStopRequest request) {
  		startOrStop();
  	}

  	@Subscribe public void onPlayTimeModifyRequest(PlayTimeModifyRequest request) {
  		if (request.getPeriodTime() > phaseDurationProperty.get() * 60) return;
  		int cumul = model.getCumulativeTimeWrapper().get();
  		if (request.isAggregate() && phaseProperty.get() == PhaseOfPlay.PLAYTIME) {
  			cumul += request.getPeriodTime() - model.getForwardTimeWrapper().get();
  		}
  		setPeriodTime(request.getPeriodTime(), cumul);
  	}
  	
  	@Subscribe public void onPlayTimeResetPeriodRequest(PlayTimeResetPeriodRequest request) {
  		resetPeriodTime(request.getCumulatedIncrement());
  	}

  	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}
  	
  	@Subscribe public void onPeriodModificationNotif(PeriodModificationNotif event) {
  		phaseChanged(event);
  	}
    
    @Subscribe public void onPeriodEndNotif(PeriodEndNotif event) {
    	stop();
	}
    
 	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}
 	
	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! ReportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	private void buildReport(ReportBuildRequest request) {
		PlayTimeReport content = new PlayTimeReport();
		content.setBackwardTime(model.getBackwardTimeWrapper().get());
		content.setCumulativeTime(model.getCumulativeTimeWrapper().get());
		content.setForwardTime(model.getForwardTimeWrapper().get());
		content.setPlayTimeRunning(model.getPlayTimeRunningWrapper().get());
		
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(ReportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

	private void reinit(ReportReinitRequest request) {
		PlayTimeReport snapshot = (PlayTimeReport) request.getContent();
	
		model.getCumulativeTimeWrapper().set(snapshot.getCumulativeTime());
		model.getForwardTimeWrapper().set(snapshot.getForwardTime());
		model.getPlayTimeRunningWrapper().set(snapshot.isPlayTimeRunning());
	}
	
	private void phaseChanged(PeriodModificationNotif event) {
		if (event.getPreviousPhaseOfPlay() == PhaseOfPlay.TIMEOUT) return;
		
		Integer diffCumul = null;
		if (event.isIncremented() && event.getPreviousPhaseOfPlay() == PhaseOfPlay.PLAYTIME) {
			diffCumul = event.getPreviousDuration() * 60 - model.getForwardTimeWrapper().get();
		} else if (event.isDecremented() && event.getPreviousPhaseOfPlay() == PhaseOfPlay.PLAYTIME) {
			diffCumul = - model.getForwardTimeWrapper().get();
		} else if (event.isDecremented() && event.getPhaseOfPlay() == PhaseOfPlay.PLAYTIME) {
			diffCumul = - event.getPhaseDuration() * 60;
		}
		
		if (diffCumul != null) {
			model.getCumulativeTimeWrapper().set(model.getCumulativeTimeWrapper().get() + diffCumul);
		}
		if (! loadReportInProgressProperty.get()) {
			model.getForwardTimeWrapper().set(0);
		}
	}

	private void reset() {
		stop();
		model.getCumulativeTimeWrapper().set(0);
		model.getForwardTimeWrapper().set(0);
	}

	private void start() {
    	if (timer == null) {
    		timer = new TimerEnabler();
    	    timer.fxScheduleAtFixedRate(()->incTime(), 1000, 1000);
    	    notifyTimeStartOrStop();
    	}
    }
    
    private void stop() {
    	if (timer != null) {
    	   timer.cancel();
    	   timer = null;
    	   notifyTimeStartOrStop();
    	}
    }
    
    private void startOrStop() {
    	if (timer == null) {
    		start();
    	} else {
    		stop();
    	}
    }
    
    private void incTime() {
		int phaseSeconds =  phaseDurationProperty.get() * 60;
		if (model.getForwardTimeWrapper().get() < phaseSeconds) {
			// The cumulative time must be incremented first because of listeners
			// set on the forwardTimeWrapper: when the listeners are triggered
			// the cumulative time shall be in line with the current time.
			if (phaseProperty.get() == PhaseOfPlay.PLAYTIME) {
				model.getCumulativeTimeWrapper().set(model.getCumulativeTimeWrapper().get() + 1);
			}
			model.getForwardTimeWrapper().set(model.getForwardTimeWrapper().get() + 1);
		}
    }


    private void notifyTimeStartOrStop() {
    	model.getPlayTimeRunningWrapper().set(timer != null);
    	context.postEvent(initTimeEvent(new PlayTimeStartOrStopNotif()));
    }
    
    private <T extends PlayTimeNotif> T initTimeEvent(T event) {
    	event.setCumulatedTime(model.getCumulativeTimeWrapper().get());
    	event.setPeriodTime(model.getForwardTimeWrapper().get());
    	event.setRemainingTime(model.getBackwardTimeWrapper().get());
    	event.setRunning(model.getPlayTimeRunningWrapper().get());
    	event.setPhaseOfPlay(phaseProperty.get());
    	return event;
    }


	private void resetPeriodTime(int cumulatedIncrement) {
		stop();
		setPeriodTime(0, model.getCumulativeTimeWrapper().get() + cumulatedIncrement);
	}

	private void setPeriodTime(int newTime, int newCumulatedTime) {
		if (model.getForwardTimeWrapper().get() != newTime || model.getCumulativeTimeWrapper().get() != newCumulatedTime) {
			model.getForwardTimeWrapper().set(newTime);
			
			if (newCumulatedTime >= 0) {
				model.getCumulativeTimeWrapper().set(newCumulatedTime);
			}
		}
	}


	@Override
	protected PlayTimeModel getModel() {
		return model;
	}

}
