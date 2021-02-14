package tz.okronos.controller.period.event.request;


public class PeriodDurationsModificationRequest extends PeriodRequest{
	 private int timeoutDuration;
	 private int halfTimeDuration;
	 private int  warmupDuration;
	 private int playTimeDuration;
	public int getTimeoutDuration() {
		return timeoutDuration;
	}
	public PeriodDurationsModificationRequest setTimeoutDuration(int timeoutDuration) {
		this.timeoutDuration = timeoutDuration;
		return this;
	}
	public int getHalfTimeDuration() {
		return halfTimeDuration;
	}
	public PeriodDurationsModificationRequest setHalfTimeDuration(int halfTimeDuration) {
		this.halfTimeDuration = halfTimeDuration;
		return this;
	}
	public int getWarmupDuration() {
		return warmupDuration;
	}
	public PeriodDurationsModificationRequest setWarmupDuration(int warmupDuration) {
		this.warmupDuration = warmupDuration;
		return this;
	}
	public int getPlayTimeDuration() {
		return playTimeDuration;
	}
	public PeriodDurationsModificationRequest setPlayTimeDuration(int playTimeDuration) {
		this.playTimeDuration = playTimeDuration;
		return this;
	}

}
