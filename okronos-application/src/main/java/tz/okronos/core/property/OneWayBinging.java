package tz.okronos.core.property;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

/**
 * Represents an one way binding as created by BindingHelper.bind().
 *
 * @param <S> the type of the source.
 * @param <D> the type of the destination.
 */
public class OneWayBinging <S, D>  {
	private InvalidationListener listener;
	private WritableValue<D> destination;
	private ObservableValue<S> source;

	public OneWayBinging(ObservableValue<S> source, WritableValue<D> destination, InvalidationListener listener) {
		super();
		this.source = source;
		this.destination = destination;
		this.listener = listener;
	}

	public InvalidationListener getListener() {
		return listener;
	}
	
	public WritableValue<D> getDestination() {
		return destination;
	}
	
	public ObservableValue<S> getSource() {
		return source;
	}
	
}