package tz.okronos.scene.operator.media;

import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *  A popup item that represents a media.
 */
@Accessors(chain = true)
@Setter @Getter
public class MediaItem extends MenuItem {
	private String fileName;
	
	public MediaItem(final String name) {
		super(name);
	}
}
