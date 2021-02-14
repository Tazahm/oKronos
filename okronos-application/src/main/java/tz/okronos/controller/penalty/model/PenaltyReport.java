package tz.okronos.controller.penalty.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

/**
 *  List of penalties used to build the report.
 */
@Setter @Getter 
public class PenaltyReport {
	@JacksonXmlElementWrapper(localName = "penalties")
	@JacksonXmlProperty(localName = "penalty")
	private PenaltySnapshot[] penalties;
}