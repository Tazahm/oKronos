package tz.okronos.controller.report.event.notif;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.report.event.ReportEvent;


@Accessors(chain = true)
@Getter @Setter
public class ReportBuildAnswer extends ReportEvent {
	String categoryId;
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
	Object content;
}
