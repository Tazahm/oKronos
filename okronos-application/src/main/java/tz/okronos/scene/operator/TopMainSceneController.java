package tz.okronos.scene.operator;


import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.period.event.request.PeriodCurrentDurationModifiationcRequest;
import tz.okronos.controller.period.event.request.PeriodDecrementRequest;
import tz.okronos.controller.period.event.request.PeriodIncrementRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeModifyRequest;
import tz.okronos.controller.score.event.request.ScoreDecrementRequest;
import tz.okronos.controller.score.event.request.ScoreIncrementRequest;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.team.event.request.TeamImageModificationRequest;
import tz.okronos.controller.team.event.request.TeamNameModificationRequest;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.scene.AbstractSceneController;

/**
 *   The controller for the top pane that contains the clocks, score, team name and logo 
 *   as well as the phase label.
 */
@Slf4j
@Component
@Scope("prototype")
public class TopMainSceneController extends AbstractSceneController implements Initializable  {
	@Autowired private OperatorInputBuilder inputsBuilder;
	@Autowired OperatorSceneDelegate sceneHelper;
    @Autowired @Qualifier("phaseDurationProperty")
    private ReadOnlyIntegerProperty phaseDurationProperty;
    @Autowired @Qualifier("playTimeRunningProperty")
    private ReadOnlyBooleanProperty playTimeRunningProperty;
    @Autowired @Qualifier("forwardTimeProperty")
    private ReadOnlyIntegerProperty forwardTimeProperty;
    @Autowired @Qualifier("backwardTimeProperty")
    private ReadOnlyIntegerProperty backwardTimeProperty;
    @Autowired @Qualifier("cumulativeTimeProperty")
    private ReadOnlyIntegerProperty cumulativeTimeProperty;
    @Autowired @Qualifier("teamNamePropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyStringProperty> teamNameProperties;
    @Autowired @Qualifier("teamImagePropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyObjectProperty<Image>> teamImageProperties;
    @Autowired @Qualifier("scorePropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyIntegerProperty> scoreProperties;
    @Autowired @Qualifier("periodLabelProperty")
    private ReadOnlyStringProperty periodLabelProperty;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    @Autowired @Qualifier("correctionProperty")
    private ReadOnlyBooleanProperty correctionProperty;
    @Autowired @Qualifier("scoreDecAllowedPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyBooleanProperty> scoreDecAllowedProperties;
    @Autowired @Qualifier("scoreIncAllowedPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyBooleanProperty> scoreIncAllowedProperties;
    @Autowired @Qualifier("phaseDecAllowedProperty")
    private ReadOnlyBooleanProperty phaseDecAllowedProperty;
    @Autowired @Qualifier("phaseIncAllowedProperty")
    private ReadOnlyBooleanProperty phaseIncAllowedProperty;
    
    @FXML private Label teamLeftLabel;
    @FXML private Label teamRightLabel;
    @FXML private ImageView teamLeftImageView;
    @FXML private ImageView teamRightImageView;
    @FXML private Label teamLeftImageLabel;
    @FXML private Label teamRightImageLabel;
    @FXML private Label period;
    @FXML private Label scoreLeft;
    @FXML private Label scoreRight;
    @FXML private Label clock1;
    @FXML private Label clock2;
    @FXML private Label clock3;
    
	private SimpleLateralizedPair<BooleanBinding> scoreSelectBindingLateralized;
	private SimpleLateralizedPair<BooleanBinding> scoreCorrectionBindingLateralized;
	private BooleanBinding phaseSelectBinding;
	private BooleanBinding phaseCorrectionBinding;
    private SimpleLateralizedPair<Label> scoreLabels;
	
	private TimeInputController timeInputController;
	private PeriodDurationInputController durationController;
	private TeamInputController teamInputController;
	private MarkInputController markInputController;
    
    
    @Override
   	public void initialize(URL location, ResourceBundle resources) {
       	try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
   	}

    @FXML private void onPeriodAction(MouseEvent event) {    	
    	if (! phaseSelectBinding.get()) return;
    	
    	if (correctionProperty.get()) {
    		boolean proceed = forwardTimeProperty.get() == 0
    		    || KronoHelper.requestUser(
    		    	context.getItString("operator.onPeriodAction.goBackward.title"),
    				context.getItString("operator.onPeriodAction.goBackward.question") + "\n"
    		   		   + context.getItString("operator.onPeriodAction.goBackward.warning") + ".", 
    		   		context.getPrimaryStage());
    		if (proceed) {
    		    context.postEvent(new PeriodDecrementRequest());
    		}
    		toggleCorrectionMode();
    	} else { 
    		boolean proceed = forwardTimeProperty.get() >= phaseDurationProperty.get() * 60
    			||  KronoHelper.requestUser(
        		    	context.getItString("operator.onPeriodAction.goForward.title"),
        				context.getItString("operator.onPeriodAction.goForward.question") + "\n"
        		   		   + context.getItString("operator.onPeriodAction.goForward.warning") + ".", 
        		   		context.getPrimaryStage());
    		if (proceed) {
    			context.postEvent(new PeriodIncrementRequest());
    		}
    	}
    }
    
    @FXML private void scoreLeftAction(MouseEvent event) {
    	scoreAction(PlayPosition.LEFT);
    }
    
    @FXML private void scoreRightAction(MouseEvent event) {
    	scoreAction(PlayPosition.RIGHT);
    }
    
    @FXML private void durationAction(MouseEvent event) {
    	if (! correctionProperty.get()) return;
    	
    	durationController.setDuration(phaseDurationProperty.get());
    	durationController.showModal();
    	if (durationController.getDuration() > 0) {
    		context.postEvent(new PeriodCurrentDurationModifiationcRequest()
    			.setDuration(durationController.getDuration()));
    	}
    	toggleCorrectionMode();
    }
    
    @FXML private void teamLeftAction(MouseEvent event) {    	
    	teamAction(PlayPosition.LEFT);
    }
    
    @FXML private void teamRightAction(MouseEvent event) {    	
    	teamAction(PlayPosition.RIGHT);
    }
    
    @FXML private void onTeamLeftImageAction(MouseEvent event) {    	
    	onTeamImageAction(PlayPosition.LEFT);
    }
    
    @FXML private void onTeamRightImageAction(MouseEvent event) {    	
    	onTeamImageAction(PlayPosition.RIGHT);
    }
    
	@FXML private void timeModificationAction(MouseEvent event) {
    	if (! correctionProperty.get()) return;   		
		timeInputController.showModal();
		int newTime = timeInputController.getModifiedTime();
		if (! timeInputController.isCancelled() && newTime >= 0 && newTime <= phaseDurationProperty.get() * 60) {
			context.postEvent(new PlayTimeModifyRequest()
					.setPeriodTime(newTime));
		}
		toggleCorrectionMode();
    }

	@FXML private void clockAction(MouseEvent event) {    	
		sceneHelper.startOrStopPlayTime();
    }

	@FxSubscribe public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}

	private void init() throws Exception {

      	BindingHelper.bind(scoreLeft.textProperty(), scoreProperties.getLeft(), BindingHelper.IntegerToString);
      	BindingHelper.bind(scoreRight.textProperty(), scoreProperties.getRight(), BindingHelper.IntegerToString);
    	BindingHelper.bind(clock1.textProperty(), backwardTimeProperty, BindingHelper.SecondsToTime);
      	BindingHelper.bind(clock2.textProperty(), forwardTimeProperty, BindingHelper.SecondsToTime);
    	BindingHelper.bind(clock3.textProperty(), cumulativeTimeProperty, BindingHelper.SecondsToTime);

      	teamLeftLabel.textProperty().bind(teamNameProperties.getLeft());
      	teamRightLabel.textProperty().bind(teamNameProperties.getRight());
      	period.textProperty().bind(periodLabelProperty);
      	teamLeftImageView.imageProperty().bind(teamImageProperties.getLeft());
      	teamRightImageView.imageProperty().bind(teamImageProperties.getRight());

      	scoreLabels = new SimpleLateralizedPair<>(scoreLeft, scoreRight);
      	
      	setSelectabilityStyle(clock1, true);
	    setSelectabilityStyle(teamLeftLabel, true);
	    setSelectabilityStyle(teamRightLabel, true);
	    setSelectabilityStyle(teamLeftImageLabel, true);
	    setSelectabilityStyle(teamRightImageLabel, true);
	    
    	timeInputController = inputsBuilder.timeInputController();
    	durationController = inputsBuilder.periodDurationInputController();
    	teamInputController = inputsBuilder.teamInputController();
    	markInputController = inputsBuilder.getSingleton(MarkInputController.class);
    	
    	correctionProperty.addListener(o->setClockCorrectionMode(correctionProperty.get()));
	    handleScoreSelectability();
		handlePhaseSelectability();
		
		playTimeRunningProperty.addListener(o->setPendingStyle(clock1));
		setPendingStyle(clock1);
		
		context.registerEventListener(this);
	}

    private void toggleCorrectionMode() {    	
    	sceneHelper.toggleCorrectionMode();
    }

    private void setSelectabilityStyle(Control control, boolean selectable) {
    	KronoHelper.setStyle(control, selectable, "selectable");
    }
    
    private void setClockCorrectionMode(boolean mode) {    	
    	setCorrectionStyle(clock2, correctionProperty.get());
    	setCorrectionStyle(clock3, correctionProperty.get());
    	setSelectabilityStyle(clock2, correctionProperty.get());
    	setSelectabilityStyle(clock3, correctionProperty.get());
    }

    private void handleScoreSelectability() {
    	scoreSelectBindingLateralized = new SimpleLateralizedPair<BooleanBinding>();
    	scoreCorrectionBindingLateralized = new SimpleLateralizedPair<BooleanBinding>();
    	handleScoreSelectability(PlayPosition.LEFT);
    	handleScoreSelectability(PlayPosition.RIGHT);
    }
    
    private void handlePhaseSelectability() {
    	phaseSelectBinding = Bindings.not(playTimeRunningProperty).and(Bindings.or(
        	    Bindings.not(correctionProperty).and(phaseIncAllowedProperty),
        		correctionProperty.and(phaseDecAllowedProperty)
        	));
    	phaseSelectBinding.addListener(observable -> 
    	    setSelectabilityStyle(period, phaseSelectBinding.get())); 
    	
    	phaseCorrectionBinding = phaseSelectBinding.and(correctionProperty);
    	phaseCorrectionBinding.addListener(observable -> 
    		setCorrectionStyle(period, phaseCorrectionBinding.get()));
    }
    
    private void handleScoreSelectability(final PlayPosition position) {
    	BooleanBinding scoreSelectBinding = Bindings.or(
    	    Bindings.not(correctionProperty).and(scoreIncAllowedProperties.getFromPosition(position)),
    		correctionProperty.and(scoreDecAllowedProperties.getFromPosition(position))
    	);
    	scoreSelectBindingLateralized.setFromPosition(scoreSelectBinding, position);
    	scoreSelectBinding.addListener(observable -> 
	    	setSelectabilityStyle(scoreLabels.getFromPosition(position), scoreSelectBindingLateralized.getFromPosition(position).get()));
    	
    	BooleanBinding scoreCorrectionBinding = scoreSelectBinding.and(correctionProperty);
    	scoreCorrectionBindingLateralized.setFromPosition(scoreCorrectionBinding, position);
    	scoreSelectBinding.addListener(observable -> 
    		setCorrectionStyle(scoreLabels.getFromPosition(position), scoreCorrectionBindingLateralized.getFromPosition(position).get()));
    }
    
    private void setCorrectionStyle(Control control, boolean correction) {
    	KronoHelper.setStyle(control, correction, "correction"); 
	}
    
	private void scoreAction(PlayPosition position) {
		// Checks that the processing is allowed.
		if (! scoreSelectBindingLateralized.getFromPosition(position).get()) return;
		
		if (correctionProperty.get()) requestScoreDec(position);
		else requestScoreInc(position);
	}
	
	private void requestScoreInc(PlayPosition position) {
		ScoreSnapshot mark;
		if (context.getBooleanProperty("markInput", true)
				&& ! playerListProperties.getFromPosition(position).isEmpty()) {
			markInputController.clear();
			markInputController.setInputMode(MarkInputController.InputMode.CREATION);
			markInputController.setPlayers(playerListProperties.getFromPosition(position));
			markInputController.showModal();
	    	if (markInputController.isCancelled()) return;
	    	mark = markInputController.getMark();
		} else {
			mark = new ScoreSnapshot();
			mark.setScorer(Integer.MIN_VALUE);
			mark.setAssist1(Integer.MIN_VALUE);
			mark.setAssist2(Integer.MIN_VALUE);
		}
		
		mark.setTeam(position);
		context.postEvent(new ScoreIncrementRequest().setMark(mark));
	}
	
	private void requestScoreDec(PlayPosition position) {
		ScoreSnapshot mark = new ScoreSnapshot();
		mark.setTeam(position);
		
		context.postEvent(new ScoreDecrementRequest().setMark(mark));		
		toggleCorrectionMode();
	}

    private void teamAction(PlayPosition position) {
     	teamInputController.setTeamName(teamNameProperties.getFromPosition(position).get());
    	teamInputController.showModal();    	
    	if (! teamInputController.isCancelled()) {
    		context.postEvent(new TeamNameModificationRequest()
    			.setTeamName(teamInputController.getTeamName())
    			.setSide(position));
    	}
    }
    
    private void onTeamImageAction(PlayPosition position) {    	
    	FileChooser fileChooser = new FileChooser();
    	
    	List<File> paths = context.getPaths(ResourceType.IMAGES);
    	if (paths != null && ! paths.isEmpty() && paths.get(0).isDirectory()) {
    	    fileChooser.setInitialDirectory(paths.get(0));
    	}
    	fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(context.getItString("operator.filetype.imageFiles"), "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter(context.getItString("operator.filetype.allFiles"), "*.*")
            );
    	fileChooser.setTitle(context.getItString("operator.onTeamImageAction.title"));
    	File file = fileChooser.showOpenDialog(stage);
    	setTeamImage(file, position);
    }

    private void setTeamImage(File file, PlayPosition position) {
    	String url = file == null || ! file.canRead() ? null : KronoHelper.toExternalForm(file);    	    		
    	if (file != null && url == null) {
			log.warn("Cannot read image " + file.getAbsolutePath());
		} else {
			Image image = file == null ? null : new Image(url);	
			TeamImageModificationRequest request = new TeamImageModificationRequest().setImage(image);	
			context.postEvent(request.setSide(position));
		}
    }

	private void reset() {
		setTeamImage(null, PlayPosition.LEFT);
		setTeamImage(null, PlayPosition.RIGHT);
		resetTeamElements();
	}

	private void resetTeamElements() {
    	customizeTeamImage("leftTeamImage", PlayPosition.LEFT);
    	customizeTeamImage("rightTeamImage", PlayPosition.RIGHT);
    }
	
	private void customizeTeamImage(String prop, PlayPosition position) {
		String fileName = context.getProperty(prop, null);
		if (fileName == null) return;
		File file = context.getLocalFile(fileName, ResourceType.IMAGES);
		setTeamImage(file, position);
	}
		
    private void setPendingStyle(Control control) {
    	KronoHelper.setStyle(control, ! playTimeRunningProperty.get(), "pending");
    }
}
