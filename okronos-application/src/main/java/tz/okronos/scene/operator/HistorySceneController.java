package tz.okronos.scene.operator;


import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import tz.okronos.controller.record.model.EventRecord;
import tz.okronos.scene.AbstractSceneController;

/**
 *  The controller for the history list.
 */
// @Slf4j
@Component
@Scope("prototype")
public class HistorySceneController extends AbstractSceneController implements Initializable {
    @Autowired @Qualifier("historyListProperty")
    private ReadOnlyListProperty<EventRecord<?>> historyListProperty;
    @FXML private TableView<EventRecord<?>> historyTable;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	init();
	}	
    
    private void init()  {    	
    	historyTable.setItems(historyListProperty);    	
    }
}
