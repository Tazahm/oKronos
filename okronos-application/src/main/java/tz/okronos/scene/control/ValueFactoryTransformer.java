package tz.okronos.scene.control;

import java.lang.reflect.InvocationTargetException;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.core.property.BindingHelper;
import tz.okronos.core.property.OneWayConverter;

/** 
 *  Displays the value of a property of a given model object
 *  into the cell of a table view. This property can be of any type
 *  and is converted to a string with the help of a OneWayConverter instance.
 *  
 *  This class can be used into a fxml file.
 * 
 *  @param <S> The type of the model object.
 *  @param <T> The type of the property.
 *  
 *  @see javafx.scene.control.cell.PropertyValueFactory
 *  @see OneWayConverter
  */
@Slf4j
public class ValueFactoryTransformer<S, T>
        implements Callback<CellDataFeatures<S, String>, ObservableValue<String>>  {
	
	private PropertyValueFactory<S,T> delegate;
    private OneWayConverter<T, String> converter;

	public ValueFactoryTransformer(@NamedArg("property") String property,
			@NamedArg("converter") String converter) {
		delegate = new PropertyValueFactory<>(property);
        this.converter = findConverter(converter);
    }
	
 	@Override 
    public ObservableValue<String> call(CellDataFeatures<S, String> param) {
 		TableColumn<S, T> fakeColumn = new TableColumn<>();
 		CellDataFeatures<S, T> paramDelegate = new CellDataFeatures<>(
 		    param.getTableView(), fakeColumn, param.getValue());
    	ObservableValue<T> value = delegate.call(paramDelegate);
    	SimpleStringProperty converted = new SimpleStringProperty();
    	BindingHelper.bindWeakly(converted, value, converter);
    	
    	return converted;
    }
 	
   
   @SuppressWarnings("unchecked")
   protected OneWayConverter<T, String> findConverter(String name) {
    	try {
    		converter = (OneWayConverter<T, String>) Class.forName(name).getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException | ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new IllegalArgumentException(name, ex); 
		}
    	return converter;
    }
}
