package tz.okronos.controller.penalty.model;

import java.util.concurrent.atomic.AtomicLong;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import tz.okronos.core.PlayPosition;

/**
 *  The living version of a penalty : its values can change during the time.
 */
public class PenaltyVolatile implements PenaltyContract, Comparable<PenaltyVolatile> {
	private static AtomicLong generator = new AtomicLong(0);
	
	private long uid;
	private PlayPosition team;

	private SimpleIntegerProperty remainderProperty;
	private SimpleIntegerProperty playerProperty;
	private SimpleIntegerProperty durationProperty;
	private SimpleIntegerProperty penaltyTimeProperty;
	private SimpleIntegerProperty startTimeProperty;
	private SimpleIntegerProperty stopTimeProperty;
	private SimpleIntegerProperty periodProperty;
	private SimpleBooleanProperty onStoppageProperty;
	private SimpleBooleanProperty pendingProperty;
	private SimpleBooleanProperty validatedProperty;
	private SimpleStringProperty codeProperty;
	private SimpleLongProperty systemTimeProperty;
	
	public static PenaltyVolatile of(PenaltyContract prototype) {
		PenaltyVolatile penaltyVolatile = new PenaltyVolatile();
		PenaltyContract.copy (prototype, penaltyVolatile, false);
		return penaltyVolatile;
	}
	
	public static void resetUid() {
		generator.set(0);
	}
	
	public PenaltyVolatile(long uid) {
		super();
		this.uid = uid;
		systemTimeProperty = new SimpleLongProperty();
		this.remainderProperty = new SimpleIntegerProperty();
		this.playerProperty = new SimpleIntegerProperty();
		this.durationProperty = new SimpleIntegerProperty();
		this.penaltyTimeProperty = new SimpleIntegerProperty();
		this.startTimeProperty = new SimpleIntegerProperty();
		this.stopTimeProperty = new SimpleIntegerProperty();
		this.periodProperty = new SimpleIntegerProperty();
		this.onStoppageProperty = new SimpleBooleanProperty();
		this.pendingProperty = new SimpleBooleanProperty();
		this.validatedProperty = new SimpleBooleanProperty();
		this.codeProperty = new SimpleStringProperty();
	}

	public PenaltyVolatile() {
		this(generator.incrementAndGet());
	}
	
	public PenaltyVolatile(long uid, int player, int duration, int startTime, PlayPosition team, String code) {
		this(uid);
		playerProperty.set(player);
		durationProperty.set(duration);
		remainderProperty.set(duration * 60);
		startTimeProperty.set(startTime);
		stopTimeProperty.set(Integer.MIN_VALUE);
		codeProperty.set(code);
		this.team = team;
	}

	public SimpleIntegerProperty remainderProperty() {
		return remainderProperty;
	}
	
	public SimpleIntegerProperty playerProperty() {
		return playerProperty;
	}
	
	public SimpleIntegerProperty durationProperty() {
		return durationProperty;
	}
	
	public SimpleIntegerProperty periodProperty() {
		return periodProperty;
	}
	
	public SimpleIntegerProperty stopTimeProperty() {
		return stopTimeProperty;
	}
	
	public SimpleIntegerProperty startTimeProperty() {
		return startTimeProperty;
	}
	
	public SimpleIntegerProperty penaltyTimeProperty() {
		return penaltyTimeProperty;
	}
	
	
	public SimpleBooleanProperty onStoppageProperty() {
		return onStoppageProperty;
	}
	
	public SimpleBooleanProperty pendingProperty() {
		return pendingProperty;
	}
	
	public SimpleBooleanProperty validatedProperty() {
		return validatedProperty;
	}
	
	public SimpleStringProperty codeProperty() {
		return codeProperty;
	}
	
	public SimpleLongProperty systemTimeProperty() {
		return systemTimeProperty;
	}

	@Override
	public int getPlayer() {
		return playerProperty.get();
	}
	
	@Override
	public void setPlayer(int player) {
		playerProperty.set(player);
	}
	
	@Override
	public int getDuration() {
		return durationProperty.get();
	}
	
	@Override
	public void setDuration(int duration) {
		durationProperty.set(duration);
	}
	
	@Override
	public int getRemainder() {
		return remainderProperty.get();
	}
	
	@Override
	public void setRemainder(int remainder) {
		remainderProperty.set(remainder);
	}
	
	@Override
	public void setSystemTime(long time) {
		systemTimeProperty.set(time);
	}

	@Override
	public long getSystemTime() {
		return systemTimeProperty.get();
	}

	@Override
	public String toString() {
		return "PenaltyVolatile " + uid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + durationProperty.get();
		result = prime * result + startTimeProperty.get();
		result = prime * result + penaltyTimeProperty.get();
		result = prime * result + stopTimeProperty.get();
		result = prime * result + remainderProperty.get();
		result = prime * result + playerProperty.get();
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		result = prime * result + (onStoppageProperty.get() ? 1231 : 1237);
		result = prime * result + (pendingProperty.get() ? 1231 : 1237);
		result = prime * result + codeProperty.get().hashCode();
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
		return compareTo((PenaltyVolatile) obj) == 0;
	}
	
	@Override
	public int compareTo(PenaltyVolatile other) {
		if (this == other)
			return 0;
		if (other == null)
			return 1;
		int res = other.getRemainder() - getRemainder();
		if (res != 0) return res;
		return (int) (uid - other.uid);
	}
	
	@Override
	public int getPenaltyTime() {
		return penaltyTimeProperty.get();
	}

	@Override
    public void setPenaltyTime(int time) {
		this.penaltyTimeProperty.set(time);
	}

	@Override
	public int getStartTime() {
		return startTimeProperty.get();
	}

	@Override
    public void setStartTime(int time) {
		this.startTimeProperty.set(time);
	}

	@Override
	public int getStopTime() {
		return stopTimeProperty.get();
	}

	@Override
    public void setStopTime(int time) {
		this.stopTimeProperty.set(time);
	}

	@Override
	public PlayPosition getTeam() {
		return team;
	}

	@Override
	public void setTeam(PlayPosition team) {
		this.team = team;
	}

	@Override
	public boolean isOnStoppage() {
		return onStoppageProperty.get();
	}

	@Override
	public void setOnStoppage(boolean onStoppage) {
		this.onStoppageProperty.set(onStoppage);
	}

	@Override
	public boolean isPending() {
		return pendingProperty.get();
	}

	@Override
	public void setPending(boolean pending) {
		this.pendingProperty.set(pending);
	}

	@Override
	public long getUid() {
		return uid;
	}

	@Override
	public void setUid(long uid) {
		this.uid = uid;
	}

	@Override
	public boolean isValidated() {
		return validatedProperty.get();
	}

	@Override
	public void setValidated(boolean validated) {
		validatedProperty.set(validated);
	}

	@Override
	public int getPeriod() {
		return periodProperty.get();
	}

	@Override
	public void setPeriod(int period) {
		periodProperty.set(period);
		
	}

	@Override
	public String getCode() {
		return codeProperty.get();
	}

	@Override
	public void setCode(String code) {
		codeProperty.set(code);
	}

}
