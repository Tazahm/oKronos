package tz.okronos.scene.operator;

import java.util.List;

import javax.annotation.PostConstruct;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import tz.okronos.controller.score.model.ScoreContract;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoHelper;
import tz.okronos.scene.ModalController;
import tz.okronos.scene.control.IntegerField;
import tz.okronos.scene.control.PlayerSelector;
import tz.okronos.scene.control.TimeField;


/**
 *  Controls the input of goals.
 */
public class MarkInputController extends ModalController {
	public enum InputMode {
		CREATION,
		MODIFICATION
	}

    @Getter private boolean shallDelete;
    @Setter private InputMode inputMode = InputMode.CREATION;
    
    @FXML private PlayerSelector scorerSelector;
    @FXML private PlayerSelector assist1Selector;
    @FXML private PlayerSelector assist2Selector;
    @FXML private Button deleteButton;
    @FXML private HBox topContainer;
    @FXML private IntegerField periodField;
    @FXML private TimeField timeField;
    
    
	public MarkInputController() {		
	}
  
	@PostConstruct 
    public void init()  {
		scorerSelector.setContext(context);
		assist1Selector.setContext(context);
		assist2Selector.setContext(context);
		
		assist1Selector.setAllowNoSelection(true);
		assist2Selector.setAllowNoSelection(true);
	}
	
	public void setMark(ScoreContract input) {
		scorerSelector.selectPlayer(undefToNull(input.getScorer()));
		assist1Selector.selectPlayer(undefToNull(input.getAssist1()));
		assist2Selector.selectPlayer(undefToNull(input.getAssist2()));
		
		if (inputMode == InputMode.MODIFICATION) {
			periodField.setValue(input.getPeriod());
			timeField.setValue(input.getTime());
		}
	}
	
	public ScoreSnapshot getMark() {
		ScoreSnapshot mark = new ScoreSnapshot();
		mark.setScorer(nullToUndef(scorerSelector.getSelectedPlayer()));
		mark.setAssist1(nullToUndef(assist1Selector.getSelectedPlayer()));
		mark.setAssist2(nullToUndef(assist2Selector.getSelectedPlayer()));
		if (inputMode == InputMode.MODIFICATION) {
			mark.setPeriod(periodField.getValue());
			mark.setTime(timeField.getValue());
		}
		
		return mark;
	}

	public void setPlayers(List<PlayerSnapshot> players) {
		scorerSelector.setPlayers(players);
		assist1Selector.setPlayers(players);
		assist2Selector.setPlayers(players);
	}

	public void clear() {
		scorerSelector.selectPlayer(null);
		assist1Selector.selectPlayer(null);
		assist2Selector.selectPlayer(null);
	}

	@FXML private void deleteAction(ActionEvent event) {
		shallDelete = true;
		stage.hide();
	}
	
	protected void doShowModal() {
		shallDelete = false;
		KronoHelper.setManaged(deleteButton, inputMode == InputMode.MODIFICATION);
		KronoHelper.setManaged(topContainer, inputMode == InputMode.MODIFICATION);
	}
	
	private int nullToUndef(PlayerSnapshot input) {
		return input == null ? Integer.MIN_VALUE : input.getShirt();
	}
	
	private Integer undefToNull(int input) {
		return input == Integer.MIN_VALUE ? null : input;
	}
	
}
