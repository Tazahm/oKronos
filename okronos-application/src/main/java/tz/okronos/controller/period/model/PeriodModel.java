package tz.okronos.controller.period.model;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import lombok.Getter;
import tz.okronos.core.PhaseOfPlay;


@Component
public class PeriodModel {
	@Getter private ReadOnlyObjectWrapper<PhaseOfPlay> phaseWrapper;
	@Getter private ReadOnlyIntegerWrapper playTimeDurationWrapper;
	@Getter private ReadOnlyIntegerWrapper halfTimeDurationWrapper;
	@Getter private ReadOnlyIntegerWrapper timeoutDurationWrapper;
	@Getter private ReadOnlyIntegerWrapper warmupDurationWrapper;
	@Getter private ReadOnlyIntegerWrapper phaseDurationWrapper;
	@Getter private ReadOnlyStringWrapper periodLabelWrapper;
	@Getter private ReadOnlyIntegerWrapper periodCountWrapper;
	
	
	public PeriodModel() {
		phaseWrapper = new ReadOnlyObjectWrapper<PhaseOfPlay>();
		phaseDurationWrapper = new ReadOnlyIntegerWrapper();
		periodLabelWrapper = new ReadOnlyStringWrapper(); 
		periodCountWrapper = new ReadOnlyIntegerWrapper();
		playTimeDurationWrapper = new ReadOnlyIntegerWrapper();
		halfTimeDurationWrapper = new ReadOnlyIntegerWrapper();
		timeoutDurationWrapper = new ReadOnlyIntegerWrapper();
		warmupDurationWrapper = new ReadOnlyIntegerWrapper();
	}
	
	@Bean
	public ReadOnlyObjectProperty<PhaseOfPlay> phaseProperty() {
		return phaseWrapper.getReadOnlyProperty();
	}
	
	@Bean
	public ReadOnlyIntegerProperty playTimeDurationProperty() {
		return playTimeDurationWrapper.getReadOnlyProperty();
	}
	
	@Bean
	public ReadOnlyIntegerProperty halfTimeDurationProperty() {
		return halfTimeDurationWrapper.getReadOnlyProperty();
	}
	
	@Bean
	public ReadOnlyIntegerProperty timeoutDurationProperty() {
		return timeoutDurationWrapper.getReadOnlyProperty();
	}
	
	@Bean
	public ReadOnlyIntegerProperty warmupDurationProperty() {
		return warmupDurationWrapper.getReadOnlyProperty();
	}

	@Bean
	public ReadOnlyIntegerProperty phaseDurationProperty() {
		return phaseDurationWrapper.getReadOnlyProperty();
	}

	@Bean
	public ReadOnlyStringProperty periodLabelProperty() {
		return periodLabelWrapper.getReadOnlyProperty();
	}

	@Bean
	public ReadOnlyIntegerProperty periodCountProperty() {
		return periodCountWrapper.getReadOnlyProperty();
	}
}