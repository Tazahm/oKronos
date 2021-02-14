package tz.okronos.controller.team.model;

import java.util.Comparator;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.transformation.SortedList;
import javafx.scene.image.Image;
import lombok.Getter;
import tz.okronos.core.KronoHelper;


@Configuration
@Scope("prototype")
public class TeamModel {
	
	public static final String ReportIdPreffix = "team";
	private static Comparator<PlayerSnapshot> uidComparator = (a, b) -> (int) (b.getUid() - a.getUid());
	private static Comparator<PlayerSnapshot> goalComparator 
		= (a, b) -> (a.isGoalkeeper() ? 1 : 0) - (b.isGoalkeeper() ? 1 : 0);
	private static Comparator<PlayerSnapshot> officialComparator 
		= (a, b) -> (a.isOfficial() ? 1 : 0) - (b.isOfficial() ? 1 : 0);
	private static Comparator<PlayerSnapshot> shirtComparator 
		= (a, b) -> a.getShirt() - b.getShirt() ;
	private static Comparator<PlayerSnapshot> playerComparator 
	    = officialComparator
	    	.thenComparing(goalComparator)
	    	.thenComparing(shirtComparator)
	    	.thenComparing(uidComparator);
	
    @Getter private ReadOnlyStringWrapper teamNameWrapper;
    @Getter private ReadOnlyObjectWrapper<Image> teamImageWrapper;
    @Getter private ReadOnlyListWrapper<PlayerSnapshot> playerListWrapper;
    @Getter private SortedList<PlayerSnapshot> sortedPlayerList;
    @Getter private ReadOnlyListWrapper<PlayerSnapshot> playerSortedListWrapper;
	
	
	public TeamModel() {
		teamNameWrapper = new ReadOnlyStringWrapper();
		teamImageWrapper = new ReadOnlyObjectWrapper<Image>();
		playerListWrapper = KronoHelper.createListWrapper();
		
		sortedPlayerList = new SortedList<>(playerListWrapper, playerComparator);
		playerSortedListWrapper = KronoHelper.createListWrapper();
		Bindings.bindContent(playerSortedListWrapper, sortedPlayerList);
	}

	public ReadOnlyStringProperty teamNameProperty() {
		return teamNameWrapper.getReadOnlyProperty();
	}	
	
	public ReadOnlyObjectProperty<Image> teamImageProperty() {
		return teamImageWrapper.getReadOnlyProperty();
	}
	
    public ReadOnlyListProperty<PlayerSnapshot> playerListProperty() {
    	return playerSortedListWrapper.getReadOnlyProperty();
    }

}


