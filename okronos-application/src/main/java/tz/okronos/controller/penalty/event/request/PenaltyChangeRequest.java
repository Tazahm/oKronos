package tz.okronos.controller.penalty.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.penalty.event.PenaltyEvent;
import tz.okronos.controller.penalty.model.PenaltySnapshot;

@Accessors(chain = true)
@Getter @Setter
public class PenaltyChangeRequest extends PenaltyEvent {
	private PenaltySnapshot newValues;
}
