package tz.okronos.scene.operator.media;

import java.util.List;
import javafx.scene.control.ContextMenu;

/**
 *  A popup menu that displays some media items.
 */
public class MediaPopup extends ContextMenu {
    private List<MediaItem> mediaItems;
	
	public List<MediaItem> getMediaItems() {
		return mediaItems;
	}
	
	public void setMediaItems(List<MediaItem> items) {
		mediaItems = items;
		getItems().addAll(mediaItems);
	}
}
