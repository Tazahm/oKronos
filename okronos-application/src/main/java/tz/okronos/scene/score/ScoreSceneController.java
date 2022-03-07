package tz.okronos.scene.score;
 
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.animation.AnimationActionController;
import tz.okronos.controller.penalty.event.notif.PenaltyNotif;
import tz.okronos.controller.penalty.event.notif.PenaltyScoreListNotif;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeChangeNotif;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.score.event.notif.ScoreNotif;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.controller.team.event.notif.TeamImageModificationNotif;
import tz.okronos.controller.team.event.notif.TeamNameModificationNotif;
import tz.okronos.core.KronoHelper;
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
    
    private SimpleLateralizedPair<SimpleListProperty<PenaltyVolatile>> penaltyScoreListProperties 
        = new SimpleLateralizedPair<>(KronoHelper.createListProperty(), KronoHelper.createListProperty());
    private SimpleBooleanProperty playTimeRunningProperty = new SimpleBooleanProperty(); 
    private SimpleIntegerProperty backwardTimeProperty = new SimpleIntegerProperty();
    private SimpleLateralizedPair<SimpleIntegerProperty> scoreProperties 
        = new SimpleLateralizedPair<>(new SimpleIntegerProperty(), new SimpleIntegerProperty());
    private SimpleLateralizedPair<SimpleStringProperty> teamNameProperties
        = new SimpleLateralizedPair<>(new SimpleStringProperty(), new SimpleStringProperty());
    private SimpleStringProperty periodLabelProperty = new SimpleStringProperty();
    private SimpleLateralizedPair<SimpleObjectProperty<Image>> teamImageProperties 
        = new SimpleLateralizedPair<>(new SimpleObjectProperty<Image>(), new SimpleObjectProperty<Image>());
    private SimpleLateralizedPair<List<PenaltyControl>> penaltyControls;
    @Autowired AnimationActionController animationActionController;


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
  	  	playTimeRunningProperty.set(event.isRunning());
  		timeResumed();
  	}

// 	@FxSubscribe public void onPenaltyNotif(PenaltyNotif event) {
//  		bindScores();
//  	}

 	@FxSubscribe public void onShutdownRequest(final ShutdownRequest event) {
		getStage().hide();
	}
 	
 	@FxSubscribe public void onPlayTimeChangeNotif(final PlayTimeChangeNotif event) {
 		backwardTimeProperty.set(event.getRemainingTime());
 	 }
 	
 	@FxSubscribe public void onScoreEvent(final ScoreNotif event) {
 		scoreProperties.getFromPosition(event.getSide()).set(event.getScore());
 	}
 	
	@FxSubscribe public void onTeamImageModificationNotif(TeamImageModificationNotif event) {
		teamImageProperties.getFromPosition(event.getSide()).set(event.buildImage());
	}

	@FxSubscribe public void onTeamNameModificationNotif(TeamNameModificationNotif event) {
		teamNameProperties.getFromPosition(event.getSide()).set(event.getTeamName());
	}

	@FxSubscribe public void onPeriodModificationNotif(PeriodModificationNotif event) {
		periodLabelProperty.set(event.getLabel());
	}
	
	@FxSubscribe public void onPenaltyScoreListNotif(PenaltyScoreListNotif event) {
		SimpleListProperty<PenaltyVolatile> penaltyList = penaltyScoreListProperties.getFromPosition(event.getSide());
		penaltyList.clear();
		penaltyList.addAll(event.getPenaltyList().stream().map(p->PenaltyVolatile.of(p, true)).toList());
		bindScores(penaltyList, penaltyControls.getFromPosition(event.getSide()));
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
