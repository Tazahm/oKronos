package tz.okronos.scene.control;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.control.TableCell;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.core.KronoContext;

/**
  *  A table cell to contain an element of a penalty. This cell highlight its contents
  *  where the remaining time pass below its threshold, i.e. add the style "alert" to itself.
  *  The threshold is given by the property "penaltyThreshold".
  *  Pending penalties are not highlighted. On stoppage penalties are highlighted only if
  *  the time is frozen and the remaining time is zero.
 */
public class HighlightableTableCell extends TableCell<PenaltyVolatile, String> {
	
    private ReadOnlyBooleanProperty playTimeRunningProperty;
	
	/** From this time, the cell is highlighted. */
	private int thresholdBefore;
    
	
	public HighlightableTableCell(final KronoContext context,
			final ReadOnlyBooleanProperty playTimeRunningProperty) {
		thresholdBefore = context.getIntProperty("penaltyThreshold", 10);
		this.playTimeRunningProperty = playTimeRunningProperty;
	}
	
	@Override protected void updateItem(final String item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
        	setText(null);
            setGraphic(null);
        } else {
            PenaltyVolatile penaltyVolatile = getTableView().getItems().get(getIndex());
            setText(item.toString());
            // Add alert style 
            //   - not on stoppage case : if the penalty remaining time reaches its threshold,
            //   - on stoppage case : if the penalty remaining time is zero and the time is frozen.
            getStyleClass().removeAll("alert"); // "cell-renderer-original"
            if (    (penaltyVolatile.getRemainder() <= thresholdBefore 
            		    && ! penaltyVolatile.isOnStoppage() 
            		    && ! penaltyVolatile.isPending())
                 || (penaltyVolatile.getRemainder() == 0 ) 
                       && penaltyVolatile.isOnStoppage()
                       && ! penaltyVolatile.isPending() 
                       && ! playTimeRunningProperty.get()) {
                getStyleClass().add("alert");
           }
        }
    }
}