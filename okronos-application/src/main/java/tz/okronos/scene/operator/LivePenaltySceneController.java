package tz.okronos.scene.operator;


import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
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
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.control.HighlightableTableCell;
import tz.okronos.scene.operator.PenaltyInputController.InputMode;

/**
 *  The controller for the live penalties.
 */
// @Slf4j
@Component
@Scope("prototype")
public class LivePenaltySceneController extends AbstractSceneController implements Initializable {   
    @Autowired private OperatorInputBuilder inputsBuilder;
    private SimpleLateralizedPair<TableView<PenaltyVolatile>> penaltyTables;
    @Autowired @Qualifier("penaltyLiveListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyLiveListProperties;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    @Autowired @Qualifier("playTimeRunningProperty")
    private ReadOnlyBooleanProperty playTimeRunningProperty;
    
    @FXML private TableView<PenaltyVolatile> leftPenaltyTable;
    @FXML private TableView<PenaltyVolatile> rightPenaltyTable;

	private PenaltyInputController penaltyInputController;

	
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
  	@FxSubscribe public void onPenaltyNotif(PenaltyNotif event) {
  		updatePenaltyTable(event);
  	}
  	    
    @FXML private void leftPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, true, PlayPosition.LEFT);
    }
	
    @FXML private void rightPenaltyTableAction(MouseEvent event) {
    	penaltyTableAction(event, true, PlayPosition.RIGHT);
    }

    private void init() throws Exception  {
    	penaltyInputController = inputsBuilder.getSingleton(PenaltyInputController.class);
    	
    	penaltyTables = new SimpleLateralizedPair<>(leftPenaltyTable, rightPenaltyTable);
       	leftPenaltyTable.setItems(penaltyLiveListProperties.getLeft());
       	rightPenaltyTable.setItems(penaltyLiveListProperties.getRight());       

       	customizePenaltyColumn(leftPenaltyTable);
		customizePenaltyColumn(rightPenaltyTable);
		
		context.registerEventListener(this);
    }
    
    private void customizePenaltyColumn(TableView<PenaltyVolatile> tableView) {
    	@SuppressWarnings("unchecked")
		TableColumn<PenaltyVolatile, String> column = (TableColumn<PenaltyVolatile, String>) tableView.getColumns().get(0);
    	column.setCellFactory(c -> new HighlightableTableCell(context, playTimeRunningProperty));
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
    
    private void updatePenaltyTable(PenaltyNotif event) {
    	final TableView<PenaltyVolatile> liveTable = penaltyTables.getFromPosition(event.getSide());
    	liveTable.refresh();    	
	}
    


}
