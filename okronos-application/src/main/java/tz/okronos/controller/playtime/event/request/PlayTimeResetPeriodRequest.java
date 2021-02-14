package tz.okronos.controller.playtime.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter @Setter
public class PlayTimeResetPeriodRequest extends PlayTimeRequest {
	private int cumulatedIncrement;
}
