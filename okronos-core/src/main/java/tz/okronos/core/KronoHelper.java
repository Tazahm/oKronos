package tz.okronos.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.stage.Stage;

/**
 * Miscellaneous utility static methods.
 */
public class KronoHelper {
	private static KronoHelper instance = new KronoHelper();
	
	private KronoHelper() {		
	}
	
	/**
	 * Formats an integer as a time. The integer T shall contains the minutes and seconds as following :
	 * T = mm * 60 + ss. The time can be negative, in that case T = -(mm * 60 + ss). 
	 * The output will be "mm:ss" or,if negative, "-mm:ss".
	 * 
	 * @param time the time.
	 * @return the human readable matching string.
	 */
	public static String secondsToTime(int time) {
		int absTime = time >= 0 ? time : - time;
		String sign = time >= 0 ? "" : "-";
		return String.format("%s%02d:%02d", sign, absTime / 60, absTime % 60);
    }
	
	/**
	 * Displays a confirmation window.
	 * 
	 * @param title the title of the window.
	 * @param message the message.
	 * @param stage the window owner.
	 * @return true if the user confirms, false otherwise.
	 */
	public static boolean requestUser(final String title, final String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.getButtonTypes().add(ButtonType.YES);
        
        alert.setTitle(title);
        alert.setContentText(message);
        alert.initOwner(stage.getOwner());
        Optional<ButtonType> res = alert.showAndWait();
       
        return ! res.isPresent() || ! res.get().equals(ButtonType.CANCEL);
    }
	
	/**
	 * Displays a confirmation window, searches the messages into the context bundle.
	 * @param context the context of the application.
	 * @param titleKey the key for the title
	 * @param messageKey the key for the message.
	 * @param stage the window owner.
	 * @return true if the user confirms, false otherwise.
	 */
	public static boolean requestUser(final KronoContext context, final String titleKey, final String messageKey, Stage stage) {
        return requestUser(context.getItString(titleKey),
            context.getItString(messageKey), stage);
    }
	
	/**
	 * Adds or removes a style into the list of style of a control.
	 * When adding, if a style already exists, do not add it another time.
	 * But, when removing, removes the style as many time as necessary.
	 * 
	 * @param control the control.
	 * @param add true to add, false to remove.
	 * @param style the style.
	 */
	public static void setStyle(Control control, boolean add, String style) {
    	List<String> styles = control.getStyleClass();
    	if (add) {
    		if (! styles.contains(style))
    			styles.add(style);
    	}
    	else {
    		while (styles.remove(style)) { 
    			// noop
    		}
    	}
	}
	
	/**
	 * Creates correctly a SimpleListProperty with a list with an empty content.
	 * 
	 * @param <T> the type of the elements of the list.
	 * @return the new property.
	 */
	public static <T> SimpleListProperty<T> createListProperty() { 
	    return new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>())); 
	}
	
	/**
	 * Creates correctly a SimpleListProperty with a list with an empty content.
	 * 
	 * @param <T> the type of the elements of the list.
	 * @return the new property.
	 */
	public static <T> ReadOnlyListWrapper<T> createListWrapper() { 
	    return new ReadOnlyListWrapper<>(FXCollections.observableArrayList(new ArrayList<>())); 
	}

	/**
	 * Creates an string property initialized with the empty string.
	 * @return the property.
	 */
	public static SimpleStringProperty createSimpleStringProperty() {
		return new SimpleStringProperty("");
	}
	
	public static <S, D> SimpleLateralizedPair<List<D>> translate(SimpleLateralizedPair<ReadOnlyListProperty<S>> marksProperties,
		Function<S, D> mapper, Comparator<D> comparator) {
		List<D> dstLeft = translate(marksProperties.getLeft().get(), mapper, comparator);		
		List<D> dstRight = translate(marksProperties.getRight().get(), mapper, comparator);
		return new SimpleLateralizedPair<>(dstLeft, dstRight);
	}

	public static <S, D> List<D> translate(ObservableList<S> list, Function<S, D> mapper, Comparator<D> comparator) {
		List<D> dst = list.stream().map(mapper).collect(Collectors.toList());
		if (comparator != null) {
			dst.sort(comparator);
		}
		return dst;
	}

	@SuppressWarnings("unchecked")
	public static <S, D> D[] toArray(ObservableList<S> list, Function<S, D> mapper, 
		Comparator<D> comparator, Class<D> dstClass) {
		 List<D> listDst = translate(list, mapper, comparator);
		 D[] arrayDst = (D[]) java.lang.reflect.Array.newInstance(dstClass, listDst.size());
		 for (int i = 0; i < listDst.size(); i++) {
			 arrayDst[i] = listDst.get(i);
		 }
		 return arrayDst;
	}

	public static <S, D> List<D> fromArray(S[] src, Function<S, D> mapper) {
		if (src == null) {
			return new ArrayList<D>();
		}
		return Stream.of(src).map(mapper).collect(Collectors.toList());
	}
	
	public static String toExternalForm(File file) {
		try {
			return file.toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			Logger logger = LoggerFactory.getLogger(instance.getClass());
			logger.warn(e.getMessage());
		}
		return null;
	}
	
	public static File urlToFile(String externalForm) {
		try {
			URL url = new URL(externalForm);
			String fileName = url.getFile();
			File file = new File(fileName);
			if  (file.canRead()) {
				return file;
			}
		} catch (MalformedURLException e) {
			Logger logger = LoggerFactory.getLogger(instance.getClass());
			logger.warn(e.getMessage());
		}
		return null;
	}
	
	public static File urlToNewFile(String externalForm) {
		try {
			return safeUrlToNewFile(externalForm);
		} catch (MalformedURLException e) {
			Logger logger = LoggerFactory.getLogger(instance.getClass());
			logger.warn(e.getMessage());
		}
		return null;
	}
	
	public static File safeUrlToNewFile(String externalForm) throws MalformedURLException {
		URL url = new URL(externalForm);
		String fileName = url.getFile();
		File file = new File(fileName);
		return file;
	}
	
	/**
	 * Set/unset the control visible and managed by its container.
	 * 
	 * @param control the control.
	 * @param manage true to manage, false otherwise.
	 */
	public static void setManaged(Node control, boolean manage) {
		control.setVisible(manage);
		control.setManaged(manage);
		
		// Hide the parents if their have no children managed. 
//		Parent parent = control.getParent();
//		while (parent != null) {
//			boolean parentManeged = manage && ! getManagedChildren(parent).isEmpty();
//			parent.setVisible(parentManeged);
//			parent.setManaged(parentManeged);
//			
//			parent = parent.getParent();			
//		}
	}

    /**
     * Gets the list of all managed children of this {@code Parent}.
     *
     * @param <E> the type of the children nodes
     * @return list of all managed children in this parent
     */
	public static  List<Node> getManagedChildren(Parent parent) {
        
		 List<Node> managedChildren = new ArrayList<Node>();
		 List<Node> children = parent.getChildrenUnmodifiable();
            for (int i=0, max=children.size(); i<max; i++) {
                Node e = children.get(i);
                if (e.isManaged()) {
                    managedChildren.add(e);
                }
            }
        
        return managedChildren;
    }

}

