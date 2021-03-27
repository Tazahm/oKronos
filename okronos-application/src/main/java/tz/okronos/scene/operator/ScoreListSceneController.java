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
import tz.okronos.controller.score.event.notif.ScoreNotif;
import tz.okronos.controller.score.event.request.ScoreDeletionRequest;
import tz.okronos.controller.score.event.request.ScoreModificationRequest;
import tz.okronos.controller.score.event.request.ScoreRequest;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.score.model.ScoreVolatile;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneControllerLateralized;

/**
 *  The controller for the score list.
 */
@Component
@Scope("prototype")
public class ScoreListSceneController extends AbstractSceneControllerLateralized {
    @Autowired @Qualifier("scoreListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<ScoreVolatile>> scoreListProperties;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    @Autowired private OperatorInputBuilder inputsBuilder;
    @FXML private TableView<ScoreVolatile> scoreTable;
    private MarkInputController markInputController;
    
    
	@FxSubscribe public void onScoreNotif(final ScoreNotif event) {
		scoreEvent(event);		
	}
	
    @FXML private void scoreTableAction(MouseEvent event) {
    	handleTableAction(event);
    }
        

	@Override
	protected Node getNodeForInitialization() {
		return scoreTable;
	}

    protected void postInit() throws Exception {
    	scoreTable.setItems(scoreListProperties.getFromPosition(side));
    	// TODO use only on instance
    	// markInputController = inputsBuilder.markInputController();
    	markInputController = inputsBuilder.getSingleton(MarkInputController.class);
    	context.registerEventListener(this);
	}
    
    private void scoreEvent(ScoreNotif event) {    	 		
 		if (event.getMark().getTeam() != side) return;
 		scoreTable.refresh();
    }

	private void handleTableAction(MouseEvent event) {
		@SuppressWarnings("unchecked")
		TableView<ScoreVolatile> table = (TableView<ScoreVolatile>) event.getSource();
    	ScoreVolatile scoreVolatile =  table.getSelectionModel().selectedItemProperty().get();
    	if (scoreVolatile == null) return;
    	
    	markInputController.setInputMode(MarkInputController.InputMode.MODIFICATION);
		markInputController.setPlayers(playerListProperties.getFromPosition(side));
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

}
	