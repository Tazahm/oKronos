package tz.okronos.scene.operator;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.team.event.notif.TeamPlayerModificationNotif;
import tz.okronos.controller.team.event.request.TeamExportRequest;
import tz.okronos.controller.team.event.request.TeamImportExportRequest;
import tz.okronos.controller.team.event.request.TeamImportRequest;
import tz.okronos.controller.team.event.request.TeamPlayerCreationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerDeletionRequest;
import tz.okronos.controller.team.event.request.TeamPlayerModificationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerRequest;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.scene.AbstractSceneControllerLateralized;


/**
 *  The controller for the players list.
 */
@Component
@Scope("prototype")
public class PlayerSceneController extends AbstractSceneControllerLateralized {
    @FXML private BorderPane playerPane;
    @FXML private TableView<PlayerSnapshot> playerTable;
    @Autowired @Qualifier("playerListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
    private PlayerInputController playerInputController;
    @Autowired private OperatorInputBuilder inputsBuilder;
    
    
    @FXML private void playerTableAction(MouseEvent event) {
    	modifyPlayer(event);
    }
    
    @FXML private void addPlayerAction(ActionEvent event) {    	
    	addPlayer();
    }
    
    @FXML private void importPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(false);
    }
    
    @FXML private void exportPlayerAction(ActionEvent event) {
    	importOrExportPlayerAction(true);
    }
    
    @FxSubscribe public void onTeamPlayerModificationNotif(TeamPlayerModificationNotif notif) {
    	playerTable.refresh();
    }
    
	@Override
	protected Node getNodeForInitialization() {
		return playerPane;
	}

	@Override
    protected void postInit() throws Exception {
    	playerTable.setItems(playerListProperties.getFromPosition(side));
    	playerInputController = inputsBuilder.playerInputController();
    	
    	context.registerEventListener(this);
	}
    
    private void addPlayer() {
    	while (true) {
	    	playerInputController.setInputMode(PlayerInputController.InputMode.CREATION);
	    	playerInputController.clear();
	    	playerInputController.showModal();
	    	if (playerInputController.isCancelled()) return;
	    	PlayerSnapshot player = playerInputController.getPlayer();
	    	player.setTeam(side);
	    	context.postEvent(new TeamPlayerCreationRequest().setPlayer(player).setSide(side));
    	}
    }
    
    private void modifyPlayer(MouseEvent event) {
    	@SuppressWarnings("unchecked")
		TableView<PlayerSnapshot> table = (TableView<PlayerSnapshot>) event.getSource();
    	PlayerSnapshot player =  table.getSelectionModel().selectedItemProperty().get();
    	if (player == null) return;
    	    	
    	playerInputController.setInputMode(PlayerInputController.InputMode.MODIFICATION);
    	playerInputController.setPlayer(player);
    	playerInputController.showModal();
    	if (playerInputController.isCancelled()) return;
    	
    	PlayerSnapshot output;
    	if (playerInputController.isShallDelete()) {
    		output = player;
    	} else {
	    	output = playerInputController.getPlayer();
	    	output.setTeam(side);
	    	output.setUid(player.getUid());
    	}
    	
    	TeamPlayerRequest request = playerInputController.isShallDelete()
    		? new TeamPlayerDeletionRequest()
    		: new TeamPlayerModificationRequest();
    	context.postEvent(request.setPlayer(output).setSide(side));
    }
    
    private void importOrExportPlayerAction(boolean export) {    	  		
    	String titleProp = export 
        			? "operator.onImportPlayerAction.title"
        			: "operator.onExportPlayerAction.title";
    	File file = fileSelect("histoPath", "gen/histo", titleProp, export, 
    			"operator.filetype.csvFiles", "*.csv",
    			"operator.filetype.allFiles", "*.*");
    		
    	if (file != null && (export || file.canRead())) {
    		importOrExport(file, export ? new TeamExportRequest() : new TeamImportRequest());
    	}
    }
    
    private void importOrExport(File file, TeamImportExportRequest request) {
		context.postEvent(request
			.setUrl(KronoHelper.toExternalForm(file))
			.setSide(side));
    }

}
	