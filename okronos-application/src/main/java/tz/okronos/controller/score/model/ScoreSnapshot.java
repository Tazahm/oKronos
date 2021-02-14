package tz.okronos.controller.score.model;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import tz.okronos.core.PlayPosition;


/**
 *  Records a goal. Contains the time and the scorers as simple variables. Used
 *  when these values are not supposed to evolve.
 */
@Setter @Getter
public class ScoreSnapshot implements ScoreContract {
	private long uid;	
	private PlayPosition team;
	private int time;
	private int period;
	private int scorer;
	private int assist1;
	private int assist2;
	@JsonFormat(shape = JsonFormat.Shape.STRING) // , pattern = "yyyy-MM-ddTHH:mm:ss:sssZ") // , timezone="Europe/Paris" timezone="UTC"
	private Date systemDate = new Date();
	
	public static ScoreSnapshot of(ScoreContract prototype) {
		ScoreSnapshot snapshot = new ScoreSnapshot();
		ScoreContract.copy(prototype, snapshot, true);
		return snapshot;
	}

	@Override
	public void setSystemTime(long time) {
		systemDate = new Date(time);
	}

	@Override
	public long getSystemTime() {		
		return systemDate.getTime();
	}
}
