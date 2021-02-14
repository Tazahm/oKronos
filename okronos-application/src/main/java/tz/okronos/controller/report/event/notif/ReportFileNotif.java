package tz.okronos.controller.report.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.report.event.ReportEvent;

@Accessors(chain = true)
@Getter @Setter
public class ReportFileNotif extends ReportEvent {
	private String url;
}
