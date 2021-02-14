package tz.okronos.controller.period.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.PlayPosition;
import tz.okronos.model.container.PhaseOfPlay;

@Accessors(chain = true)
@Getter @Setter
public class PeriodPhaseRequest extends PeriodRequest{
	private  PhaseOfPlay newPhase;
	private int newTime;
	private PlayPosition requester;
}
