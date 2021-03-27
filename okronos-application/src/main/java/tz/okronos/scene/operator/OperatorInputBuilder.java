package tz.okronos.scene.operator;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javafx.beans.property.ReadOnlyListProperty;
import tz.okronos.controller.breach.model.BreachDesc;
import tz.okronos.core.KronoContext;
import tz.okronos.scene.ModalControllerMultiScenes;
import tz.okronos.scene.SceneBuilderHelper;

/**
 * Builds all input controllers used by the operator controller.
 * As the controllers are built by java FX they are prototypes.
 * To use them as singleton, use the [{@code getSingleton()} method.
 */
@Configuration
public class OperatorInputBuilder {
	@Autowired private KronoContext context;
	@Autowired private SceneBuilderHelper sceneBuilderHelper;
	@Autowired private ApplicationContext applicationContext;
	@Autowired @Qualifier("breachListProperty")
    private ReadOnlyListProperty<BreachDesc> breachListProperty;
	private final Map<Class<?>, Object> singletonMap = new HashMap<>();
	
	
	/**
	 * Returns a unique input controller following its type. As the controllers are built by java FX they shall be
	 * prototypes, but we needs to use them as singleton. This method returns this singleton.  
	 * @param <T> the input type.
	 * @param aClass its class.
	 * @return the singleton.
	 */
	public <T> T getSingleton(final Class<T> aClass) {
		@SuppressWarnings("unchecked")
		T singleton = (T) singletonMap.get(aClass);
		if (singleton == null) {
			singleton = applicationContext.getBean(aClass);
			singletonMap.put(aClass, singleton);
		}
		return singleton;
	}
	
    @Bean
	@Scope("prototype")
    public PenaltyInputController penaltyInputController() throws Exception {
    	PenaltyInputController controller = sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "penalty.fxml", "penalty.title");
    	ModalControllerMultiScenes penaltyTimeController = penaltyTimeInputController();
    	controller.setSecondaryController(penaltyTimeController);
    	penaltyTimeController.setSecondaryController(controller);
    	penaltyTimeController.setStage(controller.getStage());
    	controller.setBreaches(breachListProperty);
    	
    	return controller;
    }

    @Bean
 	@Scope("prototype")
     public PenaltyTimeInputController penaltyTimeInputController() throws Exception {
    	return sceneBuilderHelper.buildStageAndController("penaltyTime.fxml", "penalty.time.title");
     }


    @Bean
	@Scope("prototype")
    public TimeInputController timeInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "time.fxml", "time.title");
    }
    
    @Bean
	@Scope("prototype")
    public PeriodDurationInputController periodDurationInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "period.fxml", "period.title");
    }

    @Bean
	@Scope("prototype")
    public TeamInputController teamInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "team.fxml", "team.title");
    }

    @Bean
	@Scope("prototype")
    public MarkInputController markInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "mark.fxml", "mark.title");
    }

    @Bean
	@Scope("prototype")
    public SettingsInputController settingsInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "settings.fxml", "settings.title");
    }
    
    @Bean
	@Scope("prototype")
    public PlayerInputController playerInputController() throws Exception {
    	return sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "player.fxml", "player.title");
    }

}
