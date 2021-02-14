package tz.okronos.scene.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 *  Allows to modify the current play time.
 */
public class TimeInputController extends TimeInputAbstractController {
    @Autowired @Qualifier("forwardTimeProperty") private ReadOnlyIntegerProperty forwardTimeProperty;
	
	@Override
	protected ReadOnlyIntegerProperty getSourceProperty() {
		return forwardTimeProperty;
	}

}
