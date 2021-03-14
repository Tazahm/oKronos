package tz.okronos.core;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

import tz.okronos.annotation.fxsubscribe.FxSubcriberController;


/**
 * Builds the context of the application.
 */
@Configuration
public class ContextBuilder {

    @Bean
    public KronoContext context() throws FileNotFoundException, IOException {
		// Do not log before log configuration into readProperties().
    	KronoContext output = new KronoContext();
    	output.readProperties();
	
		EventBus eventBus = new EventBus();
		output.setEventBus(eventBus);

		FxSubcriberController subcriberController = new FxSubcriberController(eventBus);
		output.setFxSubcriberController(subcriberController);

		return output;
    }
}
