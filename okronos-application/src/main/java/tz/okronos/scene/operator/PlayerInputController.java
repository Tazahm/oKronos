package tz.okronos.scene.operator;

import javax.annotation.PostConstruct;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.scene.ModalController;
import tz.okronos.scene.control.IntegerField;


/**
 *  Controls the input of goals.
 */
public class PlayerInputController extends ModalController {
	public enum InputMode {
		CREATION,
		MODIFICATION
	}

    @Getter private boolean shallDelete;
    @Setter private InputMode inputMode = InputMode.CREATION;
    
	@FXML private TextField nameField;
	@FXML private TextField licenceField;
	@FXML private IntegerField shirtField;
	@FXML private CheckBox goalkeeperCheckbox;
	@FXML private CheckBox officialCheckbox;
    @FXML private Button deleteButton;
	@FXML private Button modifyButton;
    
    
	public PlayerInputController() {		
	}
  
	@PostConstruct 
    public void init()  {
		goalkeeperCheckbox.selectedProperty().addListener((o,p,n) -> unselect(officialCheckbox, goalkeeperCheckbox));
		officialCheckbox.selectedProperty().addListener((o,p,n) -> unselect(goalkeeperCheckbox, officialCheckbox));
		
		// Shirt number is mandatory.
		shirtField.addContentListener((s,o,n)->modifyButton.setDisable(shirtField.getText() == null 
				|| shirtField.getText().isBlank()));
	}
	
	public void setPlayer(PlayerSnapshot player) {
		nameField.setText(player.getName());
		licenceField.setText(player.getLicence());
		shirtField.setValue(undefToNull(player.getShirt()));
		goalkeeperCheckbox.setSelected(player.isGoalkeeper());
		officialCheckbox.setSelected(player.isOfficial());
	}

	public void clear() {
		nameField.clear();
		licenceField.clear();
		shirtField.clear();
		goalkeeperCheckbox.setSelected(false);
		officialCheckbox.setSelected(false);
	}

	public PlayerSnapshot getPlayer() {
		PlayerSnapshot player = new PlayerSnapshot();
		player.setName(nameField.getText());
		player.setLicence(licenceField.getText());
		player.setShirt(nullToUndef(shirtField.getValue()));
		player.setGoalkeeper(goalkeeperCheckbox.isSelected());
		player.setOfficial(officialCheckbox.isSelected());
		return player;
	}

	@FXML private void deleteAction(ActionEvent event) {
		shallDelete = true;
		stage.hide();
	}
	
	protected void preShowModal() {
		shallDelete = false;
		KronoHelper.setManaged(deleteButton, inputMode == InputMode.MODIFICATION);
	}
		
	private int nullToUndef(Integer input) {
		return input == null ? BindingHelper.NO_VALUE : input;
	}
	
	private Integer undefToNull(int input) {
		return input == BindingHelper.NO_VALUE ? null : input;
	}

	private void unselect(CheckBox target, CheckBox origin) {
		if (origin.isSelected() && target.isSelected()) {
		    target.setSelected(false);
		}
	}

}
