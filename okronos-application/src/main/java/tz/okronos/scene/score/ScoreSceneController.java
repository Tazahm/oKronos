package tz.okronos.scene.score;
 
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.animation.AnimationActionController;
import tz.okronos.controller.penalty.event.notif.PenaltyNotif;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.control.PenaltyControl;

/**
 *  Handles the display of the score stage.
 */
@Component
@Scope("prototype")
public class ScoreSceneController extends AbstractSceneController {	
    @FXML private Label clock;
    @FXML private Label period;
    @FXML private Label scoreLeft;
    @FXML private Label scoreRight;
    @FXML private PenaltyControl penaltyLeft1;
    @FXML private PenaltyControl penaltyLeft2;
    @FXML private PenaltyControl penaltyLeft3;
    @FXML private PenaltyControl penaltyLeft4;
    @FXML private PenaltyControl penaltyRight1;
    @FXML private PenaltyControl penaltyRight2;
    @FXML private PenaltyControl penaltyRight3;
    @FXML private PenaltyControl penaltyRight4;
    @FXML private Label teamLeftLabel;
    @FXML private Label teamRightLabel;
    @FXML private ImageView teamLeftImageView;
    @FXML private ImageView teamRightImageView;
    
    @Autowired @Qualifier("penaltyScoreListPropertyLateralized") 
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyScoreListProperties;
    @Autowired @Qualifier("playTimeRunningProperty") 
    private ReadOnlyBooleanProperty playTimeRunningProperty;  
    @Autowired @Qualifier("backwardTimeProperty") 
    private ReadOnlyIntegerProperty backwardTimeProperty;
    @Autowired @Qualifier("scorePropertyLateralized") 
    private SimpleLateralizedPair<ReadOnlyIntegerProperty> scoreProperties;
    @Autowired @Qualifier("teamNamePropertyLateralized") 
    private SimpleLateralizedPair<ReadOnlyStringProperty> teamNameProperties;
    @Autowired @Qualifier("periodLabelProperty") 
    private ReadOnlyStringProperty periodLabelProperty;
    @Autowired @Qualifier("teamImagePropertyLateralized") 
    private SimpleLateralizedPair<ReadOnlyObjectProperty<Image>> teamImageProperties;
    @Autowired AnimationActionController animationActionController;

    private SimpleLateralizedPair<List<PenaltyControl>> penaltyControls;
    
    
    @PostConstruct 
    public void init()  {
		penaltyControls = new SimpleLateralizedPair<>(
			Stream.of(penaltyLeft1, penaltyLeft2, penaltyLeft3, penaltyLeft4)
			    .filter(p -> p != null)
			    .collect(Collectors.toList()), 
			Stream.of(penaltyRight1, penaltyRight2, penaltyRight3, penaltyRight4)
			    .filter(p->p != null)
			    .collect(Collectors.toList()));
		
		BindingHelper.bind(clock.textProperty(), backwardTimeProperty, BindingHelper.SecondsToTime);
		BindingHelper.bind(scoreLeft.textProperty(), scoreProperties.getLeft(), BindingHelper.IntegerToString);
		BindingHelper.bind(scoreRight.textProperty(), scoreProperties.getRight(), BindingHelper.IntegerToString);	
		teamLeftLabel.textProperty().bind(teamNameProperties.getLeft());
		teamRightLabel.textProperty().bind(teamNameProperties.getRight());
		period.textProperty().bind(periodLabelProperty);
		teamLeftImageView.imageProperty().bind(teamImageProperties.getLeft());
		teamRightImageView.imageProperty().bind(teamImageProperties.getRight());
		bindScores();				
		
		context.registerEventListener(this);
	}
    
	@FXML protected void clockAction(MouseEvent event) {  
		stage.setFullScreen(! stage.isFullScreen());
    }

  	@FxSubscribe public void onPlayTimeStartOrStopNotif(PlayTimeStartOrStopNotif event) {
  		timeResumed();
  	}

 	@FxSubscribe public void onPenaltyNotif(PenaltyNotif event) {
  		bindScores();
  	}

 	@FxSubscribe public void onShutdownRequest(final ShutdownRequest event) {
		getStage().hide();
	}
  	
    private void timeResumed() {
    	if (playTimeRunningProperty.get()) clock.getStyleClass().remove("pending"); 
    	else clock.getStyleClass().add("pending");
    }

    private void bindScores() {
    	bindScores(penaltyScoreListProperties.getLeft(), penaltyControls.getLeft());
    	bindScores(penaltyScoreListProperties.getRight(), penaltyControls.getRight());
    }
    
    private void bindScores(ReadOnlyListProperty<PenaltyVolatile> penaltyList, List<PenaltyControl> controlList) {
    	for (int i = 0; i < controlList.size(); i++) {
    		PenaltyControl control = controlList.get(i);
    		PenaltyVolatile penalty = i < penaltyList.size() ? penaltyList.get(i) : null;
    			control.setPenalty(penalty);
    	}
    }
	
}
