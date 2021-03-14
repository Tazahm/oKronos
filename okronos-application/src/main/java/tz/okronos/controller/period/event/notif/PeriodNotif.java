package tz.okronos.controller.period.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.PhaseOfPlay;

@Accessors(chain = true)
@Getter @Setter
public class PeriodNotif {
	private PhaseOfPlay phaseOfPlay;
	private int phaseDuration;
	private int period;
	private boolean periodEnd;
	private int previousDuration;
    private boolean isPhaseIncAllowed;
    private boolean isPhaseDecAllowed;
}
