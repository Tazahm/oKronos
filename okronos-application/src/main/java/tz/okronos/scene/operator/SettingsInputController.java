package tz.okronos.scene.operator;

import java.util.function.BooleanSupplier;

import javax.annotation.PostConstruct;

import org.controlsfx.control.PropertySheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Setter;
import tz.okronos.controller.period.event.request.PeriodDurationsModificationRequest;
import tz.okronos.controller.playtime.event.request.PlayTimeResetPeriodRequest;
import tz.okronos.core.PropertyItem;
import tz.okronos.scene.ModalController;


/**
 *   Allows to modify some settings before a new match begin : play durations and team names.
 */
public class SettingsInputController extends ModalController {
		
	@FXML private PropertySheet propertySheet;

	@Autowired @Qualifier("playTimeDurationProperty") 
	private ReadOnlyIntegerProperty playTimeDurationProperty;
	@Autowired @Qualifier("halfTimeDurationProperty") 
	private ReadOnlyIntegerProperty halfTimeDurationProperty;
	@Autowired @Qualifier("timeoutDurationProperty") 
	private ReadOnlyIntegerProperty timeoutDurationProperty;
	@Autowired @Qualifier("warmupDurationProperty") 
	private ReadOnlyIntegerProperty warmupDurationProperty;

	@Setter private BooleanSupplier loadCallback;
	private SimpleIntegerProperty userPlayTimeDurationProperty;
	private SimpleIntegerProperty userHalfTimeDurationProperty;
	private SimpleIntegerProperty userTimeoutDurationProperty;
	private SimpleIntegerProperty userWarmupDurationProperty;

	
	public SettingsInputController() {
		userPlayTimeDurationProperty = new SimpleIntegerProperty();
		userHalfTimeDurationProperty = new SimpleIntegerProperty();
		userTimeoutDurationProperty = new SimpleIntegerProperty();
		userWarmupDurationProperty = new SimpleIntegerProperty();
	}
	
    @PostConstruct 
    public void init()  {
//		propertySheet.setMode(PropertySheet.Mode.CATEGORY);
		propertySheet.setMode(PropertySheet.Mode.NAME);
		propertySheet.setModeSwitcherVisible(false);
		propertySheet.setSearchBoxVisible(false);
		
		String categoryDuration = context.getItString("settings.category.duration");
		
		addProperty(userPlayTimeDurationProperty, context.getItString("settings.duration.play"), categoryDuration);
		addProperty(userHalfTimeDurationProperty, context.getItString("settings.duration.halfTime"), categoryDuration);
		addProperty(userTimeoutDurationProperty, context.getItString("settings.duration.timeout"), categoryDuration);
		addProperty(userWarmupDurationProperty, context.getItString("settings.duration.warmup"), categoryDuration);
		
		initProperties();		
	}
	
	@FXML private void loadAction(ActionEvent event) {		
		stage.hide();
		if (loadCallback != null && ! loadCallback.getAsBoolean()) {
			stage.show();
		}
	}
	
	@Override
	protected void doValidateAction(ActionEvent event) {
		super.doValidateAction(event);
		updateProperties();
	}
	
	
	private void initProperties() {
		userPlayTimeDurationProperty.set(playTimeDurationProperty.get());
		userHalfTimeDurationProperty.set(halfTimeDurationProperty.get());
		userTimeoutDurationProperty.set(timeoutDurationProperty.get());
		userWarmupDurationProperty.set(warmupDurationProperty.get());
//		userTeamNameProperties.getLeft().set(teamNameProperties.getLeft().get());
//		userTeamNameProperties.getRight().set(teamNameProperties.getRight().get());
	}
	
	
	private void updateProperties() {
//		context.postEvent(new TeamNameModificationRequest()
//				.setSide(PlayPosition.LEFT).setTeamName(userTeamNameProperties.getLeft().get()));
//		context.postEvent(new TeamNameModificationRequest()
//				.setSide(PlayPosition.RIGHT).setTeamName(userTeamNameProperties.getRight().get()));
		context.postEvent(new PeriodDurationsModificationRequest()
			.setPlayTimeDuration(userPlayTimeDurationProperty.get())
			.setHalfTimeDuration(userHalfTimeDurationProperty.get())
			.setTimeoutDuration(userTimeoutDurationProperty.get())
			.setWarmupDuration(userWarmupDurationProperty.get()));
		context.postEvent(new PlayTimeResetPeriodRequest());
	}
	
	private void addProperty(Property<?> property, String name, String category) {	
			propertySheet.getItems().add(new PropertyItem<>(name, category, property));
	}
}
