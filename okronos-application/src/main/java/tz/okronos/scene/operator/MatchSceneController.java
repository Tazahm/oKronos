package tz.okronos.scene.operator;


import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import tz.okronos.controller.match.model.MatchDataVolatile;
import tz.okronos.controller.match.model.MatchDataVolatile.GoalkeeperSwap;
import tz.okronos.controller.match.model.MatchDataVolatile.TeamData;
import tz.okronos.controller.pdfexport.event.request.ExportPdfRequest;
import tz.okronos.controller.report.event.request.ReportSaveAsRequest;
import tz.okronos.controller.xlsexport.event.request.ExportXlsRequest;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.PropertyItem;
import tz.okronos.scene.AbstractSceneController;
import tz.okronos.scene.control.TimeEditor;

/**
 *  The controller for the match date.
 */
// @Slf4j
@Component
@Scope("prototype")
public class MatchSceneController extends AbstractSceneController implements Initializable {
    @FXML private PropertySheet matchSheet;
    @Autowired private MatchDataVolatile matchData;
   
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	init();
	}
    
	@FXML private void exportExcelAction(ActionEvent event) {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onExportExcelAction.title", true, 
    			"operator.filetype.xslxFiles", "*.xlsx",
    			"operator.filetype.allFiles", "*.*");
    	if (file != null) {
    		context.postEvent(new ExportXlsRequest().setUrl(KronoHelper.toExternalForm(file)));
    	}
    }
	
	@FXML private void exportPdfAction(ActionEvent event) {
	    	File file = fileSelect("histoPath", "gen/histo", "operator.onExportPdfAction.title", true, 
	    			"operator.filetype.pdfFiles", "*.pdf",
	    			"operator.filetype.allFiles", "*.*");
	    	if (file != null) {
	    		context.postEvent(new ExportPdfRequest().setUrl(KronoHelper.toExternalForm(file)));
	    	}
	    }
	
    @FXML private void saveAsAction(ActionEvent event) {
    	File file = fileSelect("histoPath", "gen/histo", "operator.onSaveAsAction.title", true, 
    			"operator.filetype.xmlFiles", "*.xml",
    			"operator.filetype.allFiles", "*.*");
    	if (file != null) {
    		context.postEvent(new ReportSaveAsRequest().setUrl(KronoHelper.toExternalForm(file)));
    	}
    }
    
    private void init()  {    	
		matchSheet.setMode(PropertySheet.Mode.CATEGORY);
    	matchSheet.setModeSwitcherVisible(false);
    	matchSheet.setSearchBoxVisible(false);

    	final String categoryGeneral = context.getItString("match.category.general");	
    	final String categoryOfficiels = context.getItString("match.category.officiels");	
    	final String categoryTeamLeft = context.getItString("match.category.teamLeft");
    	final String categoryTeamRight = context.getItString("match.category.teamRight");

    	final Map<String, Integer> comparatorMap = new TreeMap<>();
    	comparatorMap.put(categoryGeneral, 1);
    	comparatorMap.put(categoryOfficiels, 2);
    	comparatorMap.put(categoryTeamLeft, 3);
    	comparatorMap.put(categoryTeamRight, 4);
    	Comparator<String> categoryComparator = (a, b) -> comparatorMap.get(a) -  comparatorMap.get(b);    	
    	matchSheet.setCategoryComparator(categoryComparator);

		addProperty(matchData.matchNumber(), context.getItString("match.number"), categoryGeneral);
		addProperty(matchData.location(), context.getItString("match.location"), categoryGeneral);		
		addProperty(matchData.competition(), context.getItString("match.competition"), categoryGeneral);
		addProperty(matchData.group(), context.getItString("match.group"), categoryGeneral);
		addProperty(matchData.date(), context.getItString("match.date"), categoryGeneral);
		addProperty(matchData.beginTime(), context.getItString("match.beginTime"), TimeEditor.class, categoryGeneral);
		addProperty(matchData.endTime(), context.getItString("match.endTime"), TimeEditor.class, categoryGeneral);
		addProperty(matchData.extension(), context.getItString("match.extension"), categoryGeneral);
		addProperty(matchData.reservesBeforeMatch(), context.getItString("match.reserves"), categoryGeneral);
		addProperty(matchData.claim(), context.getItString("match.claim"), categoryGeneral);
		addProperty(matchData.incidentReport(), context.getItString("match.report"), categoryGeneral);
    		
		addProperty(matchData.getReferee1().name(), context.getItString("match.referre.name") + " 1", categoryOfficiels);		
		addProperty(matchData.getReferee1().licence(), context.getItString("match.referre.licence") + " 2", categoryOfficiels);		
		addProperty(matchData.getReferee2().name(), context.getItString("match.referre.name") + " 1", categoryOfficiels);		
		addProperty(matchData.getReferee2().licence(), context.getItString("match.referre.licence") + " 2", categoryOfficiels);		
		addProperty(matchData.getMarker().name(), context.getItString("match.marker.name"), categoryOfficiels);		
		addProperty(matchData.getMarker().licence(), context.getItString("match.marker.licence"), categoryOfficiels);		
		addProperty(matchData.getChrono().name(), context.getItString("match.chrono.name"), categoryOfficiels);		
		addProperty(matchData.getChrono().licence(), context.getItString("match.chrono.licence"), categoryOfficiels);		
    
		addTeamData(matchData.getTeam().getLeft(), categoryTeamLeft);	
		addTeamData(matchData.getTeam().getRight(), categoryTeamRight);
    }
    
    private void addTeamData(final TeamData td, final String category) {
		addProperty(td.timeoutPeriod1(), context.getItString("match.team.period") + " 1", TimeEditor.class, category);		
		addProperty(td.timeoutPeriod2(), context.getItString("match.team.period") + " 2", TimeEditor.class, category);		
		addGoalkeeperSwap(td.getGoalkeeperSwaps(0), 1, category);		
		addGoalkeeperSwap(td.getGoalkeeperSwaps(1), 2, category);		
		addGoalkeeperSwap(td.getGoalkeeperSwaps(2), 3, category);		
    }
    
    private void addGoalkeeperSwap(final GoalkeeperSwap gw, final int nb, final String category) {
    	addProperty(gw.time(), context.getItString("match.goalSwap.time") + " " + nb, TimeEditor.class, category);
    	addProperty(gw.sheet(), context.getItString("match.goalSwap.sheet") + " " + nb, category);
    }
    
	private void addProperty(Property<?> property, String name, String category) {	
		matchSheet.getItems().add(new PropertyItem<>(name, category, property));
	}
	
	private void addProperty(Property<?> property, String name, Class<? extends PropertyEditor<?>> editorClass, String category) {	
		matchSheet.getItems().add(new PropertyItem<>(name, category, property, editorClass));
	}

}
