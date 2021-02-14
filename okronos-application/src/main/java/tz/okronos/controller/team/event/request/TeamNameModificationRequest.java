package tz.okronos.controller.team.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Getter @Setter
public class TeamNameModificationRequest implements Lateralized {
	private String teamName;
	private PlayPosition side;
}
