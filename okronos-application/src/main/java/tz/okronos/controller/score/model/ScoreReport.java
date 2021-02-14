package tz.okronos.controller.score.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ScoreReport {
	@JacksonXmlElementWrapper(localName = "marks")
	@JacksonXmlProperty(localName = "mark")
	ScoreSnapshot[] marks;
}