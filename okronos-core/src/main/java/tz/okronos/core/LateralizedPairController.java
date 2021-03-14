package tz.okronos.core;

/**
 * Contains an instance of controller for the right side of the field and another for the left.
 * 
 * @param <T> the type of controller.
 */
public class LateralizedPairController<T extends AbstractController> 
    extends SimpleLateralizedPair<T>  {
	
	public LateralizedPairController() {
		super();
	}
	
	public LateralizedPairController(T left, T right) {
		super(left, right);
	}
	
	@Override
	public LateralizedPairController<T> clone() {
		return new LateralizedPairController<>(getLeft(), getRight());
	}

}
