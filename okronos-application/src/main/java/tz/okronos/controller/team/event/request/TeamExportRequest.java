package tz.okronos.controller.team.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter @Getter
public class TeamExportRequest extends TeamImportExportRequest {
	private String url;
}
