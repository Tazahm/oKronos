package tz.okronos.controller.playtime.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter @Setter
public class PlayTimeModifyRequest extends PlayTimeRequest {
	private int periodTime;
	private boolean aggregate = true;
}
