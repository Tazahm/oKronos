package tz.okronos.scene.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import tz.okronos.controller.team.model.PlayerSnapshot;

/**
 *  Displays the property 'goalkeeper' (as 'G') and 'official' (as 'O') 
 *  of a player into a table cell.
 */
public class PlayerGOValueFactory 
implements Callback<CellDataFeatures<PlayerSnapshot, String>, ObservableValue<String>> {

	@Override
	public ObservableValue<String> call(CellDataFeatures<PlayerSnapshot, String> param) {
		PlayerSnapshot player = param.getValue();
		//StringExpression binding = Bindings.when(player.isGoalkeeper()).then("G").otherwise("")
		//	.concat(Bindings.when(player.isOfficial()).then("O").otherwise(""));
		//return binding;
		String value = "";
		if (player.isGoalkeeper()) value = value + "G";
		if (player.isOfficial()) value = value + "O";
		return new SimpleStringProperty(value);
	}
	
}
