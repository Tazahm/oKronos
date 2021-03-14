package tz.okronos.scene.operator;


import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.controlsfx.control.PropertySheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.animation.event.request.AnimationRequest;
import tz.okronos.controller.animation.event.request.AnimationStartRequest;
import tz.okronos.controller.animation.event.request.AnimationStopRequest;
import tz.okronos.controller.breach.model.BreachDesc;
import tz.okronos.controller.music.event.request.MusicStartRequest;
import tz.okronos.controller.music.event.request.MusicStopRequest;
import tz.okronos.controller.pdfexport.event.request.ExportPdfRequest;
import tz.okronos.controller.penalty.event.notif.PenaltyNotif;
import tz.okronos.controller.penalty.event.request.PenaltyAddRequest;
import tz.okronos.controller.penalty.event.request.PenaltyCompleteRequest;
import tz.okronos.controller.penalty.event.request.PenaltyModifRequest;
import tz.okronos.controller.penalty.event.request.PenaltyRemoveRequest;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.period.event.notif.PeriodEndNotif;
import tz.okronos.controller.period.event.notif.PeriodModificationNotif;
import tz.okronos.controller.period.event.request.PeriodCurrentDurationModifiationcRequest;
import tz.okronos.controller.period.event.request.PeriodDecrementRequest;
import tz.okronos.controller.period.event.request.PeriodIncrementRequest;
import tz.okronos.controller.playtime.event.notif.PlayTimeStartOrStopNotif;
import tz.okronos.controller.playtime.event.request.PlayTimeModifyRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeStartOrStopRequest;
import tz.okronos.controller.record.model.EventRecord;
import tz.okronos.controller.report.event.request.ReportLoadRequest;
import tz.okronos.controller.report.event.request.ReportSaveAsRequest;
import tz.okronos.controller.score.event.notif.ScoreNotif;
import tz.okronos.controller.score.event.request.ScoreDecrementRequest;
import tz.okronos.controller.score.event.request.ScoreDeletionRequest;
import tz.okronos.controller.score.event.request.ScoreIncrementRequest;
import tz.okronos.controller.score.event.request.ScoreModificationRequest;
import tz.okronos.controller.score.event.request.ScoreRequest;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.score.model.ScoreVolatile;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.controller.team.event.notif.TeamPlayerNotif;
import tz.okronos.controller.team.event.request.TeamExportRequest;
import tz.okronos.controller.team.event.request.TeamImageModificationRequest;
import tz.okronos.controller.team.event.request.TeamImportExportRequest;
import tz.okronos.controller.team.event.request.TeamImportRequest;
import tz.okronos.controller.team.event.request.TeamNameModificationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerCreationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerDeletionRequest;
import tz.okronos.controller.team.event.request.TeamPlayerModificationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerRequest;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.controller.timeout.event.request.TimeoutStartRequest;
import tz.okronos.controller.xlsexport.event.request.ExportXlsRequest;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.control.HighlightableTableCell;
import tz.okronos.scene.operator.PenaltyInputController.InputMode;
import tz.okronos.scene.operator.media.MediaButtonHandler;
import tz.okronos.scene.operator.media.MediaItem;

/**
 *  The controller that manages the operator scene. The operator scene allows all operator actions
 *  as time start / stop, penalty management, score adding...
 */
@Slf4j
public class OperatorSceneController extends AbstractSceneController {
	private PenaltyInputController penaltyInputController;
	private TimeInputController timeInputController;
	private SettingsInputController settingsInputController;
	private PeriodDurationInputController durationController;
	private TeamInputController teamInputController;
	private MarkInputController markInputController;
	private PlayerInputController playerInputController;
	private MatchDataInputController matchDataInputController;
	
    private AudioClip beeper;
    
