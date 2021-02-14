package tz.okronos.scene.operator;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.Property;
import tz.okronos.controller.match.model.MatchDataVolatile;
import tz.okronos.controller.match.model.MatchDataVolatile.GoalkeeperSwap;
import tz.okronos.controller.match.model.MatchDataVolatile.TeamData;
import tz.okronos.core.AbstractController;
import tz.okronos.core.PropertyItem;
import tz.okronos.scene.control.TimeEditor;


/**
 *   Allows to modify some settings before a new match begin : play durations and team names.
 */
public class MatchDataInputController extends AbstractController {	
	private PropertySheet matchSheet;	
	@Autowired private MatchDataVolatile matchData;

	
	public MatchDataInputController() {
	}
	
    // @PostConstruct 
    public void init(final PropertySheet matchSheet)  {
    	this.matchSheet = matchSheet;
    	
		matchSheet.setMode(PropertySheet.Mode.CATEGORY);
    	// matchSheet.setMode(PropertySheet.Mode.NAME);
    	matchSheet.setModeSwitcherVisible(false);
    	matchSheet.setSearchBoxVisible(false);
		
		String categoryGeneral = context.getItString("match.category.general");		
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
    
		String categoryOfficiels = context.getItString("match.category.officiels");	
		addProperty(matchData.getReferee1().name(), context.getItString("match.referre.name") + " 1", categoryOfficiels);		
		addProperty(matchData.getReferee1().licence(), context.getItString("match.referre.licence") + " 2", categoryOfficiels);		
		addProperty(matchData.getReferee2().name(), context.getItString("match.referre.name") + " 1", categoryOfficiels);		
		addProperty(matchData.getReferee2().licence(), context.getItString("match.referre.licence") + " 2", categoryOfficiels);		
		addProperty(matchData.getMarker().name(), context.getItString("match.marker.name"), categoryOfficiels);		
		addProperty(matchData.getMarker().licence(), context.getItString("match.marker.licence"), categoryOfficiels);		
		addProperty(matchData.getChrono().name(), context.getItString("match.chrono.name"), categoryOfficiels);		
		addProperty(matchData.getChrono().licence(), context.getItString("match.chrono.licence"), categoryOfficiels);		
    
		String categoryTeamLeft = context.getItString("match.category.teamLeft");	
		addTeamData(matchData.getTeam().getLeft(), categoryTeamLeft);		
		String categoryTeamRight = context.getItString("match.category.teamRight");	
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
