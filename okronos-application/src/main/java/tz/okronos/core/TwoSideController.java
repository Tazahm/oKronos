package tz.okronos.core;

/**
 * Contains an instance of controller for the right side of the field and another for the left.
 * 
 * @param <T> the type of controller.
 */
public class TwoSideController<T extends AbstractController> 
    extends TwoSide<T>  {
	
	public TwoSideController() {
		super();
	}
	
	public TwoSideController(T left, T right) {
		super(left, right);
		
		if (left instanceof SideAware) {
			((SideAware) left).setSide(PlayPosition.LEFT);
		}
		
		if (right instanceof SideAware) {
			((SideAware) right).setSide(PlayPosition.RIGHT);
		}
	}
	
	@Override
	public TwoSideController<T> clone() {
		return new TwoSideController<>(getLeft(), getRight());
	}

}
