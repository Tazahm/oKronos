package tz.okronos.controller.timeout.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Getter @Setter
public class TimeoutRequest {
	private PlayPosition position;
}
