package tz.okronos.controller.playtime.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.model.container.PhaseOfPlay;

@Accessors(chain = true)
@Getter @Setter
public class PlayTimeNotif {
	private int periodTime;
	private int remainingTime;
	private int cumulatedTime;
	private boolean isRunning;
	private PhaseOfPlay phaseOfPlay;
}
