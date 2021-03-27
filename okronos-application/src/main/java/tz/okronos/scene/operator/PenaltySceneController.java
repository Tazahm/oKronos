package tz.okronos.scene.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.penalty.event.notif.PenaltyNotif;
import tz.okronos.controller.penalty.event.request.PenaltyCompleteRequest;
import tz.okronos.controller.penalty.event.request.PenaltyModifRequest;
import tz.okronos.controller.penalty.event.request.PenaltyRemoveRequest;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneControllerLateralized;
import tz.okronos.scene.operator.PenaltyInputController.InputMode;


/**
 *  The controller for the live penalty pane.
 */
@Component
@Scope("prototype")
public class PenaltySceneController extends AbstractSceneControllerLateralized {
    @Autowired @Qualifier("penaltyHistoryListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyHistoryListProperties;
    @Autowired private OperatorInputBuilder inputsBuilder;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;    
    @FXML private TableView<PenaltyVolatile> allPenaltyTable;
	    
    private PenaltyInputController penaltyInputController;

	
    @FXML private void allPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, false, PlayPosition.RIGHT);
    }

  	@FxSubscribe public void onPenaltyNotif(PenaltyNotif event) {
  		updatePenaltyTable(event);
  	}
  	
	@Override
	protected Node getNodeForInitialization() {
		return allPenaltyTable;
	}

    protected void postInit() throws Exception {
    	allPenaltyTable.setItems(penaltyHistoryListProperties.getFromPosition(side));
    	penaltyInputController = inputsBuilder.getSingleton(PenaltyInputController.class);
    	
    	context.registerEventListener(this);
	}
    
    private void updatePenaltyTable(PenaltyNotif event) {
    	if (event.getSide() != side) return;
    	allPenaltyTable.refresh();
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

}
	