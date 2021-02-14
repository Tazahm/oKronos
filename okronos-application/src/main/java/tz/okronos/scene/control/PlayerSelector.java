package tz.okronos.scene.control;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import lombok.Setter;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.core.KronoContext;

/**
 *  Allows to select a player. Contains one button per player. The player is depicted by its shirt number.
 *  The buttons are set into a grid of {@code columnCount} columns and are grouped into a toggle group
 *  so that only one player can be selected at a time.
 */
public class PlayerSelector extends GridPane {
	@Setter KronoContext context;
	
	private int columnCount;
    private ToggleGroup toggleGroup ;
    private Integer initialSelectedShirt;
    private boolean empty = true;
    private boolean showNoSection = false;
    private ToggleButton noSelectionButton;
    
	public PlayerSelector(final int columnCount) {
		this.columnCount = columnCount;
		toggleGroup = new ToggleGroup();
		
		setVgap(4);
		setHgap(4);
	}

	public PlayerSelector() {
		this(4);
	}

	public void setPlayers(List<PlayerSnapshot> players) {
		removeLabels();
		buildLabels(players);
		 
		if (empty) {
			buildEmptyMessage();
		}
	}
	
	public void selectPlayer(Integer shirt) {
		initialSelectedShirt = shirt;
		selectPlayer();
	}
	
	public void setAllowNoSelection(boolean allow) {
		// showNoSection = allow;
	}
	
	private void selectPlayer() {
		if (initialSelectedShirt == null) {
			if (showNoSection) {
				toggleGroup.selectToggle(noSelectionButton);
			} else {
			    toggleGroup.selectToggle(null);
			}
			return;
		}
		if (empty) return;

		ToggleButton button = (ToggleButton) getChildren().stream()
			.filter(b -> b != noSelectionButton)
			.filter(b-> ((PlayerSnapshot)b.getUserData()).getShirt() == initialSelectedShirt)
			.findFirst()
			.orElse(null);
		toggleGroup.selectToggle(button);
	}
	
	public PlayerSnapshot getSelectedPlayer() {
		Toggle toggle = toggleGroup.getSelectedToggle();
		if (toggle == null) return null;
		return (PlayerSnapshot) toggle.getUserData();
	}
	
	private void buildEmptyMessage() {
		Label label = new Label(context.getItString("playerSelector.emptyList"));
		add(label, 0, 0);
	}
	
	private void buildLabels(List<PlayerSnapshot> players) {		
		int i = 0;
		for (PlayerSnapshot player : players) {
			if (! player.isOfficial()) {
			    add(createPlayerButton(player), i % columnCount, i / columnCount);
			    empty = false;
			    i++;
			}
		}
		if (showNoSection && ! empty) {
			add(createNoSelectionButton(), i % columnCount, i / columnCount);
		}
	}

	private void removeLabels() {
		getChildren().clear();
		toggleGroup.getToggles().clear();
		empty = true;
	}

	private ToggleButton createPlayerButton(PlayerSnapshot player) {
		ToggleButton button = createButton();
		button.setText(String.format("%02d", player.getShirt()));
		button.setUserData(player);
		
		return button;
	}
	
	private ToggleButton createNoSelectionButton() {
		noSelectionButton = createButton();
		noSelectionButton.setText("X");
		
		return noSelectionButton;
	}
	
	private ToggleButton createButton() {
		ToggleButton button = new ToggleButton();
		button.getStyleClass().add("toggle");
		button.setMaxWidth(1000);
		toggleGroup.getToggles().add(button);
		
		return button;
	}
	
}


