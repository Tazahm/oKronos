package tz.okronos.controller.score.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.score.model.ScoreSnapshot;


@Accessors(chain = true)
@Getter @Setter
public class ScoreEvent {
	private ScoreSnapshot mark;
}
