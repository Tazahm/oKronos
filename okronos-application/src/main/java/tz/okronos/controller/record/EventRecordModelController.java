package tz.okronos.controller.record;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.penalty.event.notif.PenaltyStartNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyStopNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.record.model.EventRecordSnapshot;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.model.EventRecordModel;
import tz.okronos.controller.score.event.notif.ScoreNotif;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.TwoSide;
import tz.okronos.event.request.ResetPlayRequest;
import tz.okronos.model.container.EventRecord;
import tz.okronos.model.container.PhaseOfPlay;


/**
 * Records the main events in order to display them on screen.
 * The event selected and the output strings are hard coded.
 */
@Configuration
public class EventRecordModelController extends AbstractModelController<EventRecordModel>  {
	public static final String ReportId = "record";
		
	@Autowired @Qualifier("teamNamePropertyTwoSide") private TwoSide<ReadOnlyStringProperty> teamNameProperties;
	@Autowired @Qualifier("phaseProperty") private ReadOnlyObjectProperty<PhaseOfPlay> phaseProperty;
	@Autowired @Qualifier("phaseDurationProperty") private ReadOnlyIntegerProperty phaseDurationProperty;
    @Autowired @Qualifier("forwardTimeProperty") private ReadOnlyIntegerProperty forwardTimeProperty;
    @Autowired @Qualifier("periodCountProperty") private ReadOnlyIntegerProperty periodCountProperty;
    @Autowired private  EventRecordModel model;
    
    
    public EventRecordModelController() {
    }
    
    @PostConstruct 
    public void init() {
		context.registerEventListener(this);
	}
    
    @Bean
    ReadOnlyListProperty<EventRecord<?>> historyListProperty() {
    	return model.getHistoryListWrapper().getReadOnlyProperty();
    }

	@FxSubscribe public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}
	
	@FxSubscribe public void onPenaltyStopNotif(final PenaltyStopNotif event) {
		penaltyStop(event);		
	}

	@FxSubscribe public void onPenaltyStartNotif(final PenaltyStartNotif event) {
  		penaltyStart(event);
  	}

	@FxSubscribe public void onScoreNotif(final ScoreNotif event) {
  		score(event);
  	}
	
	@FxSubscribe public void onPlayTimeStartOrStopNotif(final PlayTimeStartOrStopNotif event) {
		playTimeStartOrStop(event);		
	}

 	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	private void buildReport(ReportBuildRequest request) {
		EventRecordSnapshot content = new EventRecordSnapshot();
		content.setRecords(KronoHelper.translate(model.getHistoryListWrapper(), Function.identity(), null));
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(ReportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

    private <R> void add(EventRecord<R> record) {
    	record.setSystemTime(System.currentTimeMillis());
    	record.setPeriod(periodCountProperty.get());
    	record.setPlayTime(forwardTimeProperty.get());
    	
    	model.getHistoryListWrapper().add(record);
    }

	private void reset() {
		model.getHistoryListWrapper().clear();
	}

	private void penaltyStart(PenaltyStartNotif notif) {
		add(new EventRecord<PenaltyStartNotif>()
		    .setEvent(notif)
		    .setDesc(context.getItString("operator.event.penalty.begin") 
		    	+ " " + notif.getPenalty().getPlayer()));
	}

  	private void penaltyStop(PenaltyStopNotif notif) {
		add(new EventRecord<PenaltyStopNotif>()
			.setEvent(notif)
			.setDesc(context.getItString("operator.event.penalty.end") 
				+ " " + notif.getPenalty().getPlayer()));
	}

  	private void score(ScoreNotif notif) {
	    if (notif.isReset()) return;
	    
	    ScoreSnapshot mark = notif.getMark();
    	int diff = notif.getScore() - notif.getPreviousScore();
    	String suffix = (diff == 1) ? "goal" : (diff == -1) ? "goalRemoval" : "goalUndef";
    	String msg = context.getItString("operator.event.score." + suffix);
    	String scorers = Stream.of((mark.getScorer()), mark.getAssist1(), mark.getAssist2())
    	    .filter(i -> i >= 0)
    	    .map(i -> Integer.toString(i))
    	    .collect(Collectors.joining("/"));
    	if (scorers.length() > 0) scorers = " (" + scorers + ")";
  		msg = msg + " " + teamNameProperties.getPosition(mark.getTeam()).get() + scorers;
  		
  		add(new EventRecord<ScoreNotif>()
  				.setEvent(notif)
  				.setDesc(msg));
	}

  	private void playTimeStartOrStop(PlayTimeStartOrStopNotif notif) {
  		String msg = null;
 		if (notif.getPeriodTime() == 0 && notif.isRunning())	{			
   			msg = context.getItString("operator.event.phase.begin") + " " + PhaseToString(phaseProperty.get());
		} else {
			int periodSeconds =  phaseDurationProperty.get() * 60;
			if (notif.getPeriodTime() == periodSeconds && ! notif.isRunning())	{				
	   			msg = context.getItString("operator.event.phase.end") + " " + PhaseToString(phaseProperty.get());
			}
		}
 		
 		if (msg != null) {
 			add(new EventRecord<PlayTimeStartOrStopNotif>()
 	  				.setEvent(notif)
 	  				.setDesc(msg));
 		}
  	}
  	
	private String PhaseToString(PhaseOfPlay phase) {
		return context.getItString("operator.event.phase." + phase.name().toLowerCase());
	}

	@Override
	protected EventRecordModel getModel() {
		return model;
	}

}
