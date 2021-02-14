package tz.okronos.scene.control;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import tz.okronos.controller.penalty.model.PenaltyVolatile;

/**
 *  Displays the property 'pending' (as 'S') and 'on stoppage' (as 'A') 
 *  of a penalty into a table cell.
 */
public class PenaltySAValueFactory 
implements Callback<CellDataFeatures<PenaltyVolatile, String>, ObservableValue<String>> {

	@Override
	public ObservableValue<String> call(CellDataFeatures<PenaltyVolatile, String> param) {
		PenaltyVolatile penaltyVolatile = param.getValue();
		StringExpression binding = Bindings.when(penaltyVolatile.pendingProperty()).then("S").otherwise("")
			.concat(Bindings.when(penaltyVolatile.onStoppageProperty()).then("A").otherwise(""));
		return binding;
	}
	
}
