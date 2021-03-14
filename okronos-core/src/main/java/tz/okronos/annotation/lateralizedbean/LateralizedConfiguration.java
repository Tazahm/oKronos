package tz.okronos.annotation.lateralizedbean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 *  For each class that exposes this interface, two Spring instances are created, one for the right side
 *  and the others for the left side. If the class inherits from the {@code SideAware} interface, each instance
 *  is assigned a side with the {@code setSide()} method.
 *  <p>
 *  Two bean instances are created for each method tagged with {@link LateralizedBean}, one for the right side instance
 *  and others for the left side instance.
 *  <p>
 *  If a method {@code myMethod()} is tagged with {@code LateralizedBean}, the beans that are created are stored into a 
 *  instance of the Lateralized class. This class can be retrieve in a Spring bean usual way with the qualifier 
 *  {@code myMethodLateralized}
 *  <p>
 *  Each method tagged as {@link LateralizedPostConstruct} is an initialization method similar to Sprint {@code PostConstruct}
 *  <p>
 *  <pre>
 *  Example
 *    
 *    &#64;LateralizedConfiguration
 *    public class MyController implements Lateralized, SideAware {
 *    
 *    &#64;LateralizedBean
 *	  MyProperty myProperty() { return new MyProperty() };
 *      ...
 *    }
 *    
 *    public class MyClient {
 *        &#64;Autowired
 *        &#64;Qualifier("myPropertyLateralized") 
 *        private Lateralized<MyProperty> myControlerPropertyLateralized;
 *        
 *        public void run() {
 *           MyProperty leftProperty = myControlerPropertyLateralized.getLeft();
 *           ...
 *        }
 *    }
 *  </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@Scope("prototype")
public @interface LateralizedConfiguration {

}
