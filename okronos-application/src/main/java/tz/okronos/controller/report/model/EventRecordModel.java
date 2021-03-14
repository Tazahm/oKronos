package tz.okronos.controller.report.model;

import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyListWrapper;
import lombok.Getter;
import tz.okronos.controller.record.model.EventRecord;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoHelper;

@Component
public class EventRecordModel extends AbstractController  {
	@Getter private ReadOnlyListWrapper<EventRecord<?>> historyListWrapper;	
	
	
	public EventRecordModel() {
		historyListWrapper = KronoHelper.createListWrapper();
	}
}
