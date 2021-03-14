package tz.okronos.controller.record.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *  Records an event. This class is used by the event table.
 *
 * @param <N> the type of the event.
 */
public class EventRecord<N> {	
	private long systemTime;
	private int period;
	private int playTime;
	/** A user friendly description.  */
	private String desc;
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
	private N event;
	
	public long getSystemTime() {
		return systemTime;
	}
	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getDesc() {
		return desc;
	}
	public EventRecord<N> setDesc(String desc) {
		this.desc = desc;
		return this;
	}
	public int getPlayTime() {
		return playTime;
	}
	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public N getEvent() {
		return event;
	}

	public EventRecord<N> setEvent(N notif) {
		this.event = notif;
		return this;
	}
}
