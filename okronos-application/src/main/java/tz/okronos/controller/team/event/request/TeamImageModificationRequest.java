package tz.okronos.controller.team.event.request;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;

@Accessors(chain = true)
@Getter @Setter
public class TeamImageModificationRequest implements Lateralized {
	private Image image;
	private PlayPosition side;
}
