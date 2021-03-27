package tz.okronos.controller.score.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListWrapper;
import lombok.Getter;
import tz.okronos.core.KronoHelper;


@Component
@Scope("prototype")
public class ScoreModel {
    @Getter private ReadOnlyListWrapper<ScoreVolatile> scoreListWrapper;
    @Getter private ReadOnlyIntegerWrapper scoreWrapper;
    @Getter private ReadOnlyBooleanWrapper scoreDecAllowedWrapper;
    @Getter private ReadOnlyBooleanWrapper scoreIncAllowedWrapper;
	
	public ScoreModel() {
		scoreListWrapper = KronoHelper.createListWrapper();
		scoreWrapper = new ReadOnlyIntegerWrapper();
		scoreDecAllowedWrapper = new ReadOnlyBooleanWrapper();
		scoreIncAllowedWrapper = new ReadOnlyBooleanWrapper();
	}
}
