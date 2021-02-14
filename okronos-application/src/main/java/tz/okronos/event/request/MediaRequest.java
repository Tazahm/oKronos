package tz.okronos.event.request;

import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter @Setter
public class MediaRequest {
	private URL url;
}
