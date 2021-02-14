package tz.okronos.scene.operator.media;

import java.io.File;
import java.net.URL;
import javafx.scene.control.MenuItem;

/**
 *  A popup item that represents a media.
 */
public class MediaItem extends MenuItem {
	private URL url;
	private File file;
	
	public MediaItem(final String name) {
		super(name);
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
