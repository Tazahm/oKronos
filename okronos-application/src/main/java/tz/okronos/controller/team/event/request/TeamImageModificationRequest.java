package tz.okronos.controller.team.event.request;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.team.event.TeamImageEvent;

@Accessors(chain = true)
@Getter @Setter
public class TeamImageModificationRequest extends TeamImageEvent {

	public static TeamImageModificationRequest builbRequestFromFile(File file) {
		return TeamImageEvent.fromFile(new TeamImageModificationRequest(), file);
	}
}
