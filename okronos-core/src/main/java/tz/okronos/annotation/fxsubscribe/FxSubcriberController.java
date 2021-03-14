package tz.okronos.annotation.fxsubscribe;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.util.ClassUtils;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles subscriptions done by the {@link FxSubscribe} annotation.
 * <br> 
 * Each classes that used the {@link FxSubscribe} annotation shall be registered with the {@link register} method.
 */
@Slf4j
public class FxSubcriberController {
	
	@Getter private EventBus eventBus;
	// TODO replace both lists by  a weak map indexed with the subscriber containing its proxy : if the subscriber
	// is released, the proxy shall be released as well.
	private List<Object> subscribers;
	private List<FxSubscriber<?>> fxSubscribers;
	
	/**
	 * Creates a subscriber controller for a given event bus.
	 * 
	 * @param eventBus the target event bus.
	 */
	public FxSubcriberController(EventBus eventBus) {
		this.eventBus = eventBus;
		subscribers = new ArrayList<>();
		fxSubscribers = new ArrayList<>();
	}

	/**
	 * Registers the target class with the event bus handled by that controller.
	 * 
	 * @param <T> the class of the instance registered.
	 * @param object the instance to be registered.
	 */
	public <T> void register(T object) {
		Class<?> objectClass = object.getClass();
		
		log.debug("Register " + objectClass.getName());
		if (hasMethodAnnotatedWith(objectClass, FxSubscribe.class)) {
			// If the CGLIB is user, the input object can be a class generated by this library.
			// This is why we search for the former class.
			Class<?> userClass = ClassUtils.getUserClass(objectClass);
			String genratedName = FxSubscriberProcessor.computeGeneratedClassName(userClass.getName());
			try {
				log.debug("FxSubscribe " + genratedName);
				Class<?> generatedClass = Class.forName(genratedName);
				@SuppressWarnings("unchecked")
				FxSubscriber<T> generatedInstance = (FxSubscriber<T>) generatedClass.getDeclaredConstructor().newInstance();
				generatedInstance.setTarget(object);
				
				fxSubscribers.add(generatedInstance);
				eventBus.register(generatedInstance);
			} catch (ReflectiveOperationException  | SecurityException exception) {
				log.error("Cannot register " + object.getClass().getName(), exception);
			}
		}
		
		if (hasMethodAnnotatedWith(object.getClass(), Subscribe.class)) {
			log.debug("Subscribe " + object.getClass());
			
			subscribers.add(object);
			eventBus.register(object);
		}
	}
	
	/**
	 * Unregisters the target class from the event bus handled by that controller.
	 * 
	 * @param <T> the class of the instance registered.
	 * @param object the instance to be unregistered.
	 */
	public <T> void unregister(T object) {
		log.debug("Unregister " + object.getClass());
		if (subscribers.remove(object)) {
			eventBus.unregister(object);
		}
		
		Optional<FxSubscriber<?>> fxOptional = fxSubscribers.stream().filter(s->object == s.getTarget()).findFirst();
		if (fxOptional.isPresent()) {
			@SuppressWarnings("unchecked")
			FxSubscriber<T> fxSubscriber = (FxSubscriber<T>) fxOptional.get();
			log.debug("Unregister " + fxSubscriber.getClass());
			fxSubscribers.remove(fxSubscriber);
			eventBus.unregister(fxSubscriber);
		}
		
	}
	
	/**
	 * Checks if the class {@code source} has got a method decorated by the annotation {@code reference}.
	 * 
	 * @param source the class to check.
	 * @param reference the annotation.
	 * @return true if the class has got a method decorated, false otherwise.
	 */
	private boolean hasMethodAnnotatedWith(Class<?> source, Class<? extends Annotation> reference) {
		Optional<?> res = Stream.of(source.getMethods()).filter(m -> m.getAnnotation(reference) != null).findFirst();
		if (res.isPresent()) {
			return true;
		}
		
		Class<?> parent = source.getSuperclass();
		return (parent != null && hasMethodAnnotatedWith(parent, reference));
	}
}