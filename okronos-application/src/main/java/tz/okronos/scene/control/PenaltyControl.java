package tz.okronos.scene.control;

import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.core.property.OneWayBinging;

/**
 *  A control that displays a penalty into the score screen.
 *  Displays the number of the player in a given style (penalty-player)
 *  and the remaining time in another style (penalty-time).
 *  
 *  The minimum size of a penalty shall be computed at initialization, 
 *  see initMinimunSize(). 
 */
public class PenaltyControl extends HBox {
	private static double minimimPlayerWidth = -1;
	private static double minimimTimeWidth = -1;
	
    private Label player;
    private Label time;
    HBox playerBox;
    HBox timeBox;
    private PenaltyVolatile penalty = null;
    private OneWayBinging<Number, String> playerBinding = null;
    private OneWayBinging<Number, String> remainderBinding = null;
    
    
    /**
     * Initializes the minimum size of a control. The size shall be computed at initialization with
     * the style sheets that will applied to. This size is computed so that penalty number and time
     * are align when display vertically.
     * 
     * @param stylesheets the style sheets.
     */
    public static void initMinimunSize(List<String> stylesheets) {
    	PenaltyVolatile penaltyPattern = new PenaltyVolatile(0, 99, 2, 0, PlayPosition.LEFT, "EQU");
    	penaltyPattern.setRemainder(99 * 60 + 59);
    	PenaltyControl controlPattern = new PenaltyControl();
    	controlPattern.setPenalty(penaltyPattern);
    	
    	Group root = new Group();
        root.getStylesheets().addAll(stylesheets);
        new Scene(root);
        root.getChildren().add(controlPattern);
        root.applyCss();
        root.layout();
       
        minimimPlayerWidth = controlPattern.playerBox.getWidth();
        minimimTimeWidth = controlPattern.timeBox.getWidth();
    }

    public PenaltyControl() {
     	player = new Label();
    	player.getStyleClass().add("penalty-player");
    	playerBox = new HBox(player);
     	if (minimimPlayerWidth > 0) {
    		playerBox.minWidthProperty().set(minimimPlayerWidth);
    	}
    	
    	time = new Label();
    	time.getStyleClass().add("penalty-time");
    	timeBox = new HBox(time);
    	if (minimimTimeWidth > 0) {
    		timeBox.minWidthProperty().set(minimimTimeWidth);
    	}
 
    	setSpacing(10);
        getChildren().addAll(playerBox, timeBox);
    }

    public void setPenalty(PenaltyVolatile input) {
    	if (this.penalty != null) {
    		BindingHelper.unbind(playerBinding);
    		BindingHelper.unbind(remainderBinding);
    		playerBinding = null;
    		remainderBinding = null;
    	}
    	this.penalty = input;
    	if (penalty != null) {
    		playerBinding = BindingHelper.bind(player.textProperty(), penalty.playerProperty(), BindingHelper.IntegerToString);
    		remainderBinding = BindingHelper.bind(time.textProperty(), penalty.remainderProperty(), BindingHelper.SecondsToTime);
    	} else {
    		player.setText("");
    		time.setText("");
    	}
    }
    
    public PenaltyVolatile getPenalty() {
    	return penalty;
    }

}
