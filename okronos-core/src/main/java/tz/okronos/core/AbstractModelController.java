package tz.okronos.core;


/**
 * Base class for all controllers than handles a part of the model.
 * <p>
 * The model is intended to contains some ReadOnlyXxxWrapper instances, each values of this
 * instances shall be exposed as read only object as following :
 * <pre>
 * (at)Bean
 *   ReadOnlyIntegerProperty xxxProperty() {
 *   	return xxxWrapper.getReadOnlyProperty();
 *   }
 * </pre>
 * In order to allow the spring framework to wave the links, and in order to prevent loop wiring, 
 * all exposed properties shall be contains into the model instance. Because of this problem of loop wiring,
 * the model shall not be contains directly into the controller but into the model instance.
 * <p>
 * The links between object shall be as following :
 * <pre>
 * controller 1 ---contains--> model 1 +--contains---> Wrapper 1.1
 *                                     |                   ^
 *                                     |                   |  getReadOnlyProperty()
 *                                     ---expose  ---> ReadOnlyProperty 1
 *                                                           ^
 *                                     ...                   |
 *                                                           |
 * controller 2 ---uses--------------------------------------+
 * </pre>
 * @param <M> the class that depicts the part of the model this controller manages.
 */
public abstract class AbstractModelController<M> extends AbstractController {
	protected abstract M getModel();
}
