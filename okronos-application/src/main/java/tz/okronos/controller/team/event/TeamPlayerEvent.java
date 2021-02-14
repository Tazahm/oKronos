package tz.okronos.controller.team.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Getter @Setter
public class TeamPlayerEvent implements Lateralized {
	private PlayerSnapshot player;
	private PlayPosition side;
}
