package tz.okronos.scene.operator;


import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.core.KronoHelper;
import tz.okronos.scene.AbstractSceneController;

/**
 *  The controller for the buttons button pane.
 */
//@Slf4j
@Component
@Scope("prototype")
public class BottomButtonsSceneController extends AbstractSceneController implements Initializable  {

   @Autowired @Qualifier("playTimeRunningProperty")
   private ReadOnlyBooleanProperty playTimeRunningProperty;
   @Autowired private OperatorSceneDelegate sceneHelper;
   
	@FXML private ToggleButton musicBottomButton;
	@FXML private ToggleButton animationBottomButton;
	
	 
    @FXML private void onResetAction(ActionEvent event) {
    	if (playTimeRunningProperty.get()) return;
    	
    	if (KronoHelper.requestUser(context, "operator.onResetAction.title", 
    		    "operator.onResetAction.message", context.getPrimaryStage())) {
    		context.postEvent(new ResetPlayRequest());
    	}
    }
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	init();
	}
    
    private void init() {	
    	sceneHelper.buildMusicHandler("musicBottom", musicBottomButton);
    	sceneHelper.buildAnimationHandler("animationBottom", animationBottomButton);
	}	

}
