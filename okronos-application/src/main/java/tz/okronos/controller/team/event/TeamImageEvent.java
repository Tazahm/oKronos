package tz.okronos.controller.team.event;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;


@Accessors(chain = true)
@Getter @Setter
@Slf4j
public class TeamImageEvent implements Lateralized {
	private byte[] imageContent;
	private String imageName;
	private PlayPosition side;
	
    public static <T extends TeamImageEvent> T fromFile(T event, File file) {
		if (file != null && !file.canRead()) {
			log.warn("No right to read image " + file.getAbsolutePath());
			file = null;
		}

		byte[] content = null;
		if (file != null) {
			try {
				content = Files.readAllBytes(file.toPath());
			} catch (IOException ex) {
				log.warn("Cannot read image " + file.getAbsolutePath());
				file = null;
			}
		}
		
		if (file != null) {
			event.setImageContent(content);
			event.setImageName(file.getName());
		}
		return event;
    }
    
    public static <T extends TeamImageEvent> T fromEvent(T destination, TeamImageEvent origin) {
    	destination
			.setSide(origin.getSide())
			.setImageContent(origin.getImageContent())
			.setImageName(origin.getImageName());
    	return destination;
    }
    
    public Image buildImage() {
    	 Image image = null;
		
		  if (imageContent != null) {
		  final InputStream stream = new ByteArrayInputStream(imageContent);
		  image = new Image(stream);
		}
		return image;
    }
}
