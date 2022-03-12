package tz.okronos.controller.music.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter @Setter
public class MusicRequest {
	private String fileName;
}
