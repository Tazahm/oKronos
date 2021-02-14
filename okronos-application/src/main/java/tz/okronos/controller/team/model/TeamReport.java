package tz.okronos.controller.team.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamReport {
	private String teamName;
	private String imageUrl;
	@JacksonXmlElementWrapper(localName = "players")
	@JacksonXmlProperty(localName = "player")
	@JsonInclude(Include.ALWAYS)
	private PlayerSnapshot[] playerList = new PlayerSnapshot[0];
}