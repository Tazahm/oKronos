package tz.okronos.controller.score.model;

import java.util.concurrent.atomic.AtomicLong;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import tz.okronos.core.PlayPosition;

/**
 *  Records a goal. Contains the time and the scorers as properties, the value
 *  of this properties can evolves along the time.
 */
public class ScoreVolatile implements Comparable<ScoreVolatile>, ScoreContract {
	private static AtomicLong generator = new AtomicLong(0);
	
	private long uid;	
	private PlayPosition team;
	private SimpleIntegerProperty timeProperty;
	private SimpleLongProperty systemTimeProperty;
	private SimpleIntegerProperty periodProperty;
	private SimpleIntegerProperty scorerProperty;
	private SimpleIntegerProperty assist1Property;
	private SimpleIntegerProperty assist2Property;

	
	public static void resetUid() {
		generator.set(0);
	}
	
	public ScoreVolatile(long uid) {
		this.uid = uid;
		team = PlayPosition.LEFT;
		timeProperty = new SimpleIntegerProperty();
		periodProperty = new SimpleIntegerProperty();
		systemTimeProperty = new SimpleLongProperty();
		scorerProperty = new SimpleIntegerProperty(Integer.MIN_VALUE);
		assist1Property = new SimpleIntegerProperty(Integer.MIN_VALUE);
		assist2Property = new SimpleIntegerProperty(Integer.MIN_VALUE);
	}
	
	public ScoreVolatile() {
		this(generator.incrementAndGet());
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public PlayPosition getTeam() {
		return team;
	}

	public void setTeam(PlayPosition team) {
		this.team = team;
	}

	public int getTime() {
		return timeProperty.get();
	}

	public void setTime(int time) {
		this.timeProperty.set(time);
	}

	public int getScorer() {
		return scorerProperty.get();
	}

	public void setScorer(int scorer) {
		this.scorerProperty.set(scorer);
	}

	public int getAssist1() {
		return assist1Property.get();
	}

	public void setAssist1(int assist1) {
		this.assist1Property.set(assist1);
	}

	public int getAssist2() {
		return assist2Property.get();
	}

	public void setAssist2(int assist2) {
		this.assist2Property.set(assist2);
	}

	public int getPeriod() {
		return periodProperty.get();
	}

	public void setPeriod(int assist2) {
		this.periodProperty.set(assist2);
	}

	@Override
	public void setSystemTime(long time) {
		systemTimeProperty.set(time);
	}

	@Override
	public long getSystemTime() {
		return systemTimeProperty.get();
	}

	public SimpleIntegerProperty timeProperty() {
		return timeProperty;
	}

	public SimpleIntegerProperty periodProperty() {
		return periodProperty;
	}

	public SimpleIntegerProperty scorerProperty() {
		return scorerProperty;
	}

	public SimpleIntegerProperty assist1Property() {
		return assist1Property;
	}

	public SimpleIntegerProperty assist2Property() {
		return assist2Property;
	}
	
	public SimpleLongProperty systemTimeProperty() {
		return systemTimeProperty;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assist1Property == null) ? 0 : assist1Property.get());
		result = prime * result + ((assist2Property == null) ? 0 : assist2Property.get());
		result = prime * result + ((scorerProperty == null) ? 0 : scorerProperty.get());
		result = prime * result + ((team == null) ? 0 : team.getIndex());
		result = prime * result + ((timeProperty == null) ? 0 : timeProperty.get());
		result = prime * result + (int) (uid ^ (uid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return this.getUid() == ((ScoreVolatile) obj).getUid();
	}

	@Override
	public int compareTo(ScoreVolatile scoreVolatile) {
		if (this == scoreVolatile)
			return 0;
		if (scoreVolatile == null)
			return -1;
		int res = this.getTime() - scoreVolatile.getTime();
		if (res != 0) return res;
		
		return (int) (this.getUid() - scoreVolatile.getUid());
	}

	public static ScoreVolatile of(ScoreContract prototype) {
		ScoreVolatile scoreVolatile = new ScoreVolatile();
		ScoreContract.copy(prototype, scoreVolatile, false);
		return scoreVolatile;
	}

	public void copy(ScoreSnapshot mark) {
		ScoreContract.copy(mark, this, false);
	}

}
