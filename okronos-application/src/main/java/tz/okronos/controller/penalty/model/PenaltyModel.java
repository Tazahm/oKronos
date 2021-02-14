package tz.okronos.controller.penalty.model;

import java.util.Comparator;
import java.util.function.Predicate;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import tz.okronos.core.KronoHelper;


@Component
@Scope("prototype")
public class PenaltyModel {
	private static Predicate<PenaltyVolatile> scoreFilter = p -> ! p.isValidated() && ! p.isPending();
	private static Predicate<PenaltyVolatile> liveFilter = p -> ! p.isValidated();
	private static Comparator<PenaltyVolatile> uidComparator = (a, b) -> (int) (b.getUid() - a.getUid());
	private static Comparator<PenaltyVolatile> remainderComparator = (a, b) -> a.getRemainder() - b.getRemainder();
	private static Comparator<PenaltyVolatile> startTimeComparator = (a, b) -> b.getStartTime() - a.getStartTime();
	private static Comparator<PenaltyVolatile> penaltyLiveComparator 
	    = remainderComparator.thenComparing(uidComparator);
	private static Comparator<PenaltyVolatile> penaltyHistoryComparator 
	    = startTimeComparator.thenComparing(uidComparator);
	
	
	// All list shall be registered as class variable because some
	// containers use weak references, the list would contains not data
	// if declared as method variables.
	@Getter private SimpleListProperty<PenaltyVolatile> penaltyMainList;
	
	@Getter private FilteredList<PenaltyVolatile> penaltyLiveUnorderedList;
	@Getter private SortedList<PenaltyVolatile> penaltyLiveOrderedList;
	@Getter private ReadOnlyListWrapper<PenaltyVolatile> penaltyLiveListWrapper;
	
	@Getter private SortedList<PenaltyVolatile> penaltyHistoryOrderedList;
	@Getter private ReadOnlyListWrapper<PenaltyVolatile> penaltyHistoryListWrapper;
	
	@Getter private FilteredList<PenaltyVolatile> penaltyScoreListUnordered;
	@Getter private SortedList<PenaltyVolatile> penaltyScoreListOrdered;
	@Getter private ReadOnlyListWrapper<PenaltyVolatile> penaltyScoreListWrapper;
	
	
	public PenaltyModel() {	
		// Creates the unordered penalty list as well as the control penalty list.
		// Binds the control penalty list with a sorted version of the  unordered penalty list.
		
		// Builds the list source of all others.
		penaltyMainList = KronoHelper.createListProperty();
		
		// Builds the live list property and intermediary containers.
		penaltyLiveUnorderedList = new FilteredList<PenaltyVolatile>(penaltyMainList, liveFilter);
		penaltyLiveOrderedList = new SortedList<>(penaltyLiveUnorderedList, penaltyLiveComparator);
		penaltyLiveListWrapper = KronoHelper.createListWrapper();
		Bindings.bindContent(penaltyLiveListWrapper, penaltyLiveOrderedList);
		
		// Creates the history score penalty list and intermediary containers.
		penaltyHistoryOrderedList = new SortedList<>(penaltyMainList, penaltyHistoryComparator);
		penaltyHistoryListWrapper = KronoHelper.createListWrapper();
		Bindings.bindContent(penaltyHistoryListWrapper, penaltyHistoryOrderedList);

		// Creates the score penalty list and intermediary containers.
		penaltyScoreListUnordered = new FilteredList<PenaltyVolatile>(penaltyMainList, scoreFilter);
		penaltyScoreListOrdered = new SortedList<>(penaltyScoreListUnordered, penaltyLiveComparator);
		penaltyScoreListWrapper = KronoHelper.createListWrapper();
		
		Bindings.bindContent(penaltyScoreListWrapper, penaltyScoreListOrdered);
	}
	
	public ReadOnlyListProperty<PenaltyVolatile> penaltyHistoryListProperty() {
    	return penaltyHistoryListWrapper.getReadOnlyProperty();
    }
	
	public ReadOnlyListProperty<PenaltyVolatile> penaltyLiveListProperty() {
    	return penaltyLiveListWrapper.getReadOnlyProperty();
    }
	
	public ReadOnlyListProperty<PenaltyVolatile> penaltyScoreListProperty() {
    	return penaltyScoreListWrapper.getReadOnlyProperty();
    }

}


