package tz.okronos.controller.breach.model;

import java.util.Comparator;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import tz.okronos.core.KronoHelper;


@Component
public class BreachModel {
	private static Comparator<BreachDesc> breachComparator = Comparator.comparing(BreachDesc::getCode);

	@Getter private SimpleListProperty<BreachDesc> breachListUnordered; 
	@Getter private SortedList<BreachDesc> breachOrderedList;
	@Getter private ReadOnlyListWrapper<BreachDesc> breachListWrapper;
	
	
	public BreachModel() {
		breachListUnordered = KronoHelper.createListProperty();
		breachOrderedList = new SortedList<>(breachListUnordered, breachComparator);
		breachListWrapper = KronoHelper.createListWrapper();
		Bindings.bindContent(breachListWrapper, breachOrderedList);		
	}

	@Bean
	ReadOnlyListProperty<BreachDesc> breachListProperty() {
    	return breachListWrapper.getReadOnlyProperty();
    }
  	
}