package tz.okronos.scene.operator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import tz.okronos.core.KronoContext;
import tz.okronos.scene.ModalControllerMultiScenes;
import tz.okronos.scene.SceneBuilderHelper;

/**
 * Builds all input controllers used by the operator controller.
 */
@Configuration
public class OperatorInputBuilder {
	@Autowired private KronoContext context;
	@Autowired SceneBuilderHelper sceneBuilderHelper;

	
    /**
	 * Builds the penalty view.
	 * @param primary the main stage.
	 * @return the controller.
	 * @throws Exception on any errors.
	 */
    @Bean
	@Scope("prototype")
    public PenaltyInputController penaltyInputController() throws Exception {
    	PenaltyInputController controller = sceneBuilderHelper.buildModalStage(context.getPrimaryStage(), "penalty.fxml", "penalty.title");
    	ModalControllerMultiScenes penaltyTimeController = penaltyTimeInputController();
    	controller.setSecondaryController(penaltyTimeController);
    	penaltyTimeController.setSecondaryController(controller);
    	penaltyTimeController.setStage(controller.getStage());
    	
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
    
    @Bean
	@Scope("prototype")
    public MatchDataInputController matchDataInputController() {
    	return new MatchDataInputController();
    }
    
   

}
