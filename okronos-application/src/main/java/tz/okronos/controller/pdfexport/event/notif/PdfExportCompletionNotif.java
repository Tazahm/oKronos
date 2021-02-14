package tz.okronos.controller.pdfexport.event.notif;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Setter @Getter
public class PdfExportCompletionNotif {
	private String url;
}
