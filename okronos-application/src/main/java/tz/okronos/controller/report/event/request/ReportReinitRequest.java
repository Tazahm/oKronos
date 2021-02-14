package tz.okronos.controller.report.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.report.event.ReportEvent;


@Accessors(chain = true)
@Getter @Setter
public class ReportReinitRequest extends ReportEvent {
	String categoryId;
	Object content;
}
