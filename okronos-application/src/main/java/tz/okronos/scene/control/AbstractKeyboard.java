package tz.okronos.scene.control;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *  A virtual keyboard that allows to enter some digits.
 *  Linked with a formatted text field that receive the inputs.
 * 
 * In addition, the keyboard contains a key for reset or clear
 * and another to negate the input.
 *
 * @param <T> the type of the object hold by the formatted text field.
 */
public class AbstractKeyboard<T> extends GridPane {
	private Node signLabel;
	private Node eraseLabel;
	private Node emptySignLabel;
	private Node emptyEraseLabel;
    protected AbstractField<T, ?> targetField;
    private boolean shallReset;
    
    
	public AbstractKeyboard() {
		this(4);
	}
	
	public AbstractKeyboard(final int columnCount) {
	    setPadding(new Insets(5, 5, 5, 5));
		setVgap(4);
		setHgap(4);
		getStyleClass().add("number-keyboard");
		
		EventHandler<MouseEvent> numericHandler = e -> onNumberAction(e);
		for (int i = 0; i < 9; i++) {
			add(createLabel(Integer.toString(i + 1), numericHandler), i % columnCount, i / columnCount);
		}
		add(createLabel("0", numericHandler), 9 % columnCount, 9 / columnCount);
		
		emptyEraseLabel = createLabel(null, null);
		eraseLabel = createLabel("E", e -> onResetAction());
		add(eraseLabel, 10 % columnCount, 10 / columnCount);
		add(emptyEraseLabel, 10 % columnCount, 10 / columnCount);
		setShowErase(true);
		
		emptySignLabel = createLabel(null, null);
		signLabel = createLabel("+", e -> onSignAction());
		add(signLabel, 11 % columnCount, 11 / columnCount);
		add(emptySignLabel, 11 % columnCount, 11 / columnCount);
	    setShowSign(true);
	}

	public void setShowSign(boolean show) {
		signLabel.setVisible(show);
		emptySignLabel.setVisible(! show);
	}
	
	public void setShowErase(boolean show) {
		eraseLabel.setVisible(show);
		emptyEraseLabel.setVisible(! show);
	}
	
	public void bindWithField(final AbstractField<T, ?> textField) {
		this.targetField = textField;
	}

	public void unbindWithField(final TextField textField) {
    	targetField = null;
	}

	public boolean isBound() {
		return targetField != null;
	}
	
	private Node createLabel(String txt, EventHandler<MouseEvent> handler) {
		VBox box = new VBox();
		box.getStyleClass().add("number-key-bg");

		if (txt != null) {
			Label label = new Label(txt);
			if (handler != null) {
			  label.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
			}
			label.getStyleClass().add("number-key");
			box.getChildren().add(label);
		}
		
		return box;
	}
	
	private void onSignAction() {
		if (! isBound()) return;
		targetField.negateValue();
	}

	private void onResetAction() {
		if (! isBound()) return;

		targetField.requestFocus();
		if (shallReset) {
			targetField.resetValue();
		} else {
			targetField.clear();
		}
		shallReset = ! shallReset;
		((Label) ((VBox) eraseLabel).getChildren().get(0)).setText(shallReset ? "R" : "E");
	}
	
	private void onNumberAction(MouseEvent event) {
		if (! isBound()) return;
		
		Label label = (Label) event.getSource();
		int intValue = Integer.parseInt(label.getText().trim()); 
		fireKeyEvent(intValue);
	}

	private void fireKeyEvent(int intValue) {
		char charValue = (char)(intValue + '0');    
		final String stringValue = Character.toString(charValue);
		KeyEvent event = new KeyEvent(this,
                null,
                KeyEvent.KEY_TYPED,
                stringValue,
                stringValue,
                KeyCode.getKeyCode(stringValue),
                false, false, false, false);
		targetField.requestFocus();
		Event.fireEvent(targetField, event);
	}

}
