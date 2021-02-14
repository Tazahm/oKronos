package tz.okronos.controller.report.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReportMain {
	private Date startDate;
	private String friendlyStartDate;
	private Date generationDate;
	private String friendlyGenerationDate;
 }