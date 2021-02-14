package tz.okronos.scene.operator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tz.okronos.scene.ModalController;

/**
 *  Allows to modify the name of on team.
 */
public class TeamInputController extends ModalController {
	
	@FXML private TextField teamName;	
	
	public TeamInputController() {		
	}
			
	public void setTeamName(String name) {
		teamName.setText(name);
	}
	
	public String getTeamName() {
		return teamName.getText();
	}
}
