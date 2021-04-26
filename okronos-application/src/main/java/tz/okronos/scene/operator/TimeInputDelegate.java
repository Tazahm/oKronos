package tz.okronos.scene.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 *  Allows to modify the current play time.
 */
@Component
@Scope("prototype")
public class TimeInputDelegate extends TimeInputAbstractDelegate {
    @Autowired @Qualifier("forwardTimeProperty") private ReadOnlyIntegerProperty forwardTimeProperty;
	
	@Override
	public ReadOnlyIntegerProperty getSourceProperty() {
		return forwardTimeProperty;
	}

}
