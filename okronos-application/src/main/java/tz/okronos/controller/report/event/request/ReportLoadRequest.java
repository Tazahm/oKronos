package tz.okronos.controller.report.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter @Setter
public class ReportLoadRequest {
	private String url;
}
