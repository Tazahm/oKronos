package tz.okronos.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.stage.Stage;
import tz.okronos.core.KronoContext;
import tz.okronos.scene.operator.OperatorSceneBuilder;
import tz.okronos.scene.operator.OperatorSceneController;


/**
 * Application main class. 
 * - Initializes Spring injection (builds the context and the controllers).
 * - Starts the javafx main loop.
 * - Log all events from the event bus.
 */
public class KronoApp extends Application {
	
	/**
	 * Start the javafx main loop.
	 * @param args none
	 */
    public static void main(String[] args) {
        Application.launch(KronoApp.class, args);
    }
        
    /**
     * Initializes Spring injection and thus builds the context, play properties and controllers.
     */
    @Override
    public void start(Stage stage) throws Exception {
    	@SuppressWarnings("resource")
		AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
    	annotationContext.scan("tz.okronos");
    	annotationContext.refresh();
    	
    	KronoContext ctx = annotationContext.getBean(KronoContext.class);
    	ctx.setPrimaryStage(stage);

     	OperatorSceneBuilder operatorBuilder = annotationContext.getBean(OperatorSceneBuilder.class);
     	OperatorSceneController operatorSceneController = operatorBuilder.operatorSceneController();
		operatorSceneController.getStage().show();

		ctx.getEventBus().register(this);
		ctx.postEvent(new ResetPlayRequest());
    }

    
    /**
     * Logs all events from the event bus.
     * @param event any event.
     */
    @Subscribe public void logEvent(Object event) {
    	Logger logger = LoggerFactory.getLogger(event.getClass());
    	if (logger.isInfoEnabled()) {
	    	logger.info("Event {}", buildEventMessage(event));
    	}
    }

	/**
	 * Displays the content of an event, for logging.
	 * @param event the event.
	 * @return a user readable version of the event, on many lines.
	 */
    private String buildEventMessage(Object event) {
    	StringBuilder builder = new StringBuilder();
    	buildEventParams(builder, "", event);
    	return builder.toString();
    }
    
    /**
     * Displays recursively the content of an event, for logging. The included objects
     * are printed with some blanks characters before.
     * @param builder where to append.
     * @param prefix blanks characters.
     * @param event the current event.
     */
    private void buildEventParams(StringBuilder builder, String prefix, Object event) {
		Method[] methods = event.getClass().getMethods();
		for (Method method : methods) {
	        String name = method.getName(); 
	        if ((name.startsWith("get") || name.startsWith("is") ) && ! name.equalsIgnoreCase("getClass")
	        		&& method.getParameterCount() == 0) {
		        Object value;
	            try {
					value = method.invoke(event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					value = "unreachable (" + exception.getMessage() + ")";
				}
	            String display = name.startsWith("get") 
	            	? name.substring(3, 4).toLowerCase() + name.substring(4) 
	            	: name;
	            final String print = value == null ? "<null>" : value.toString();
	            // Search for '@' to see if the object if formatted or not.
	            // TODO find a better and more secure way.
	            if (print.contains("@")) {
	            	buildEventParams(builder, display + ".", value);
	            } else {
	            	builder.append("\n");
	            	builder.append("  ");
	            	builder.append(prefix);
	            	builder.append(display);
	            	builder.append(" = ");
	            	builder.append(print);
	            }
	        }
		}   	
    }    
}
