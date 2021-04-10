package tz.okronos.controller.penalty;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.annotation.lateralizedbean.LateralizedBean;
import tz.okronos.annotation.lateralizedbean.LateralizedConfiguration;
import tz.okronos.annotation.lateralizedbean.LateralizedPostConstruct;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.penalty.event.notif.PenaltyCreationNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyListNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyModificationNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyRemovalNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyStartNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyStopNotif;
import tz.okronos.controller.penalty.event.request.PenaltyAddRequest;
import tz.okronos.controller.penalty.event.request.PenaltyCompleteRequest;
import tz.okronos.controller.penalty.event.request.PenaltyModifRequest;
import tz.okronos.controller.penalty.event.request.PenaltyModifRequest.ModifMode;
import tz.okronos.controller.penalty.event.request.PenaltyRemoveRequest;
import tz.okronos.controller.penalty.model.PenaltyModel;
import tz.okronos.controller.penalty.model.PenaltyReport;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.playtime.event.notif.PlayTimeChangeNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PhaseOfPlay;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SideAware;

/**
 *  Handles penalties creation and modification, for one team (left or right).
 */
@LateralizedConfiguration
public class PenaltyModelController 
	extends AbstractModelController<PenaltyModel>
	implements Lateralized, SideAware {
	public static final String ReportIdPreffix = "penalty";
	
	@Autowired @Qualifier("phaseProperty") 
	private ReadOnlyObjectProperty<PhaseOfPlay> phaseProperty;
	@Autowired @Qualifier("cumulativeTimeProperty") 
	private ReadOnlyIntegerProperty cumulativeTimeProperty;
	@Autowired @Qualifier("periodCountProperty") 
	private ReadOnlyIntegerProperty periodCountProperty;
	@Autowired @Qualifier("playTimeRunningProperty") 
	private ReadOnlyBooleanProperty playTimeRunningProperty;

	private PlayPosition side;
	private String reportId = ReportIdPreffix;
	private PenaltyModel model;
	
	
	public PenaltyModelController() {	
	}
		
	@LateralizedPostConstruct
	public void init() {
		model = new PenaltyModel();
		context.registerEventListener(this);
	}
	
	@LateralizedBean
	ReadOnlyListProperty<PenaltyVolatile> penaltyHistoryListProperty() {
    	return model.penaltyHistoryListProperty();
    }
	
	@LateralizedBean
	ReadOnlyListProperty<PenaltyVolatile> penaltyLiveListProperty() {
    	return model.penaltyLiveListProperty();
    }
	
	@LateralizedBean
	ReadOnlyListProperty<PenaltyVolatile> penaltyScoreListProperty() {
    	return model.penaltyScoreListProperty();
    }

	@Override
	public PlayPosition getSide() {
		return side;
	}

	@Override
	public void setSide(PlayPosition position) {
		this.side = position;
		reportId = reportId + "-" + side.toString().toLowerCase();
	}

  	@FxSubscribe public void onPlayTimeChangeNotif(PlayTimeChangeNotif event) {
  		playTimeChanged();
  	}
  	
  	@FxSubscribe public void onPlayTimeStartOrStopNotif(PlayTimeStartOrStopNotif event) {
  		playTimeChanged();
  	}
  	
  	@Subscribe public void onPenaltyAddRequest(PenaltyAddRequest request) {
  		if (request.getPenalty().getTeam() != side) return;
  		addPenalty(request.getPenalty());
  	}
  	
  	@Subscribe public void onPenaltyRemoveRequest(PenaltyRemoveRequest request) {
  		if (request.getPenalty().getTeam() != side) return;
  		removePenalty(request.getPenalty());
  	}
  	
 	@Subscribe public void onPenaltyModifRequest(PenaltyModifRequest request) {
  		if (request.getPenalty().getTeam() != side) return;
  		modifyPenalty(request);
  	}
  	
 	@Subscribe public void onPenaltyCompleteRequest(PenaltyCompleteRequest request) {
  		if (request.getPenalty().getTeam() != side) return;
  		completePenalty(request.getNewValues());
  	}
  	
  	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}
	
 	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! reportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	private void buildReport(ReportBuildRequest request) {
		PenaltyReport content = new PenaltyReport();
		content.setPenalties(KronoHelper.toArray(model.getPenaltyMainList(), PenaltySnapshot::of, 
				Comparator.comparing(PenaltySnapshot::getUid), PenaltySnapshot.class));
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(reportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

	private void reinit(ReportReinitRequest request) {		
		PenaltyReport snapshot = (PenaltyReport) request.getContent();
		PenaltyVolatile.resetUid();
		List<PenaltyVolatile> penaltyVolatiles = KronoHelper.fromArray(snapshot.getPenalties(), PenaltyVolatile::of);
		model.getPenaltyMainList().clear();
		model.getPenaltyMainList().addAll(penaltyVolatiles);
		
		context.postEvent(new PenaltyListNotif().setSide(side));
	}

	private void reset() {
		model.getPenaltyMainList().clear();
		context.postEvent(new PenaltyListNotif().setSide(side));
	}

	private void addPenalty(PenaltySnapshot input) { 
		PenaltyVolatile penalty = PenaltyVolatile.of(input);
		penalty.setTeam(side);
		penalty.setPenaltyTime(cumulativeTimeProperty.get());
		penalty.setStartTime(penalty.isPending() ? Integer.MIN_VALUE : cumulativeTimeProperty.get());
		penalty.setStopTime(Integer.MIN_VALUE);
		penalty.setPeriod(periodCountProperty.get());
		// penalty.setRemainder(penalty.getDuration() * 60);
		updateRemainder(penalty);		
		model.getPenaltyMainList().add(penalty);
		
		context.postEvent(new PenaltyCreationNotif()
			.setPenalty(PenaltySnapshot.of(penalty)));
	}

	private void playTimeChanged() {
		if (model.getPenaltyLiveUnorderedList() == null) return;
		if (phaseProperty.get() != PhaseOfPlay.PLAYTIME) return;

		 // Update the penalty remaining time
		 boolean modified = false;
		 for (PenaltyVolatile penaltyVolatile: model.getPenaltyLiveUnorderedList()) {
			 modified = updateRemainder(penaltyVolatile) || modified;
		 }
		 
		 if (modified) {
			 context.postEvent(new PenaltyListNotif().setSide(side));
		 }
	}
	
	private boolean updateRemainder(PenaltyVolatile penaltyVolatile) {
		if (penaltyVolatile.isPending()) return false;
		
		int time = cumulativeTimeProperty.get();
		int previousRemainingTime = penaltyVolatile.getRemainder();
		int durationSeconds = penaltyVolatile.getDuration() * 60;
		int newRemainingTime = penaltyVolatile.getStartTime() - time + durationSeconds;
		newRemainingTime = newRemainingTime < 0 ? 0 : newRemainingTime;
		
		if (time == penaltyVolatile.getStartTime() && playTimeRunningProperty.get()) {
			context.postEvent(new PenaltyStartNotif()
					.setPenalty(PenaltySnapshot.of(penaltyVolatile)));
		} 

		if (newRemainingTime == previousRemainingTime) return false;
		penaltyVolatile.setRemainder(newRemainingTime);
		
		if (newRemainingTime == 0) {
		    penaltyVolatile.setStopTime(time);
		    context.postEvent(new PenaltyStopNotif()
		    	.setPenalty(PenaltySnapshot.of(penaltyVolatile)));
		}
		
		return true;
	}
	
	private PenaltyVolatile findPenalty(long uid) {
		return model.getPenaltyMainList().stream().filter(m->m.getUid() == uid).findFirst().orElse(null);
	}

	private void removePenalty(PenaltySnapshot input) {
		PenaltyVolatile penaltyVolatile = findPenalty(input.getUid());
		if (penaltyVolatile == null) return;
		if (penaltyVolatile.getRemainder() != 0) {
			penaltyVolatile.setRemainder(0);
		}
		
		model.getPenaltyMainList().remove(penaltyVolatile);
		context.postEvent(new PenaltyRemovalNotif()
			.setPenalty(PenaltySnapshot.of(penaltyVolatile)));
	}

	private void changePenalty(final PenaltyVolatile penalty, final PenaltySnapshot newValue,
			final boolean timeModif, final boolean liveModif) {
		// Reset the penalty start time if the penalty became runnable
		// so that the remaining time can be computed correctly.
		if (penalty.isPending() && ! newValue.isPending()) {
			newValue.setStartTime(cumulativeTimeProperty.get());
		}
		
		penalty.setOnStoppage(newValue.isOnStoppage());
		penalty.setPending(newValue.isPending());
		penalty.setPlayer(newValue.getPlayer());
		penalty.setCode(newValue.getCode());
		penalty.setDuration(newValue.getDuration());
		
		if (timeModif) {
			penalty.setPenaltyTime(penalty.getPenaltyTime() - penalty.getRemainder() + newValue.getRemainder());
			penalty.setStartTime(penalty.getStartTime() - penalty.getRemainder() + newValue.getRemainder());
			
			if (penalty.isValidated()) {
				penalty.setStopTime(newValue.getStartTime() + penalty.getDuration() * 60);
			}
		}

		if (! liveModif) {
			penalty.setPeriod(newValue.getPeriod());
		}
		
		updateRemainder(penalty);
	}

	private void modifyPenalty(PenaltyModifRequest request) {
		PenaltyVolatile penalty = findPenalty( request.getNewValues().getUid());
		if (penalty == null) return;
		changePenalty(penalty, request.getNewValues(), request.isTimeModification(), request.getModifMode() == ModifMode.LIVE);
		context.postEvent(new PenaltyModificationNotif()
			.setPenalty(PenaltySnapshot.of(penalty)));
	}
	
	private void completePenalty(PenaltySnapshot input) {
		PenaltyVolatile penalty = findPenalty(input.getUid());
		if (penalty == null) return;
		int oldRemainder = penalty.getRemainder();
		changePenalty(penalty, input, false, true);

		int effectiveDuration = Math.min(
				cumulativeTimeProperty.get()- penalty.getStartTime(), 
				penalty.getDuration() * 60);
		penalty.setStopTime(penalty.getStartTime() + effectiveDuration);
		penalty.setValidated(true);
		
		// Forces lists refresh.
		// TODO call listener without remove / add 
		// and refresh on validate modification (add listener).
		int idx = model.getPenaltyMainList().indexOf(penalty);
		if (idx >= 0) {
			model.getPenaltyMainList().remove(idx);
			model.getPenaltyMainList().add(idx, penalty);
		}
		
		// Notifies that the penalty has changed.
		context.postEvent(new PenaltyModificationNotif()
			.setPenalty(PenaltySnapshot.of(penalty)));

		// Notifies the penalty end if the end has not been reached previously.
		if (oldRemainder > 0) {
			context.postEvent(new PenaltyStopNotif()
			     .setPenalty(PenaltySnapshot.of(penalty)));
		}
	}

	@Override
	protected PenaltyModel getModel() {
		return model;
	}

}


