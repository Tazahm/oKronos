package tz.okronos.controller.pdfexport.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter @Getter
public class ExportPdfRequest {
	private String url;
}
