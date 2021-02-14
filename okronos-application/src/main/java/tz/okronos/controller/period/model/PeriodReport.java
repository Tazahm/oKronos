package tz.okronos.controller.period.model;

import lombok.Getter;
import lombok.Setter;
import tz.okronos.model.container.PhaseOfPlay;

@Setter @Getter
public class PeriodReport {
	private PhaseOfPlay phase;
	private int playTimeDuration;
	private int halfTimeDuration;
	private int timeoutDuration;
	private int warmupDuration;
	private int phaseDuration;
	private int periodCount;
}