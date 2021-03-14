package tz.okronos.controller.match;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.Subscribe;

import lombok.extern.slf4j.Slf4j;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.match.model.MatchDataContract;
import tz.okronos.controller.match.model.MatchDataSnapshot;
import tz.okronos.controller.match.model.MatchDataVolatile;
import tz.okronos.controller.match.model.MatchDataVolatile.GoalkeeperSwap;
import tz.okronos.controller.match.model.MatchDataVolatile.Official;
import tz.okronos.controller.match.model.MatchDataVolatile.TeamData;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.PhaseOfPlay;


/**
 * Handles miscellaneous data related to the current match.
 */
@Slf4j
@Configuration
public class MatchDataModelController extends AbstractModelController<MatchDataVolatile> {
	public final static String ReportId = "match";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired private MatchDataVolatile matchData;
	
	
	@PostConstruct public void init() {
		context.registerEventListener(this);
	}
  	
 	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}

 	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! ReportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	@Subscribe public void onPeriodModificationNotif(PeriodModificationNotif notif) {
		if (! (PhaseOfPlay.TIMEOUT == notif.getPhaseOfPlay())) return;
		if (PhaseOfPlay.TIMEOUT == notif.getPreviousPhaseOfPlay()) return;
		if (notif.getRequester() == null) {
			log.warn("on timeout notif: not requester");
			return;
		}
		if (! (notif.getPeriod() == 1 || notif.getPeriod() == 2)) {
			log.info("skip timeout notif for period " + notif.getPeriod());
			return;
		}
		handleTimeoutStart(notif);
  	}

	private void handleTimeoutStart(PeriodModificationNotif notif) {
		TeamData team = matchData.getTeam().getFromPosition(notif.getRequester());
		if (notif.getPeriod() == 1) {
			if (team.getTimeoutPeriod1() != 0) {
				log.warn("Rewrite period 1");
			}
			team.setTimeoutPeriod1(notif.getCumulativeTime());
		} else {
			if (team.getTimeoutPeriod2() != 0) {
				log.warn("Rewrite period 2");
			}
			team.setTimeoutPeriod2(notif.getCumulativeTime());
		}		
	}

	private void buildReport(ReportBuildRequest request) {
		MatchDataSnapshot content = MatchDataSnapshot.of(matchData);
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(ReportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

	private void reinit(ReportReinitRequest request) {
		MatchDataSnapshot snapshot = (MatchDataSnapshot) request.getContent();
		MatchDataContract.copy(matchData, snapshot);
	}
	
	private void reset() {
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		int time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		
		matchData.setBeginTime(time);
		matchData.setEndTime(0);
		matchData.setDate(dateFormat.format(now));
		
		matchData.setMatchNumber(0);
		matchData.setLocation("");
		matchData.setCompetition("");
		matchData.setGroup(""); 
		matchData.setExtension(false);
		matchData.setReservesBeforeMatch(false);
		matchData.setClaim(false); 
		matchData.setIncidentReport(false);
		reset(matchData.getReferee1());
		reset(matchData.getReferee2());
		reset(matchData.getMarker());
		reset(matchData.getChrono());
		reset(matchData.getTeam().getLeft());
		reset(matchData.getTeam().getRight());
	}

	private void reset(TeamData team) {
		team.setTimeoutPeriod1(0);
		team.setTimeoutPeriod2(0);
		reset(team.getGoalkeeperSwaps(0));
		reset(team.getGoalkeeperSwaps(1));
		reset(team.getGoalkeeperSwaps(2));
	}

	private void reset(GoalkeeperSwap goalkeeperSwap) {
		goalkeeperSwap.setSheet(0);
		goalkeeperSwap.setTime(0);
	}

	private void reset(Official official) {
		official.setLicence("");
		official.setName("");
	}

	@Override
	protected MatchDataVolatile getModel() {
		return matchData;
	}

}
