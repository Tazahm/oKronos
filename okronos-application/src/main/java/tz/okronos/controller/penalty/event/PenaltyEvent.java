package tz.okronos.controller.penalty.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Getter @Setter
public class PenaltyEvent {
	private PlayPosition side;
	private PenaltySnapshot penalty;
	
	public PenaltyEvent setPenalty(PenaltySnapshot penalty) {
		this.penalty = penalty;
		side = penalty.getTeam();
		return this;
	}
}
