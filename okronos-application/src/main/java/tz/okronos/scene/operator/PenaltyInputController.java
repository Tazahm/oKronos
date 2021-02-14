package tz.okronos.scene.operator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoHelper;
import tz.okronos.controller.breach.model.BreachDesc;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.scene.ModalControllerMultiScenes;
import tz.okronos.scene.control.IntegerField;
import tz.okronos.scene.control.PlayerSelector;
import tz.okronos.scene.control.TimeField;


/**
 *  Handles the input of penalties. Allows the modify the remaining time into a secondary controller.
 */
public class PenaltyInputController extends ModalControllerMultiScenes {
	public enum InputMode {
		CREATION,
		LIVE_MODIF,
		VALID_MODIF
	}
	
	private static class BreachConverter extends StringConverter<BreachDesc> {

		@Override
		public String toString(BreachDesc breach) {
			if (breach.getCode().isEmpty()) return breach.getLabel();
			return breach.getCode() + " - " + breach.getLabel();
		}

		@Override
		public BreachDesc fromString(String string) {
			return null;
		}
		
	}
	
	private PenaltySnapshot penalty;
	private InputMode inputMode = InputMode.CREATION;
	private boolean shallDelete;
	private boolean shallComplete;
	private Map<Integer, Toggle> durationToToggleMap;
	
	@FXML private PlayerSelector playerSelector;
	@FXML private ChoiceBox<BreachDesc> codeChoiceBox;
	@FXML private ToggleGroup durationGroup;
	@FXML private Button deleteButton;
	@FXML private Button completeButton;
    @FXML private CheckBox onStoppageCheckbox;
    @FXML private CheckBox pendingCheckbox;
    @FXML private Button timeButton;
    @FXML private Button validateButton;
    @FXML private IntegerField periodField;
    @FXML private Label periodLabel;
    @FXML private Label startLabel;
    @FXML private TimeField startField;
    @FXML private Label stopLabel;
    @FXML private TimeField stopField;
     
	public PenaltyInputController() {
	}
    
	public void setPlayers(List<PlayerSnapshot> players) {
		playerSelector.setPlayers(players);
	}
	
	public void setBreaches(ObservableList<BreachDesc> breaches) {
		codeChoiceBox.setItems(breaches);
		codeChoiceBox.setConverter(new BreachConverter());
	}
	
	public void setPenalty(PenaltyVolatile penalty) {
		this.penalty = PenaltySnapshot.of(penalty);
		setDuration(penalty.getDuration());
		playerSelector.selectPlayer(penalty.getPlayer());
		onStoppageCheckbox.setSelected(penalty.isOnStoppage());
		pendingCheckbox.setSelected(penalty.isPending());
		selectBreachByCode(penalty.getCode());
		
		final boolean validated = penalty.isValidated();
		KronoHelper.setManaged(periodLabel, validated);
		KronoHelper.setManaged(periodField, validated);
		KronoHelper.setManaged(startLabel, validated);
		KronoHelper.setManaged(startField, validated);
		KronoHelper.setManaged(stopLabel, validated);
		KronoHelper.setManaged(stopField, validated);

		if (validated) {
			periodField.setValue(penalty.getPeriod());
			startField.setValue(penalty.getStartTime());
			stopField.setValue(penalty.getStopTime());
		}
		
		PenaltyTimeInputController timeCtrl = (PenaltyTimeInputController) getSecondaryController();
		timeCtrl.setPenalty(penalty);
	}
	
	public PenaltySnapshot getPenalty() {
		PenaltySnapshot output =  PenaltySnapshot.of(penalty);
		PenaltyTimeInputController timeCtrl = (PenaltyTimeInputController) getSecondaryController();
		if (! timeCtrl.isCancelled()) {
		    output.setRemainder(timeCtrl.getModifiedTime());
		}
		return output;
	}
	
	@PostConstruct
	public void init() {
		durationToToggleMap = new HashMap<>();
		for (Toggle toggle : durationGroup.getToggles()) {
			int duration = Integer.parseInt(((ToggleButton) toggle).getText().trim());
			durationToToggleMap.put(duration, toggle);
		}
		
		playerSelector.setContext(context);
	}
	
	private void selectBreachByCode(String code) {
		ObservableList<BreachDesc> items = codeChoiceBox.getItems();
		BreachDesc breach = items.stream().filter(i->i.getCode().equals(code)).findFirst().orElse(null);
		codeChoiceBox.setValue(breach);
	}

	private int getDuration() {
		if (durationGroup.getSelectedToggle() == null) {
			return 2;
		}
		return Integer.parseInt(((ToggleButton) durationGroup.getSelectedToggle()).getText().trim());
	}

	private void setDuration(int duration) {
		Toggle toggle = durationToToggleMap.get(duration);
		if (toggle == null) return;
		durationGroup.selectToggle(toggle);
	}
	
	protected void doValidateAction(ActionEvent event) {
		penalty.setDuration(getDuration());
		penalty.setPlayer(playerSelector.getSelectedPlayer() == null ? 0 : playerSelector.getSelectedPlayer().getShirt());
		penalty.setOnStoppage(onStoppageCheckbox.isSelected());
		penalty.setPending(pendingCheckbox.isSelected());
		BreachDesc breach = codeChoiceBox.getValue();
		penalty.setCode(breach == null ? null : breach.getCode());
		if (InputMode.CREATION == inputMode) {
			penalty.setSystemTime(new Date().getTime());
		}
		if (penalty.isValidated()) {
			penalty.setPeriod(periodField.getValue());
			penalty.setStartTime(startField.getValue());
			penalty.setStopTime(stopField.getValue());
		}
		
		PenaltyTimeInputController timeCtrl = (PenaltyTimeInputController) getSecondaryController();
		if (! timeCtrl.isCancelled()) {
			int newTime = timeCtrl.getModifiedTime();
			if (newTime >= 0 && newTime <= penalty.getDuration() * 60) {				
				penalty.setStartTime(penalty.getStartTime() + newTime
						- penalty.getRemainder());
				penalty.setRemainder(newTime);
			}
		}				
	}

	@FXML private void deleteAction(ActionEvent event) {
		shallDelete = true;
		stage.hide();
	}

	@FXML private void modifRemainigAction(ActionEvent event) {
		toggleScene();
	}
	
	
	@FXML protected void completeAction(ActionEvent event) {
		shallComplete = true;
		validateAction(event);
	}

	public void doShowModal() {
		super.doShowModal();
		boolean isModif = inputMode == InputMode.LIVE_MODIF || inputMode == InputMode.VALID_MODIF;
		if (! isModif) {
			playerSelector.selectPlayer(null);
			codeChoiceBox.setValue(null);
		}
		shallDelete = false;
		shallComplete = false;
		
		KronoHelper.setManaged(deleteButton, isModif);
		KronoHelper.setManaged(completeButton, isModif);				
		completeButton.setDisable(penalty.isValidated());
		timeButton.setVisible(inputMode == InputMode.LIVE_MODIF);
		
		final String validateKey = inputMode == InputMode.CREATION ? "modal.validate" : "penalty.modify";
		validateButton.setText(context.getItString(validateKey));
	}

	public InputMode getInputMode() {
		return inputMode;
	}

	public void setInputMode(InputMode inputMode) {
		this.inputMode = inputMode;
	}

	public boolean shallDelete() {
		return shallDelete;
	}

	public boolean shallComplete() {
		return shallComplete;
	}
}
