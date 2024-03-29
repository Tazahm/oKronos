package tz.okronos.controller.team.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.team.event.TeamNameEvent;

@Accessors(chain = true)
@Getter @Setter
public class TeamNameModificationRequest extends TeamNameEvent {
}
