package tz.okronos.controller.playtime.model;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import lombok.Getter;


@Component
public class PlayTimeModel {
	@Getter private ReadOnlyIntegerWrapper forwardTimeWrapper;
	@Getter private ReadOnlyIntegerWrapper backwardTimeWrapper;
	@Getter private ReadOnlyIntegerWrapper cumulativeTimeWrapper;
	@Getter private ReadOnlyBooleanWrapper playTimeRunningWrapper;    

    public PlayTimeModel() {
    	forwardTimeWrapper = new ReadOnlyIntegerWrapper();
		backwardTimeWrapper = new ReadOnlyIntegerWrapper();
		cumulativeTimeWrapper = new ReadOnlyIntegerWrapper();
		playTimeRunningWrapper = new ReadOnlyBooleanWrapper();
    }
    
    @Bean
    ReadOnlyIntegerProperty forwardTimeProperty() {
    	return forwardTimeWrapper.getReadOnlyProperty();
    }
    
    @Bean
    ReadOnlyIntegerProperty backwardTimeProperty() {
    	return backwardTimeWrapper.getReadOnlyProperty();
    }
    
    @Bean
    ReadOnlyIntegerProperty cumulativeTimeProperty() {
    	return cumulativeTimeWrapper.getReadOnlyProperty();
    }
    
    @Bean
    ReadOnlyBooleanProperty playTimeRunningProperty() {
    	return playTimeRunningWrapper.getReadOnlyProperty();
    }
}