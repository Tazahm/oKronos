package tz.okronos.scene.operator;


import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import tz.okronos.controller.penalty.event.request.PenaltyAddRequest;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.controller.timeout.event.request.TimeoutStartRequest;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.operator.PenaltyInputController.InputMode;

/**
 *  The controller for the buttons top pane.
 */
// @Slf4j
@Component
@Scope("prototype")
public class TopButtonsSceneController extends AbstractSceneController implements Initializable {
	
	@Autowired private OperatorSceneDelegate sceneHelper;
    @Autowired @Qualifier("correctionProperty")
	private ReadOnlyBooleanProperty correctionProperty;
    @Autowired private OperatorInputBuilder inputsBuilder;
    
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    
	@FXML private Button correctionButton;
	@FXML private ToggleButton musicLeftButton;
    @FXML private ToggleButton musicRightButton;
    @FXML private ToggleButton animationLeftButton;
    @FXML private ToggleButton animationRightButton;
    @FXML private Button addPenaltyLeft;
    @FXML private Button addPenaltyRight;
	
    private PenaltyInputController penaltyInputController;
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
	
	@FXML private void timeoutLeftAction(ActionEvent event) {
    	context.postEvent(new TimeoutStartRequest().setPosition(PlayPosition.LEFT));
    }
    
    @FXML private void timeoutRightAction(ActionEvent event) {
    	context.postEvent(new TimeoutStartRequest().setPosition(PlayPosition.RIGHT));
    }

    @FXML private void correctionAction(ActionEvent event) {
    	sceneHelper.toggleCorrectionMode();
    }
    
	@FXML private void addPenaltyRightAction(ActionEvent event) {
    	addPenaltyAction(event, PlayPosition.RIGHT);
    }
    
    @FXML private void addPenaltyLeftAction(ActionEvent event) {
    	addPenaltyAction(event, PlayPosition.LEFT);
    }
    
    @FXML private void beepAction(ActionEvent event) {
    	sceneHelper.beep();
    }
    
    private void init() throws Exception {
    	sceneHelper.buildMusicHandler("musicLeft", musicLeftButton);
    	sceneHelper.buildMusicHandler("musicRight", musicRightButton);
    	sceneHelper.buildAnimationHandler("animationLeft", animationLeftButton);
    	sceneHelper.buildAnimationHandler("animationRight", animationRightButton);
    	
    	penaltyInputController = inputsBuilder.getSingleton(PenaltyInputController.class);
    	correctionProperty.addListener(o->setCorrectionStyle(correctionButton, correctionProperty.get()));
    	
    	context.registerEventListener(this);
	}

    private void setCorrectionStyle(Control control, boolean correction) {
    	KronoHelper.setStyle(control, correction, "correction"); 
	}

    private void addPenaltyAction(ActionEvent event, PlayPosition position) {
    	penaltyInputController.setInputMode(InputMode.CREATION);
    	penaltyInputController.setPlayers(playerListProperties.getFromPosition(position));
    	PenaltyVolatile penaltyVolatile = new PenaltyVolatile(0);
    	penaltyVolatile.setTeam(position);
    	penaltyVolatile.setDuration(2);
       	penaltyInputController.setPenalty(penaltyVolatile);
    	penaltyInputController.showModal();
    	if (! penaltyInputController.isCancelled()) {
    		context.postEvent(new PenaltyAddRequest().setPenalty(
    			penaltyInputController.getPenalty()));
     	}
    }
    
}
