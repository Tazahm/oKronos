package tz.okronos.core.property;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;


/**
 * Allows to bind a property source with a property destination of two different types.
 * The binding is unidirectional, i.e. only modification to the source property is propagated
 * to the destination property. The transformation between both properties is ensure by
 * a converter class.
 */
public class BindingHelper 
{   
	/** An instance of integer to string converter. */
	public static final OneWayConverter<Number, String>  IntegerToString = new IntToStringConverter();
	/** An instance of integer to string converter, with 2 digits. */
	public static final OneWayConverter<Number, String> IntToString2D = new Int2DToStringConverter();
	/** An instance of integer to time converter. */
	public static final OneWayConverter<Number, String> SecondsToTime = new SecondsToTimeConverter();
	/** No values, for positive integer. **/
	public static final int NO_VALUE = -1;
	
	/**
	 * Binds from dst to src. Use unbind to prevent memory leak before all rebind.
	 * @param <S> the type of src.
	 * @param <D> the type of dst.
	 * @param dst the destination.
	 * @param src the source.
	 * @param converter the converter.
	 * @return an object that reflects the binding.
	 */
	public static <S, D>  OneWayBinging <S, D> bind(
			 final  WritableValue<D> dst,
			 final ObservableValue<S> src,
	         final OneWayConverter<S, D> converter) {
		return bind(dst, src, converter, false);
	}
	 
	/**
	 * Binds from dst to src but with a week listener: no need to unbind.
	 * @param <S> the type of src.
	 * @param <D> the type of dst.
	 * @param dst the destination.
	 * @param src the source.
	 * @param converter the converter.
	 * @return an object that reflects the binding.
	 */
	public static <S, D>  OneWayBinging <S, D> bindWeakly(
			 final  WritableValue<D> dst,
			 final ObservableValue<S> src,
	         final OneWayConverter<S, D> converter) {
		return bind(dst, src, converter, true);
	}
	 
	/**
	 * Do the binding.
	 * @param <S> the type of src.
	 * @param <D> the type of dst.
	 * @param dst the destination.
	 * @param src the source.
	 * @param converter the converter.
	 * @param weakly bind weakly ?
	 * @return an object that reflects the binding.
	 */
	private static <S, D>  OneWayBinging <S, D> bind(
			 final  WritableValue<D> dst,
			 final ObservableValue<S> src,
	         final OneWayConverter<S, D> converter,
	         boolean weakly) {
		 @SuppressWarnings("unchecked")
		InvalidationListener listener = o -> dst.setValue(
		    converter.convert(((ObservableValue<S>)o).getValue()));
		if (weakly) listener = new WeakInvalidationListener(listener);
		
		 src.addListener(listener);
		 listener.invalidated(src);
		 return new OneWayBinging<>(src, dst, listener);
	}

	/**
	 * Unbind.
	 * 
	 * @param <S> the type of the source.
	 * @param <D> the type of the destination.
	 * @param binding the binding as returned by the bind method.
	 */
	public static  <S, D> void unbind(OneWayBinging<S, D> binding) {
		binding.getSource().removeListener(binding.getListener());
	}

	
//	public static <S, D>  SimpleLateralizedPair<OneWayBinging<S, D>> bind(
//			 final  WritableValue<D> left,
//			 final  WritableValue<D> right,
//			 final SimpleLateralizedPair<ObservableValue<S>> src,
//	         final OneWayConverter<S, D> converter) {
//		return new SimpleLateralizedPair<>(
//				bind(left, src.getLeft(), converter),
//				bind(right, src.getRight(), converter));
//	}
//
//	public static  <S, D> void unbind2(SimpleLateralizedPair<OneWayBinging<S, D>> bindings) {
//		bindings.getLeft().getSource().removeListener(bindings.getLeft().getListener());
//		bindings.getRight().getSource().removeListener(bindings.getRight().getListener());
//	}


}


