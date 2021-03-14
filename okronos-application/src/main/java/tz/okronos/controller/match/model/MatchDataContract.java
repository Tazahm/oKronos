package tz.okronos.controller.match.model;

import tz.okronos.core.SimpleLateralizedPair;


public interface MatchDataContract {

	public static interface  OfficialContract {
		public String getLicence();
		public void setLicence(String licence);
		public String getName();
		public void setName(String name);
	}
	
	public static interface GoalkeeperSwapContract {
		public int getTime();
		public void setTime(int time);
		public int getSheet();
		public void setSheet(int sheet);
	}
	
	public static interface TeamDataContract {
		public int getTimeoutPeriod1();
		public void setTimeoutPeriod1(int timeoutPeriod1);
		public int getTimeoutPeriod2();
		public void setTimeoutPeriod2(int timeoutPeriod2);
		public <S extends GoalkeeperSwapContract> S getGoalkeeperSwaps(int index);
	}
	
	public int getMatchNumber();
	public void setMatchNumber(int matchNumber);
	public String getLocation();
	public void setLocation(String location);
	public String getCompetition();
	public void setCompetition(String competition);
	public String getGroup();
	public void setGroup(String group);
	public String getDate();
	public void setDate(String date);
	public int getBeginTime();
	public void setBeginTime(int beginTime);
	public int getEndTime();
	public void setEndTime(int endTime);
	public boolean isExtension();
	public void setExtension(boolean extension);
	public boolean isReservesBeforeMatch();
	public void setReservesBeforeMatch(boolean reservesBeforeMatch);
	public boolean isClaim();
	public void setClaim(boolean claim);
	public boolean isIncidentReport();
	public void setIncidentReport(boolean incidentReport);
	public <T extends TeamDataContract> SimpleLateralizedPair<T> getTeam();
//	public <T extends TeamDataContract> LateralizedPairReadOnlyWrapper<T> getTeam();
	public <F extends OfficialContract> F getReferee1();
	public <F extends OfficialContract> F getReferee2();
	public <F extends OfficialContract> F getMarker();
	public <F extends OfficialContract> F getChrono();
	
	
	public static void copy(MatchDataContract dst, MatchDataContract src) {
		dst.setBeginTime(src.getBeginTime());
		dst.setEndTime(src.getEndTime());
		dst.setDate(src.getDate());
		dst.setMatchNumber(src.getMatchNumber());
		dst.setLocation(src.getLocation());
		dst.setCompetition(src.getCompetition());
		dst.setGroup(src.getGroup()); 
		dst.setExtension(src.isExtension());
		dst.setReservesBeforeMatch(src.isReservesBeforeMatch());
		dst.setClaim(src.isClaim()); 
		dst.setIncidentReport(src.isIncidentReport());
		copyOfficial(dst.getReferee1(), src.getReferee1());
		copyOfficial(dst.getReferee2(), src.getReferee2());
		copyOfficial(dst.getMarker(), src.getMarker());
		copyOfficial(dst.getChrono(), src.getChrono());
		copyTeam(dst.getTeam().getLeft(), src.getTeam().getLeft());
		copyTeam(dst.getTeam().getRight(), src.getTeam().getRight());
	}

	private static void copyTeam(TeamDataContract dst, TeamDataContract src) {
		dst.setTimeoutPeriod1(src.getTimeoutPeriod1());
		dst.setTimeoutPeriod2(src.getTimeoutPeriod2());
		copySwap(dst.getGoalkeeperSwaps(0), src.getGoalkeeperSwaps(0));
		copySwap(dst.getGoalkeeperSwaps(1), src.getGoalkeeperSwaps(1));
		copySwap(dst.getGoalkeeperSwaps(2), src.getGoalkeeperSwaps(2));
	}

	private static void copySwap(GoalkeeperSwapContract dst, GoalkeeperSwapContract src) {
		dst.setSheet(src.getSheet());
		dst.setTime(src.getTime());
	}

	private static void copyOfficial(OfficialContract dst, OfficialContract src) {
		dst.setLicence(src.getLicence());
		dst.setName(src.getName());
	}

}
