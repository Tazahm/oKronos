package tz.okronos.scene.operator.media;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ToggleButton;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.core.KronoContext;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.KronoContext.ResourceType;

/**
 *  Handles a media popup. A media popup is associated with a button. When the button is
 *  selected, the media popup is displayed. A media popup contains a list of media items, 
 *  if an item is selected, the media is running. When a media is running, a click on the
 *  button stops it.
 *  <p>
 *  The list of media is contains into a configuration property of type list. If this list
 *  is reduced to one element, the popup is not displayed but the media is launched directly.
 *  If the list has no elements the button is hidden.
 *  <p>
 *  Start and stop actions as well as the name of the configuration property shall be 
 *  provided by the matching setter methods. An handler shall be initialized as following.
 *  
 *  <pre>
 *    animationHandler.build().context(context)
 *        .startAction(i-&gt;startAction(i))
 *        .stopAction(i-&gt;stopAction(i))
 *        .listName(listName)
 *        .mediaButton(button)
 *        .build()
 *        .init();
 * </pre>
 */
@Builder
@Slf4j
public class MediaButtonHandler {

	@Autowired private KronoContext context;
    private MediaPopup popup;
    private List<MediaItem> mediaItems;
    private MediaItem selectedItem;
    private ToggleButton mediaButton;
    private String listName;
    private Consumer<MediaItem> startAction;
    private Consumer<MediaItem> stopAction;


	/**
	 * Initializes the handler once the setter has been used.
	 * @return
	 */
    public MediaButtonHandler init() {
    	buildMediaPopup();
    	
    	if (mediaItems.isEmpty()) {
    		KronoHelper.setManaged(mediaButton, false);    	    
    	    return this;
    	}

    	mediaButton.setOnAction(this::mediaButtonAction);
    	popup.setOnCloseRequest(h -> handleButtonSelection());
    	
		return this;
    }

    private void buildMediaPopup() {
    	popup = new MediaPopup();
    	popup.setMediaItems(buildItems(listName));
    	mediaButton.setContextMenu(popup);
    }

	private MediaItem buildMediaItem(final String name) {
		File target = context.getLocalFile(name, ResourceType.MEDIA);
		if (target == null) return null;
		if (! target.canRead() || ! target.isFile()) { 
			log.warn("Cannot read : '{}'", name);
			return null;
		}
		
		URL url = null;
		try {
			url = target.toURI().toURL();
		} catch (MalformedURLException ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}
		
		MediaItem item = new MediaItem(buildName(name));
		item.setFile(target);
		item.setUrl(url);
		return item;
	}
	
	private List<MediaItem> buildItems(String listName) {
		final EventHandler<ActionEvent> selectionHandler 
          = e -> startMedia((MediaItem) e.getSource());

		Function<String, MediaItem> transformer = this::buildMediaItem;
		Predicate<MediaItem> filter = i -> i != null;
		mediaItems = context.getPropertyList(listName, transformer, filter);
		mediaItems.forEach(i->i.setOnAction(selectionHandler));
		return mediaItems;
	}

	private String buildName(String name) {
		int pointIndex = name.lastIndexOf(".");
		if (pointIndex == -1) return name;
		if (name.length() - pointIndex > 4) return name;
		if (pointIndex < 4) return name;
		return name.substring(0, pointIndex);
	}
	
	private void mediaButtonAction(ActionEvent event) {
		if (selectedItem != null) {
			stopMedia();
			return;
		}

		if (mediaItems.size() == 1) {
			startMedia(mediaItems.get(0));
		}  else if (mediaItems.size() > 1) {
			popup.show(mediaButton, Side.TOP, 0, 0);
		}
	}
   	
	private void startMedia(MediaItem item) {
		selectedItem = item;
		startAction.accept(item);
        handleButtonSelection();
	}
	
	private void stopMedia() {
		stopAction.accept(selectedItem);
		selectedItem = null;
        handleButtonSelection();
	}

	private void handleButtonSelection() {
		mediaButton.setSelected(selectedItem != null);
	}
	
}
