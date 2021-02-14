package tz.okronos.controller.xlsexport.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter @Getter
public class ExportXlsRequest {
	private String url;
}
