package tz.okronos.controller.score.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.score.event.ScoreEvent;


@Accessors(chain = true)
@Getter @Setter
public class ScoreNotif extends ScoreEvent {
	private int score;
	private int previousScore;
	private boolean scoreIncAllowed;
	private boolean scoreDecAllowed;
	private boolean reset;
}
