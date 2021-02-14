package tz.okronos.controller.shutdown.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter @Setter
public class ShutdownVetoNotif {
	private String requester;
	private boolean lock;
}
