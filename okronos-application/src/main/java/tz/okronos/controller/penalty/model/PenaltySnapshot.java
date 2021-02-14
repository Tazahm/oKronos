package tz.okronos.controller.penalty.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import tz.okronos.core.PlayPosition;


/**
 *  The static version of a penalty : its values are not supposed to change during the time.
 */
@Getter @Setter
public class PenaltySnapshot implements PenaltyContract  {
	
	private long uid;
	private int startTime;
	private int stopTime;
	private int penaltyTime;
	private int remainder;
	private int player;
	private int duration;
	private int period;
	private boolean onStoppage;
	private boolean pending;
	private boolean validated;
	private PlayPosition team;
	private String code;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Date systemDate = new Date();
	
	@Override
	public void setSystemTime(long time) {
		systemDate = new Date(time);
	}

	@Override
	public long getSystemTime() {		
		return systemDate.getTime();
	}

	public static PenaltySnapshot of(PenaltyContract prototype) {
		PenaltySnapshot snapshot = new PenaltySnapshot();
		PenaltyContract.copy(prototype, snapshot, true);
		return snapshot;
	}
}
