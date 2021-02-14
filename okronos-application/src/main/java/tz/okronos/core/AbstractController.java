package tz.okronos.core;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all controllers.
 */
public class AbstractController { // implements ContextAware {
	@Autowired protected KronoContext context;
}
