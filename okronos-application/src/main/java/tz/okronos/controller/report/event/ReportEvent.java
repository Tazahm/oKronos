package tz.okronos.controller.report.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter @Setter
public class ReportEvent {
	private int requestId;
}
