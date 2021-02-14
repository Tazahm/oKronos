package tz.okronos.controller.period.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.PlayPosition;
import tz.okronos.model.container.PhaseOfPlay;

@Accessors(chain = true)
@Getter @Setter
public class PeriodModificationNotif extends PeriodNotif {
	private PhaseOfPlay previousPhaseOfPlay;
	private int cumulativeTime;
	private PlayPosition requester;
	private boolean incremented;
	private boolean decremented;
}