	@Autowired private OperatorInputBuilder inputsBuilder;
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
    @Autowired @Qualifier("historyListProperty")
    private ReadOnlyListProperty<EventRecord<?>> historyListProperty;
    @Autowired @Qualifier("scoreListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<ScoreVolatile>> scoreListProperties;
    @Autowired @Qualifier("penaltyHistoryListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyHistoryListProperties;
    @Autowired @Qualifier("penaltyLiveListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyLiveListProperties;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    @Autowired @Qualifier("breachListProperty")
    private ReadOnlyListProperty<BreachDesc> breachListProperty;
    
    private SimpleLateralizedPair<SimpleBooleanProperty> scoreDecAlloweds;
    private SimpleLateralizedPair<SimpleBooleanProperty> scoreIncAlloweds;
    private SimpleBooleanProperty phaseDecAllowed;
    private SimpleBooleanProperty phaseIncAllowed;
	private SimpleBooleanProperty correctionProperty;
	private SimpleLateralizedPair<TableView<PenaltyVolatile>> penaltyTables;
	private SimpleLateralizedPair<TableView<PenaltyVolatile>> allPenaltyTables;
	private SimpleLateralizedPair<TableView<PlayerSnapshot>> playerTables;
	
	private SimpleLateralizedPair<BooleanBinding> scoreSelectBindings;
	private BooleanBinding phaseSelectBinding;
	private SimpleLateralizedPair<Label> scoreLabels;
	
    @FXML private Label clock1;
    @FXML private Label clock2;
    @FXML private Label clock3;
    @FXML private Button addPenaltyLeft;
    @FXML private Button addPenaltyRight;
    @FXML private Label period;
    @FXML private Label scoreLeft;
    @FXML private Label scoreRight;
    @FXML private Button correctionButton;
    @FXML private Label teamLeftLabel;
    @FXML private Label teamRightLabel;
    @FXML private ImageView teamLeftImageView;
    @FXML private ImageView teamRightImageView;
    @FXML private Label teamLeftImageLabel;
    @FXML private Label teamRightImageLabel;
    @FXML private ToggleButton musicBottomButton;
    @FXML private ToggleButton musicLeftButton;
    @FXML private ToggleButton musicRightButton;
    @FXML private ToggleButton animationBottomButton;
    @FXML private ToggleButton animationLeftButton;
    @FXML private ToggleButton animationRightButton;
    @FXML private TitledPane historyPane;
    @FXML private Accordion sideAccordion;
    @FXML private TableView<PenaltyVolatile> leftPenaltyTable;
    @FXML private TableView<PenaltyVolatile> rightPenaltyTable;
    @FXML private TableView<PenaltyVolatile> allPenaltyLeftTable;
    @FXML private TableView<PenaltyVolatile> allPenaltyRightTable;
    @FXML private TableView<EventRecord<?>> historyTable;
    @FXML private TableView<ScoreVolatile> scoreLeftTable;
    @FXML private TableView<ScoreVolatile> scoreRightTable;
    @FXML private TableView<PlayerSnapshot> playersLeftTable;
    @FXML private TableView<PlayerSnapshot> playersRightTable;
    @FXML private PropertySheet matchSheet;
    
    
    public OperatorSceneController() {
    }

    @PostConstruct 
    public void init() throws Exception  {
    	penaltyInputController = inputsBuilder.penaltyInputController();
    	timeInputController = inputsBuilder.timeInputController();
    	settingsInputController = inputsBuilder.settingsInputController();
    	durationController = inputsBuilder.periodDurationInputController();
    	teamInputController = inputsBuilder.teamInputController();
    	markInputController = inputsBuilder.markInputController();
    	playerInputController = inputsBuilder.playerInputController();
    	matchDataInputController = inputsBuilder.matchDataInputController();
    	
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
      	penaltyInputController.setBreaches(breachListProperty);
      	matchDataInputController.init(matchSheet);
      	
    	beeper = new AudioClip(context.getResource("beep.wav", ResourceType.CONFIG).toString());
    	historyTable.setItems(historyListProperty);
    	scoreLeftTable.setItems(scoreListProperties.getLeft());
    	scoreRightTable.setItems(scoreListProperties.getRight());
    	
    	penaltyTables = new SimpleLateralizedPair<>(leftPenaltyTable, rightPenaltyTable);
       	leftPenaltyTable.setItems(penaltyLiveListProperties.getLeft());
       	rightPenaltyTable.setItems(penaltyLiveListProperties.getRight());
       	
        allPenaltyTables = new SimpleLateralizedPair<>(allPenaltyLeftTable, allPenaltyRightTable);
       	allPenaltyLeftTable.setItems(penaltyHistoryListProperties.getLeft());
       	allPenaltyRightTable.setItems(penaltyHistoryListProperties.getRight());
       	
       	playerTables = new SimpleLateralizedPair<TableView<PlayerSnapshot>>(playersLeftTable, playersRightTable);
       	playersLeftTable.setItems(playerListProperties.getLeft());
       	playersRightTable.setItems(playerListProperties.getRight());
       	
       	correctionProperty = new SimpleBooleanProperty();
       	scoreLabels = new SimpleLateralizedPair<>(scoreLeft, scoreRight);
       	
       	customizePenaltyColumn(leftPenaltyTable);
		customizePenaltyColumn(rightPenaltyTable);
		buildMusicHandler();
		buildAnimationHandler();
		handleScoreSelectability();
		handlePhaseSelectability();
		sideAccordion.setExpandedPane(historyPane);
		settingsInputController.setLoadCallback(()->loadReportFile());
		
	    setSelectabilityStyle(clock1, true);
	    setSelectabilityStyle(period, true);
	    setSelectabilityStyle(teamLeftLabel, true);
	    setSelectabilityStyle(teamRightLabel, true);
	    setSelectabilityStyle(teamLeftImageLabel, true);
	    setSelectabilityStyle(teamRightImageLabel, true);
	    
	    getScene().addEventFilter(KeyEvent.ANY, e-> handleKeyEvent(e));
		context.registerEventListener(this);
	}
	
    @FxSubscribe public void onPhaseEndNotif(PeriodEndNotif event) {
    	handlePeriodEnd(event);
	}

	@FxSubscribe public void onPlayTimeStartOrStopNotif(final PlayTimeStartOrStopNotif event) {
		timeResumed(event);		
	}

	@FxSubscribe public void onScoreNotif(final ScoreNotif event) {
		scoreEvent(event);		
	}
  
	@FxSubscribe public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}
	
  	@FxSubscribe public void onPenaltyNotif(PenaltyNotif event) {
  		updatePenaltyTable(event);
  	}
  	
  	@FxSubscribe public void onTeamPlayerNotif(TeamPlayerNotif event) {
  		updatePlayerTable(event);
  	}
  	
	@FxSubscribe public void onPhaseModificationNotif(PeriodModificationNotif event) {
  		updatePhase(event);
  	}

	@FxSubscribe public void onShutdownRequest(final ShutdownRequest event) {
		getStage().hide();
	}
	
	@FXML private void addPenaltyRightAction(ActionEvent event) {
    	addPenaltyAction(event, PlayPosition.RIGHT);
    }
    
    @FXML private void addPenaltyLeftAction(ActionEvent event) {
    	addPenaltyAction(event, PlayPosition.LEFT);
    }
    
    @FXML private void correctionAction(ActionEvent event) {
    	toggleCorrectionMode();
    }

	@FXML private void clockAction(MouseEvent event) {  
		startOrStopPlayTime();
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
    
    @FXML private void beepAction(ActionEvent event) {
        beeper.play();
    }
    
    @FXML private void timeoutLeftAction(ActionEvent event) {
    	context.postEvent(new TimeoutStartRequest().setPosition(PlayPosition.LEFT));
    }
    
    @FXML private void timeoutRightAction(ActionEvent event) {
    	context.postEvent(new TimeoutStartRequest().setPosition(PlayPosition.RIGHT));
    }
    
    @FXML private void onPeriodAction(MouseEvent event) {    	
    	if (! phaseSelectBinding.get()) return;
    	
    	if (correctionProperty.get()) {
    		boolean proceed = forwardTimeProperty.get() == 0
    		    || KronoHelper.requestUser(
    		    	context.getItString("operator.onPeriodAction.goBackward.title"),
    				context.getItString("operator.onPeriodAction.goBackward.question") + "\n"
    		   		   + context.getItString("operator.onPeriodAction.goBackward.warning") + ".", 
    		   		stage);
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
        		   		stage);
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

    @FXML private void leftPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, true, PlayPosition.LEFT);
    }
	
    @FXML private void rightPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, true, PlayPosition.RIGHT);
    }

