package tz.okronos.controller.report.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;

@Accessors(chain = true)
@Getter @Setter
@JacksonXmlRootElement(localName = "okronos")
public class ReportRoot {
	@JsonIgnore
	private Map<String, ReportBuildAnswer> mappedElements = Collections.synchronizedMap(new HashMap<>());
	@JacksonXmlElementWrapper(localName = "snapshots")
	@JacksonXmlProperty(localName = "snapshot")
	private List<ReportBuildAnswer> elements;
	
	public void add(ReportBuildAnswer answer) {
		mappedElements.put(answer.getCategoryId(), answer);
	}

	public void buildElements() {
		elements = new ArrayList<>(mappedElements.values());
	}
}
