package tz.okronos.controller.animation.event.request;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter @Setter
public class AnimationRequest {
	private URL url;
}