    @FXML private void leftAllPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, false, PlayPosition.LEFT);
    }
    
    @FXML private void rightAllPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, false, PlayPosition.RIGHT);
    }
    
    @FXML private void leftScoreTableAction(MouseEvent event) {
    	scoreTableAction(event, PlayPosition.LEFT );
    }
    
    @FXML private void rightScoreTableAction(MouseEvent event) {
    	scoreTableAction(event, PlayPosition.RIGHT);
    }    

    @FXML private void onResetAction(ActionEvent event) {
    	if (playTimeRunningProperty.get()) return;
    	
    	if (KronoHelper.requestUser(context, "operator.onResetAction.title", 
    		"operator.onResetAction.message", stage)) {
    		context.postEvent(new ResetPlayRequest());
    	}
    }
    
    @FXML private void leftPlayersTableAction(MouseEvent event) {
        modifyPlayer(PlayPosition.LEFT, event);
    }

    @FXML private void rightPlayersTableAction(MouseEvent event) {
    	modifyPlayer(PlayPosition.RIGHT, event);
    }

    @FXML private void addLeftPlayerAction(ActionEvent event) {
    	addPlayers(PlayPosition.LEFT);
    }
    
    @FXML private void addRightPlayerAction(ActionEvent event) {
    	addPlayers(PlayPosition.RIGHT);
    }
    
    @FXML private void importLeftPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(PlayPosition.LEFT, false);
    }
    
    @FXML private void importRightPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(PlayPosition.RIGHT, false);
    }
    
    @FXML private void exportLeftPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(PlayPosition.LEFT, true);
    }
    
    @FXML private void exportRightPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(PlayPosition.RIGHT, true);
    }
    
    @FXML private void exportExcelAction(ActionEvent event) {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onExportExcelAction.title", true, 
    			"operator.filetype.xslxFiles", "*.xlsx",
    			"operator.filetype.allFiles", "*.*");
    	if (file != null) {
    		context.postEvent(new ExportXlsRequest().setUrl(KronoHelper.toExternalForm(file)));
    	}
    }

    @FXML private void exportPdfAction(ActionEvent event) {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onExportPdfAction.title", true, 
    			"operator.filetype.pdfFiles", "*.pdf",
    			"operator.filetype.allFiles", "*.*");
    	if (file != null) {
    		context.postEvent(new ExportPdfRequest().setUrl(KronoHelper.toExternalForm(file)));
    	}
    }

    @FXML private void saveAsAction(ActionEvent event) {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onSaveAsAction.title", true, 
    			"operator.filetype.xmlFiles", "*.xml",
    			"operator.filetype.allFiles", "*.*");
    	if (file != null) {
    		context.postEvent(new ReportSaveAsRequest().setUrl(KronoHelper.toExternalForm(file)));
    	}
    }

    private void importOrExportPlayerAction(PlayPosition position, boolean export) {    	  		
    	String titleProp = export 
        			? "operator.onImportPlayerAction.title"
        			: "operator.onExportPlayerAction.title";
    	File file = fileSelect("histoPath", "gen/histo", titleProp, export, 
    			"operator.filetype.csvFiles", "*.csv",
    			"operator.filetype.allFiles", "*.*");
    		
    	if (file != null && (export || file.canRead())) {
    		importOrExport(file, position, export ? new TeamExportRequest() : new TeamImportRequest());
    	}
    }

    private File fileSelect(final String initPathProp, 
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
    
    private void importOrExport(File file, PlayPosition position, TeamImportExportRequest request) {
		context.postEvent(request
			.setUrl(KronoHelper.toExternalForm(file))
			.setSide(position));
    }

    private void addPlayers(PlayPosition position) {
    	while (true) {
	    	playerInputController.setInputMode(PlayerInputController.InputMode.CREATION);
	    	playerInputController.clear();
	    	playerInputController.showModal();
	    	if (playerInputController.isCancelled()) return;
	    	PlayerSnapshot player = playerInputController.getPlayer();
	    	player.setTeam(position);
	    	context.postEvent(new TeamPlayerCreationRequest().setPlayer(player).setSide(position));
    	}
    }
    
    private void modifyPlayer(PlayPosition position, MouseEvent event) {
    	@SuppressWarnings("unchecked")
		TableView<PlayerSnapshot> table = (TableView<PlayerSnapshot>) event.getSource();
    	PlayerSnapshot player =  table.getSelectionModel().selectedItemProperty().get();
    	if (player == null) return;
    	    	
    	playerInputController.setInputMode(PlayerInputController.InputMode.MODIFICATION);
    	playerInputController.setPlayer(player);
    	playerInputController.showModal();
    	if (playerInputController.isCancelled()) return;
    	
    	PlayerSnapshot output;
    	if (playerInputController.isShallDelete()) {
    		output = player;
    	} else {
	    	output = playerInputController.getPlayer();
	    	output.setTeam(position);
	    	output.setUid(player.getUid());
    	}
    	
    	TeamPlayerRequest request = playerInputController.isShallDelete()
    		? new TeamPlayerDeletionRequest()
    		: new TeamPlayerModificationRequest();
    	context.postEvent(request.setPlayer(output).setSide(position));
    }
    
    private void animationAction(MediaItem item, boolean start) {
    	if (playTimeRunningProperty.get()) return;
    	
    	AnimationRequest request = start
    		? new AnimationStartRequest()
    		: new AnimationStopRequest();    		
    	context.postEvent(request.setUrl(item.getUrl()));
    }
    
	private void handleKeyEvent(KeyEvent event) {
		if (event.getEventType() ==  KeyEvent.KEY_RELEASED && event.getCode() == KeyCode.SPACE) {
			startOrStopPlayTime();
			event.consume();
		}
	}
	
	private void startOrStopPlayTime() {  
		if (correctionProperty.get()) return;
		if (forwardTimeProperty.get() >= phaseDurationProperty.get() * 60) return;
		
		context.postEvent(new PlayTimeStartOrStopRequest());
    }
	
    private void buildMusicHandler() {
    	buildMusicHandler("musicBottom", musicBottomButton);
    	buildMusicHandler("musicLeft", musicLeftButton);
    	buildMusicHandler("musicRight", musicRightButton);
    }
    
    private void buildMusicHandler(String listName, ToggleButton button) {
    	MediaButtonHandler.builder()
    		.context(context)
		    .startAction(i->context.postEvent(new MusicStartRequest().setUrl(i.getUrl())))
		    .stopAction(i->context.postEvent(new MusicStopRequest().setUrl(i.getUrl())))
	   	    .listName(listName)
    	    .mediaButton(button)
    	    .build()
    	    .init();
    }
 
    private void buildAnimationHandler() {
    	buildAnimationHandler("animationBottom", animationBottomButton);
    	buildAnimationHandler("animationLeft", animationLeftButton);
    	buildAnimationHandler("animationRight", animationRightButton);
    }
    
    private void buildAnimationHandler(String listName, ToggleButton button) {
    	MediaButtonHandler.builder()
    	    .context(context)
		    .startAction(i->animationAction(i, true))
		    .stopAction(i->animationAction(i, false))
	   	    .listName(listName)
    	    .mediaButton(button)
    	    .build()
    	    .init();
    }
    	
	private void reset() {
		setTeamImage(null, PlayPosition.LEFT);
		setTeamImage(null, PlayPosition.RIGHT);
		setCorrectionMode(false);
		resetTeamElements();
		
		if (context.getBooleanProperty("showParameters", true)) {
		    settingsInputController.showModal();
		}
	}

    private void updatePenaltyTable(PenaltyNotif event) {
    	final TableView<PenaltyVolatile> liveTable = penaltyTables.getFromPosition(event.getSide());
    	liveTable.refresh();
    	
    	final TableView<PenaltyVolatile> historyTable =  allPenaltyTables.getFromPosition(event.getSide());
    	historyTable.refresh();
	}
    
  	private void updatePlayerTable(TeamPlayerNotif event) {
  		final TableView<PlayerSnapshot> playerTable = playerTables.getFromPosition(event.getSide());
    	playerTable.refresh();
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
      
    private void toggleCorrectionMode() {
    	setCorrectionMode(! correctionProperty.get());
    }
    
    private void setCorrectionMode(boolean mode) {
    	correctionProperty.set(mode); 
    	setCorrectionStyle(correctionButton, correctionProperty.get());
    	setCorrectionStyle(period, correctionProperty.get() && phaseSelectBinding.get());
    	setCorrectionStyle(scoreLeft, correctionProperty.get() && scoreSelectBindings.getLeft().get());
    	setCorrectionStyle(scoreRight, correctionProperty.get() && scoreSelectBindings.getRight().get());
    	setCorrectionStyle(clock2, correctionProperty.get());
    	setCorrectionStyle(clock3, correctionProperty.get());

    	setSelectabilityStyle(clock2, correctionProperty.get());
    	setSelectabilityStyle(clock3, correctionProperty.get());
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

    private void scoreEvent(ScoreNotif event) {    	
 		scoreDecAlloweds.getFromPosition(event.getMark().getTeam()).set(event.isScoreDecAllowed());
 		scoreIncAlloweds.getFromPosition(event.getMark().getTeam()).set(event.isScoreIncAllowed());
 		TableView<ScoreVolatile> table = (event.getMark().getTeam() == PlayPosition.LEFT) ? scoreLeftTable : scoreRightTable;
 		table.refresh();
    }

	private void updatePhase(PeriodModificationNotif event) {
		phaseDecAllowed.set(event.isPhaseDecAllowed());
		phaseIncAllowed.set(event.isPhaseIncAllowed());
	}
    
    private void handleScoreSelectability(final PlayPosition position) {
    	BooleanBinding binding = Bindings.or(
    	    Bindings.not(correctionProperty).and(scoreIncAlloweds.getFromPosition(position)),
    		correctionProperty.and(scoreDecAlloweds.getFromPosition(position))
    	);
    	scoreSelectBindings.setFromPosition(binding, position);
    	binding.addListener(observable -> 
	    	setSelectabilityStyle(scoreLabels.getFromPosition(position), scoreSelectBindings.getFromPosition(position).get()));
    }
    
    private void handleScoreSelectability() {
    	scoreDecAlloweds = new SimpleLateralizedPair<>(new SimpleBooleanProperty(), new SimpleBooleanProperty());
    	scoreIncAlloweds = new SimpleLateralizedPair<>(new SimpleBooleanProperty(), new SimpleBooleanProperty());
    	scoreSelectBindings = new SimpleLateralizedPair<BooleanBinding>();
    	handleScoreSelectability(PlayPosition.LEFT);
    	handleScoreSelectability(PlayPosition.RIGHT);
    }
    
    private void handlePhaseSelectability() {
    	phaseIncAllowed = new SimpleBooleanProperty();
    	phaseDecAllowed = new SimpleBooleanProperty();
    	phaseSelectBinding = Bindings.not(playTimeRunningProperty).and(Bindings.or(
        	    Bindings.not(correctionProperty).and(phaseIncAllowed),
        		correctionProperty.and(phaseDecAllowed)
        	));
    	phaseSelectBinding.addListener(observable -> 
    	    setSelectabilityStyle(period, phaseSelectBinding.get()));
    	
    }
    
    private boolean loadReportFile() {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onLoadReportAction.title", false, 
    			"operator.filetype.xmlFiles", "*.xml",
    			"operator.allFiles", "*.*");
    	if (file == null) {
    		return false;
    	}
    	if (! file.canRead()) {
    		log.debug("Cannot read {}", file.getAbsolutePath());
    		return false;
    	}
    	
    	context.postEvent(new ReportLoadRequest().setUrl(KronoHelper.toExternalForm(file)));
    	return true;
    }
    
	private void scoreAction(PlayPosition position) {
		// Checks that the processing is allowed.
		if (! scoreSelectBindings.getFromPosition(position).get()) return;
		
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
   
    private void setCorrectionStyle(Control control, boolean correction) {
    	KronoHelper.setStyle(control, correction, "correction"); 
	}

    private void setPendingStyle(Control control) {
    	KronoHelper.setStyle(control, ! playTimeRunningProperty.get(), "pending");
    }
    
    private void setSelectabilityStyle(Control control, boolean selectable) {
    	KronoHelper.setStyle(control, selectable, "selectable");
    }

	private void penaltyTableAction(MouseEvent event, boolean isLiveTable, PlayPosition position) {
		@SuppressWarnings("unchecked")
		TableView<PenaltyVolatile> table = (TableView<PenaltyVolatile>) event.getSource();
    	PenaltyVolatile penaltyVolatile =  table.getSelectionModel().selectedItemProperty().get();
    	if (penaltyVolatile == null) return;
    	
    	PenaltySnapshot oldValue = PenaltySnapshot.of(penaltyVolatile);
    	penaltyInputController.setInputMode(isLiveTable ? InputMode.LIVE_MODIF : InputMode.VALID_MODIF);
    	penaltyInputController.setPlayers(playerListProperties.getFromPosition(position));
    	penaltyInputController.setPenalty(penaltyVolatile);
    	penaltyInputController.showModal();
    	PenaltySnapshot newValue = penaltyInputController.getPenalty();
    			
    	if (penaltyInputController.shallDelete()) {
    		context.postEvent(new PenaltyRemoveRequest().setPenalty(oldValue));
    	} else if (penaltyInputController.shallComplete()) {
    		context.postEvent(new PenaltyCompleteRequest()
    			.setNewValues(PenaltySnapshot.of(newValue)).setPenalty(oldValue));
    	} else if (! penaltyInputController.isCancelled()) {
    		context.postEvent(new PenaltyModifRequest().
    			setNewValues(PenaltySnapshot.of(newValue)).setPenalty(oldValue));
    	}
    }
	
	private void scoreTableAction(MouseEvent event, PlayPosition position) {
		@SuppressWarnings("unchecked")
		TableView<ScoreVolatile> table = (TableView<ScoreVolatile>) event.getSource();
    	ScoreVolatile scoreVolatile =  table.getSelectionModel().selectedItemProperty().get();
    	if (scoreVolatile == null) return;
    	
    	markInputController.setInputMode(MarkInputController.InputMode.MODIFICATION);
		markInputController.setPlayers(playerListProperties.getFromPosition(position));
    	markInputController.setMark(scoreVolatile);
    	markInputController.showModal();
    	if (markInputController.isCancelled()) return;

    	ScoreSnapshot output = markInputController.getMark();
    	output.setTeam(scoreVolatile.getTeam());
    	output.setUid(scoreVolatile.getUid());
    	output.setSystemTime(scoreVolatile.getSystemTime());
    	
    	ScoreRequest request = markInputController.isShallDelete()
    		? new ScoreDeletionRequest() 
    		: new ScoreModificationRequest();
    	context.postEvent(request.setMark(output));
    }
	
    private void handlePeriodEnd(PeriodEndNotif event) {
		beeper.play();
	}

	private void timeResumed(PlayTimeStartOrStopNotif event) {
    	setPendingStyle(clock1);
    }
    
    private void customizePenaltyColumn(TableView<PenaltyVolatile> tableView) {
    	@SuppressWarnings("unchecked")
		TableColumn<PenaltyVolatile, String> column = (TableColumn<PenaltyVolatile, String>) tableView.getColumns().get(0);
    	column.setCellFactory(c -> new HighlightableTableCell(context, playTimeRunningProperty));
    }
    
	private void customizeTeamImage(String prop, PlayPosition position) {
		String fileName = context.getProperty(prop, null);
		if (fileName == null) return;
		File file = context.getLocalFile(fileName, ResourceType.IMAGES);
		setTeamImage(file, position);
	}

    private void resetTeamElements() {
    	customizeTeamImage("leftTeamImage", PlayPosition.LEFT);
    	customizeTeamImage("rightTeamImage", PlayPosition.RIGHT);
    }

}
