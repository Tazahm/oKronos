package tz.okronos.controller.team.event.notif;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.team.event.TeamImageEvent;

@Accessors(chain = true)
@Getter @Setter
public class TeamImageModificationNotif extends TeamImageEvent {
	
	public static TeamImageModificationNotif builFromFile(File file) {
		return TeamImageEvent.fromFile(new TeamImageModificationNotif(), file);
	}

	public static TeamImageModificationNotif buildFromEvent(TeamImageEvent event) {
		return TeamImageEvent.fromEvent(new TeamImageModificationNotif(), event);
	}
}
