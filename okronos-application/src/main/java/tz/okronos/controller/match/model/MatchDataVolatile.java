package tz.okronos.controller.match.model;

import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.TwoSide;


@Component
public class MatchDataVolatile implements MatchDataContract {
	
	public static class  Official implements OfficialContract {
		private SimpleStringProperty licence = KronoHelper.createSimpleStringProperty();
		private SimpleStringProperty name = KronoHelper.createSimpleStringProperty();

		public SimpleStringProperty licence() {
			return licence;
		}		
		public String getLicence() {
			return licence.get();
		}
		public void setLicence(String licence) {
			this.licence.set(licence);
		}
		public SimpleStringProperty name() {
			return name;
		}
		public String getName() {
			return name.get();
		}
		public void setName(String name) {
			this.name.set(name);
		}
	}
	
	public static class GoalkeeperSwap implements GoalkeeperSwapContract {
		private SimpleIntegerProperty time = new SimpleIntegerProperty();
		private SimpleIntegerProperty sheet = new SimpleIntegerProperty();

		public SimpleIntegerProperty time() {
			return time;
		}
		public int getTime() {
			return time.get();
		}
		public void setTime(int time) {
			this.time.set(time);
		}
		public SimpleIntegerProperty sheet() {
			return sheet;
		}
		public int getSheet() {
			return sheet.get();
		}
		public void setSheet(int sheet) {
			this.sheet.set(sheet);
		}
	}
	
	public static class TeamData implements TeamDataContract {
		private SimpleIntegerProperty timeoutPeriod1 = new SimpleIntegerProperty();
		private SimpleIntegerProperty timeoutPeriod2 = new SimpleIntegerProperty();
		GoalkeeperSwap[] goalkeeperSwaps
			= { new GoalkeeperSwap(), new GoalkeeperSwap(), new GoalkeeperSwap() };

		public SimpleIntegerProperty timeoutPeriod1() { return timeoutPeriod1; }
		public SimpleIntegerProperty timeoutPeriod2() { return timeoutPeriod2; }

		public int getTimeoutPeriod1() {
			return timeoutPeriod1.get();
		}
		public void setTimeoutPeriod1(int timeoutPeriod1) {
			this.timeoutPeriod1.set(timeoutPeriod1);
		}
		public int getTimeoutPeriod2() {
			return timeoutPeriod2.get();
		}
		public void setTimeoutPeriod2(int timeoutPeriod2) {
			this.timeoutPeriod2.set(timeoutPeriod2);
		}
		@SuppressWarnings("unchecked")
		public GoalkeeperSwap getGoalkeeperSwaps(int index) {
			return goalkeeperSwaps[index];
		}
	}
	
	private SimpleIntegerProperty matchNumber = new SimpleIntegerProperty();
	private SimpleStringProperty location = KronoHelper.createSimpleStringProperty();
	private SimpleStringProperty competition = KronoHelper.createSimpleStringProperty();
	private SimpleStringProperty group = KronoHelper.createSimpleStringProperty();
	private SimpleStringProperty date = KronoHelper.createSimpleStringProperty();
	private SimpleIntegerProperty beginTime = new SimpleIntegerProperty();
	private SimpleIntegerProperty endTime = new SimpleIntegerProperty();
	private SimpleBooleanProperty extension = new SimpleBooleanProperty();
	private SimpleBooleanProperty  reservesBeforeMatch = new SimpleBooleanProperty();
	private SimpleBooleanProperty claim = new SimpleBooleanProperty();
	private SimpleBooleanProperty incidentReport = new SimpleBooleanProperty();
//	private TwoSideReadOnly<TeamData> team = new TwoSide<>(new TeamData(), new TeamData()).buildReadOnlyWrapper();
	private TwoSide<TeamData> team = new TwoSide<>(new TeamData(), new TeamData());
	private Official referee1 = new Official();
	private Official referee2 = new Official();
	private Official marker = new Official();
	private Official chrono = new Official();

	public SimpleIntegerProperty matchNumber() { return matchNumber; }
	public 	SimpleStringProperty location() { return location; }
	public SimpleStringProperty competition() { return competition; }
	public SimpleStringProperty group() { return group; }
	public SimpleStringProperty date() { return date; }
	public SimpleIntegerProperty beginTime() { return beginTime; }
	public SimpleIntegerProperty endTime() { return endTime; }
	public SimpleBooleanProperty extension() { return extension; }
	public SimpleBooleanProperty reservesBeforeMatch() { return reservesBeforeMatch; }
	public SimpleBooleanProperty claim() { return claim; }
	public SimpleBooleanProperty incidentReport() { return incidentReport; }

	public int getMatchNumber() {
		return matchNumber.get();
	}
	public void setMatchNumber(int matchNumber) {
		this.matchNumber.set(matchNumber);
	}
	public String getLocation() {
		return location.get();
	}
	public void setLocation(String location) {
		this.location.set(location);
	}
	public String getCompetition() {
		return competition.get();
	}
	public void setCompetition(String competition) {
		this.competition.set(competition);
	}
	public String getGroup() {
		return group.get();
	}
	public void setGroup(String group) {
		this.group.set(group);
	}
	public String getDate() {
		return date.get();
	}
	public void setDate(String date) {
		this.date.set(date);
	}
	public int getBeginTime() {
		return beginTime.get();
	}
	public void setBeginTime(int beginTime) {
		this.beginTime.set(beginTime);
	}
	public int getEndTime() {
		return endTime.get();
	}
	public void setEndTime(int endTime) {
		this.endTime.set(endTime);
	}
	public boolean isExtension() {
		return extension.get();
	}
	public void setExtension(boolean extension) {
		this.extension.set(extension);
	}
	public boolean isReservesBeforeMatch() {
		return reservesBeforeMatch.get();
	}
	public void setReservesBeforeMatch(boolean reservesBeforeMatch) {
		this.reservesBeforeMatch.set(reservesBeforeMatch);
	}
	public boolean isClaim() {
		return claim.get();
	}
	public void setClaim(boolean claim) {
		this.claim.set(claim);
	}
	public boolean isIncidentReport() {
		return incidentReport.get();
	}
	public void setIncidentReport(boolean incidentReport) {
		this.incidentReport.set(incidentReport);
	}
	@SuppressWarnings("unchecked")
	public TwoSide<TeamData> getTeam() {
		return team;
	}
//	public TwoSideReadOnly<TeamData> getTeam() {
//		return team;
//	}
	
	@SuppressWarnings("unchecked")
	public Official getReferee1() {
		return referee1;
	}
	
	@SuppressWarnings("unchecked")
	public Official getReferee2() {
		return referee2;
	}
	
	@SuppressWarnings("unchecked")
	public Official getMarker() {
		return marker;
	}
	
	@SuppressWarnings("unchecked")
	public Official getChrono() {
		return chrono;
	}
	
	
	public static MatchDataVolatile of(MatchDataContract input) {
		MatchDataVolatile output = new MatchDataVolatile();
		MatchDataContract.copy(output, input);
		return output;
	}


}
