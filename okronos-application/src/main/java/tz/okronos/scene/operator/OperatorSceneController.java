package tz.okronos.scene.operator;

import java.io.File;

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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.application.ResetPlayRequest;
import tz.okronos.controller.animation.event.request.AnimationRequest;
import tz.okronos.controller.animation.event.request.AnimationStartRequest;
import tz.okronos.controller.animation.event.request.AnimationStopRequest;
import tz.okronos.controller.breach.model.BreachDesc;
import tz.okronos.controller.music.event.request.MusicStartRequest;
import tz.okronos.controller.music.event.request.MusicStopRequest;
import tz.okronos.controller.period.event.notif.PeriodEndNotif;
import tz.okronos.controller.playtime.event.request.PlayTimeStartOrStopRequest;
import tz.okronos.controller.report.event.request.ReportLoadRequest;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.controller.team.event.notif.TeamPlayerNotif;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.operator.media.MediaButtonHandler;
import tz.okronos.scene.operator.media.MediaItem;

/**
 * The main controller for the operator scene.
 */
@SuppressWarnings("unused")
@Slf4j
@Component
@Scope("prototype")
public class OperatorSceneController extends AbstractSceneController {

	@Autowired
	private OperatorInputBuilder inputsBuilder;
	@Autowired
	@Qualifier("phaseDurationProperty")
	private ReadOnlyIntegerProperty phaseDurationProperty;
	@Autowired @Qualifier("forwardTimeProperty")
    private ReadOnlyIntegerProperty forwardTimeProperty;
	@Autowired OperatorSceneDelegate sceneHelper;

	private SettingsInputController settingsInputController;


	public OperatorSceneController() {
	}

	@PostConstruct
	public void init() throws Exception {
		settingsInputController = inputsBuilder.settingsInputController();
		settingsInputController.setLoadCallback(() -> loadReportFile());

		getScene().addEventFilter(KeyEvent.ANY, e -> handleKeyEvent(e));
		context.registerEventListener(this);
	}

	@FxSubscribe
	public void onPhaseEndNotif(PeriodEndNotif event) {
		handlePeriodEnd(event);
	}

	@FxSubscribe
	public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}

	@FxSubscribe
	public void onShutdownRequest(final ShutdownRequest event) {
		getStage().hide();
	}

	@FXML
	private void beepAction(ActionEvent event) {
		sceneHelper.beep();
	}

	private void handleKeyEvent(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_RELEASED && event.getCode() == KeyCode.SPACE) {
			sceneHelper.startOrStopPlayTime();
			event.consume();
		}
	}

	private void reset() {
		if (context.getBooleanProperty("showParameters", true)) {
			settingsInputController.showModal();
		}
	}

	private boolean loadReportFile() {
		File file = fileSelect("histoPath", "gen/histo", "operator.onLoadReportAction.title", false,
				"operator.filetype.xmlFiles", "*.xml", "operator.filetype.allFiles", "*.*");
		if (file == null) {
			return false;
		}
		if (!file.canRead()) {
			log.debug("Cannot read {}", file.getAbsolutePath());
			return false;
		}

		context.postEvent(new ReportLoadRequest().setUrl(KronoHelper.toExternalForm(file)));
		return true;
	}

	private void handlePeriodEnd(PeriodEndNotif event) {
		sceneHelper.beep();
	}
}
