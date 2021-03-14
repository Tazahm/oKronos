package tz.okronos.controller.record.model;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class EventRecordSnapshot {
	@JacksonXmlElementWrapper(localName = "events")
	@JacksonXmlProperty(localName = "event")
	List<EventRecord<?>> records;
}