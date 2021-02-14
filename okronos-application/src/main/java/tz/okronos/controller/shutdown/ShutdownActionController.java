package tz.okronos.controller.shutdown;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;
import tz.okronos.controller.shutdown.event.notif.ShutdownVetoNotif;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.TimerEnabler;

/**
 *  Shutdowns the application. When it receives a ShutdownRequest, wait 1 second
 *  to see if any controller sends a ShutdownVetoNotif event with lock = true.
 *  In such a case, waits for a matching ShutdownVetoNotif event with lock = false.
 *  When the list of ShutdownVetoNotif is empty, exit the javafx platform and next the 
 *  application. In any cases shutdown the application after 10 seconds.
 */
@Slf4j
@Component
public class ShutdownActionController  extends AbstractController {

    private Map<String, ShutdownVetoNotif> vetoMap;
    private TimerEnabler timer;
    private int tryCount;

    private void process() {
    	context.getScoreSceneController().getStage().hide();
    	context.getOperatorSceneController().getStage().hide();
    	
    	tryCount ++;
    	if (! vetoMap.isEmpty() && tryCount > 10) {
    		log.info("Force exit");
    	} else if (! vetoMap.isEmpty()) {
    		log.info("Cannot exit due to shutdown locks");
    		return;
    	}
    	log.info("Exit the platform");
    	Platform.exit(); 
    	log.info("Exit the jvm");
    	System.exit(0);
    }

    @PostConstruct 
    public void init()  {
		vetoMap = new HashMap<>();
		context.registerEventListener(this);
	}

	@FxSubscribe public void onShutdownRequest(final ShutdownRequest event) {
		timer = new TimerEnabler();
		timer.fxSchedule(() -> process(), 1000, 1000);
	}
	
	@FxSubscribe public void onShutdownAnswer(final ShutdownVetoNotif event) {
		final String src = event.getRequester();
		if (event.isLock()) {
			vetoMap.put(src, event);
		} else {
			if (vetoMap.remove(src) == null) {
				log.warn("No matching veto for " + src);
			};
		}
	}	
	
}
