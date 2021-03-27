package tz.okronos.controller.match.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.okronos.core.SimpleLateralizedPair;


@Setter @Getter
public class MatchDataSnapshot implements MatchDataContract {
	@Setter @Getter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class  OfficialSnapshot implements OfficialContract {
		private String licence;
		private String name;
	}
	
	@Setter @Getter
	public static class GoalkeeperSwapSnapshot implements GoalkeeperSwapContract {
		private int time;
		private int sheet;
	}
	
	@Getter @Setter 
	public static class TeamDataSnapshot implements TeamDataContract {
		private int timeoutPeriod1;
		private int timeoutPeriod2;
		@JacksonXmlElementWrapper(localName = "goalkeeperSwaps")
		@JacksonXmlProperty(localName = "goalkeeperSwap")
		private GoalkeeperSwapSnapshot[] goalkeeperSwaps
			= { new GoalkeeperSwapSnapshot(), new GoalkeeperSwapSnapshot(), new GoalkeeperSwapSnapshot() };
		
		@SuppressWarnings("unchecked")
		@Override
		public GoalkeeperSwapSnapshot getGoalkeeperSwaps(int index) {
			return goalkeeperSwaps[index];
		}
	}
	
	private int matchNumber;
	private String location;
	private String competition;
	private String group;
	private String date;
	private int beginTime;
	private int endTime;
	private boolean extension;
	private boolean reservesBeforeMatch;
	private boolean claim;
	private boolean incidentReport;
	private SimpleLateralizedPair<TeamDataSnapshot> team 
		= new SimpleLateralizedPair<>(new TeamDataSnapshot(), new TeamDataSnapshot());
	private OfficialSnapshot referee1 = OfficialSnapshot.builder().build();
	private OfficialSnapshot referee2 =  OfficialSnapshot.builder().build();
	private OfficialSnapshot marker =  OfficialSnapshot.builder().build();
	private OfficialSnapshot chrono =  OfficialSnapshot.builder().build();
	
	
	public static MatchDataSnapshot of(MatchDataContract input) {
		MatchDataSnapshot output = new MatchDataSnapshot();
		MatchDataContract.copy(output, input);
		return output;
	}
}
