package tz.okronos.scene;

import java.io.File;

import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import tz.okronos.core.AbstractController;

/**
 *  A controller for a javafx stage.
 */
public class AbstractSceneController extends AbstractController {
	@Setter @Getter protected Stage stage;
	@Setter @Getter protected Scene scene;
	
	
	protected File fileSelect(final String initPathProp, 
    		final String initPathDef,
    		final String titleProp,
    		final boolean save,
    		final String... extensions) {
    	FileChooser fileChooser = new FileChooser();
    	
    	String histoPath = context.getProperty(initPathProp, initPathProp);
    	File histoDir = context.getFile(histoPath);
    	if (histoDir.isDirectory()) {
    	    fileChooser.setInitialDirectory(histoDir);
    	}
    	for (int i = 0, j = 1; i < extensions.length; i += 2, j +=2) {
    	fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(context.getItString(extensions[i]), extensions[j]));
    	}
    	fileChooser.setTitle(context.getItString(titleProp));
    	File file = save 
    		? fileChooser.showSaveDialog(stage)
    		: fileChooser.showOpenDialog(stage);
    	return file;
    }
}
