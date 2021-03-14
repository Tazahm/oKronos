package tz.okronos.controller.score;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import lombok.Getter;
import tz.okronos.annotation.lateralizedbean.LateralizedBean;
import tz.okronos.annotation.lateralizedbean.LateralizedConfiguration;
import tz.okronos.annotation.lateralizedbean.LateralizedPostConstruct;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.controller.score.event.notif.ScoreNotif;
import tz.okronos.controller.score.event.request.ScoreDecrementRequest;
import tz.okronos.controller.score.event.request.ScoreDeletionRequest;
import tz.okronos.controller.score.event.request.ScoreIncrementRequest;
import tz.okronos.controller.score.event.request.ScoreModificationRequest;
import tz.okronos.controller.score.event.request.ScoreRequest;
import tz.okronos.controller.score.model.ScoreModel;
import tz.okronos.controller.score.model.ScoreReport;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.score.model.ScoreVolatile;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SideAware;


@LateralizedConfiguration
public class ScoreModelController
    extends AbstractModelController<ScoreModel>
    implements Lateralized, SideAware {
	public static final String ReportIdPreffix = "score";
	
    @Autowired @Qualifier("forwardTimeProperty") 
    private ReadOnlyIntegerProperty forwardTimeProperty;
	@Autowired @Qualifier("periodCountProperty")
	private ReadOnlyIntegerProperty periodCountProperty;

	@Getter private PlayPosition side;
	private String reportId;
	private ScoreModel model;
	
	
	public ScoreModelController() {
	}
	
	@Bean @Scope("prototype")
	protected ScoreModel scoreModel() {
		return new ScoreModel();
	}
		
	@LateralizedPostConstruct 
	public void init() {
		model = scoreModel(); 
		context.registerEventListener(this);
	}

	@LateralizedBean
    public ReadOnlyListProperty<ScoreVolatile> scoreListProperty() {
    	return model.getScoreListWrapper().getReadOnlyProperty();
    }

	@LateralizedBean
	public ReadOnlyIntegerProperty scoreProperty() {
		return model.getScoreWrapper().getReadOnlyProperty();
	}	
		
  	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}

	@Subscribe public void onScoreIncrementRequest(ScoreIncrementRequest request) {
		if (request.getMark().getTeam() != side) return;
		incScore(request);
	}	
	
	@Subscribe public void onScoreDecrementRequest(ScoreDecrementRequest request) {
		if (request.getMark().getTeam() != side) return;
		decScore(request);
	}	

	@Subscribe public void onScoreModificationRequest(ScoreModificationRequest request) {
		if (request.getMark().getTeam() != side) return;
		scoreModif(request);
	}	
	
	@Subscribe public void onScoreDeletionRequest(ScoreDeletionRequest request) {
		if (request.getMark().getTeam() != side) return;
		scoreDeletion(request);
	}
	
	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! reportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	@Override
	public void setSide(PlayPosition position) {
		this.side = position;
		reportId = ReportIdPreffix + "-" + side.toString().toLowerCase();
	}

	private void buildReport(ReportBuildRequest request) {
		ScoreReport content = new ScoreReport();
		ScoreSnapshot[] markArray = KronoHelper.toArray(model.getScoreListWrapper().get(), ScoreSnapshot::of, 
				Comparator.comparing(ScoreSnapshot::getUid), ScoreSnapshot.class);
		content.setMarks(markArray);
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(reportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}
	
	private void reinit(ReportReinitRequest request) {
		int previouScore = model.getScoreWrapper().get();

		ScoreReport snapshot = (ScoreReport) request.getContent();
		ScoreVolatile.resetUid();
		List<ScoreVolatile> scoreVolatiles = KronoHelper.fromArray(snapshot.getMarks(), ScoreVolatile::of);
		model.getScoreListWrapper().clear();
		model.getScoreListWrapper().addAll(scoreVolatiles);
		
		model.getScoreWrapper().set(scoreVolatiles.size());
		notifyScoreChange(null, previouScore, true);	
	}

	private void reset() {
		model.getScoreListWrapper().clear();
		int previouScore = model.getScoreWrapper().get();
		model.getScoreWrapper().set(0);
		notifyScoreChange(null, previouScore, true);		
	}
	
	private void incScore(ScoreRequest request) {
		ScoreVolatile scoreVolatile = ScoreVolatile.of(request.getMark());
		scoreVolatile.setTime(forwardTimeProperty.get());
		scoreVolatile.setPeriod(periodCountProperty.get());
		model.getScoreListWrapper().add(scoreVolatile);
		updateScore(scoreVolatile, 1);
	}
	
	private void decScore(ScoreRequest request) {
		if (model.getScoreWrapper().get() <= 0) return;
		ScoreVolatile scoreVolatile = model.getScoreListWrapper().remove(model.getScoreListWrapper().getSize() - 1); 
		updateScore(scoreVolatile, -1);
	}

	private void scoreModif(ScoreRequest request) {
		ScoreVolatile scoreVolatile = findMark(request.getMark().getUid());
		if (scoreVolatile == null) return;
		scoreVolatile.copy(request.getMark());
		notifyScoreChange(scoreVolatile, model.getScoreWrapper().get(), false);
	}
	
	private void scoreDeletion(ScoreRequest request) {
		ScoreVolatile scoreVolatile = findMark(request.getMark().getUid());
		if (scoreVolatile == null) return;
		model.getScoreListWrapper().remove(scoreVolatile);
		
		updateScore(scoreVolatile, -1);
	}
	
	private ScoreVolatile findMark(long uid) {
		return model.getScoreListWrapper().stream().filter(m->m.getUid() == uid).findFirst().orElse(null);
	}

	private void updateScore(ScoreVolatile scoreVolatile, int diff) {
		int previous = model.getScoreWrapper().get();
		model.getScoreWrapper().set(previous + diff);
		notifyScoreChange(scoreVolatile, previous, false);
	}
	
	private void notifyScoreChange(ScoreVolatile input, int previous, boolean reset) {
		ScoreSnapshot mark = input == null 
			?  new ScoreSnapshot()
			: ScoreSnapshot.of(input);
		mark.setTeam(side);
		
		context.postEvent(new ScoreNotif()
			.setPreviousScore(previous)
			.setScore(model.getScoreWrapper().get())
			.setReset(reset)
			.setScoreIncAllowed(true)
			.setScoreDecAllowed(model.getScoreWrapper().get() > 0)
			.setMark(mark));
	}

	@Override
	protected ScoreModel getModel() {
		return model;
	}
}
