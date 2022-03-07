package tz.okronos.controller.penalty.event.notif;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Setter @Getter
public class PenaltyScoreListNotif {
	private PlayPosition side;
	private List<PenaltySnapshot> penaltyList;
}
