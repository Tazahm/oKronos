package tz.okronos.controller.pdfexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.eventbus.Subscribe;

import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.match.model.MatchDataSnapshot;
import tz.okronos.controller.match.model.MatchDataSnapshot.GoalkeeperSwapSnapshot;
import tz.okronos.controller.match.model.MatchDataSnapshot.OfficialSnapshot;
import tz.okronos.controller.match.model.MatchDataSnapshot.TeamDataSnapshot;
import tz.okronos.controller.pdfexport.event.notif.PdfExportCompletionNotif;
import tz.okronos.controller.pdfexport.event.request.ExportPdfRequest;
import tz.okronos.controller.penalty.model.PenaltyReport;
import tz.okronos.controller.penalty.model.PenaltySnapshot;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.notif.ReportFileNotif;
import tz.okronos.controller.report.event.request.ReportImmediateBuildRequest;
import tz.okronos.controller.report.model.ReportRoot;
import tz.okronos.controller.score.model.ScoreReport;
import tz.okronos.controller.score.model.ScoreSnapshot;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.controller.team.model.TeamReport;
import tz.okronos.core.KronoContext;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SimpleLateralizedPair;

/**
 *  Produces a PDF report from a test file (in XML format). 
 *  The input is located into the test input: src/test/resources/report-1.xml
 *  The output is located into the target directory: file target/live/test/report-1.pdf.
 */
@Slf4j
public class PdfExportControllerTest{
	
	private static String INPUT_DIR = "target/live/test";
	private static String OUTPUT_DIR = "target/live/test";
	private static String INPUT_EXP = "report-%02d.xml";
	private static String OUTPUT_EXP = "report-%02d.pdf";
	
	private KronoContext context;	
	private File inputFile;
	private File outputFile;
	private int currentReportCount = 0;
	private int maxReportCount = 42;
	
	public static void main(String[] args) throws IOException {
		new PdfExportControllerTest().run(args);
    }

	// Mocks the report builder.
	@Subscribe public void onReportImmediateBuildRequest(final ReportImmediateBuildRequest request) {
		context.postEvent(new ReportFileNotif().setUrl(KronoHelper.toExternalForm(inputFile)));
	}

	// Exits on build completion.
	@Subscribe public void onPdfExportCompletionNotif(final PdfExportCompletionNotif notif) throws IOException {
		log.info("Result for report {} in : {} ", currentReportCount, notif.getUrl());
		
		if (currentReportCount >= maxReportCount) {
			log.info("Test end");
			System.exit(0);
		} else {
			currentReportCount ++;
			generatePdf();
		}
	}

	// Runs the test.
	// Initializes the environment and next requests the PDF production (ExportPdfRequest).
	// Next, the PDF controller asks for report production : the answer is mock by this class (see onReportImmediateBuildRequest())
	// and produces a ReportFileNotif.
	// On ReportFileNotif reception, the PDF controller builds the PDF file and next sends a PdfExportCompletionNotif.
	// On PdfExportCompletionNotif reception, the test exists (see onPdfExportCompletionNotif()).
	public void run(String[] args) throws IOException {
		File baseDirectory = new File(System.getProperty("user.dir"));
		log.debug("baseDirectory : " + baseDirectory.getPath());
		System.setProperty("configfile", baseDirectory + "/src/test/dataset/init.properties");
		
		// Builds the minimal instances useful to run the PDF export instance : context and PDF export itself.
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
    	annotationContext.scan("tz.okronos.core", "tz.okronos.controller.pdfexport");
    	annotationContext.refresh();
    	
    	// Initializes the context.
    	context = annotationContext.getBean(KronoContext.class);    	     	
		context.getEventBus().register(this);

		// Request the first PDF generation.
		new File(INPUT_DIR).mkdir();
		new File(OUTPUT_DIR).mkdir();

		currentReportCount = 0;
		generatePdf();
	}

	private void generatePdf() throws IOException {
		inputFile = new File(INPUT_DIR, String.format(INPUT_EXP, currentReportCount));
		outputFile = new File(OUTPUT_DIR, String.format(OUTPUT_EXP, currentReportCount));		
		
		log.debug("Generation {}", currentReportCount);
		log.debug("Input file : " + inputFile.getAbsolutePath());
		log.debug("output file : " + outputFile.getAbsolutePath());
		
		buildReport(currentReportCount);
		requestPdfGeneration();		
	}
	
	private void requestPdfGeneration() {
		// Requests the production of the PDF file.
		context.postEvent(new ExportPdfRequest().setUrl(KronoHelper.toExternalForm(outputFile)));
	}
	
	private void buildReport(int elementCount) throws IOException {
		ReportRoot root = new ReportRoot();
		
		
		root.add(new ReportBuildAnswer().setCategoryId("score-left")
				.setContent(buildScoreReport(elementCount, PlayPosition.LEFT)));
		root.add(new ReportBuildAnswer().setCategoryId("score-right")
				.setContent(buildScoreReport(maxReportCount - elementCount, PlayPosition.RIGHT)));
		root.add(new ReportBuildAnswer().setCategoryId("penalty-left")
				.setContent(buildPenaltyReport(elementCount, PlayPosition.LEFT)));
		root.add(new ReportBuildAnswer().setCategoryId("penalty-right")
				.setContent(buildPenaltyReport(maxReportCount - elementCount, PlayPosition.RIGHT)));
		root.add(new ReportBuildAnswer().setCategoryId("team-left")
				.setContent(buildTeamReport(elementCount, PlayPosition.LEFT)));
		root.add(new ReportBuildAnswer().setCategoryId("team-right")
				.setContent(buildTeamReport(maxReportCount - elementCount, PlayPosition.RIGHT)));
		root.add(new ReportBuildAnswer().setCategoryId("match")
				.setContent(buildMatchReport(elementCount)));
		
		root.buildElements();

		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(true);
		ObjectMapper mapper = new XmlMapper(xmlModule);
		mapper.writerWithDefaultPrettyPrinter().writeValue(inputFile, root);
	}
		
	private ScoreReport buildScoreReport(int elementCount, PlayPosition team) {
		
		List<ScoreSnapshot> marks = new ArrayList<>();
		for (int i = 1; i < elementCount+1; i++) {
			ScoreSnapshot mark = new ScoreSnapshot();
			mark.setScorer(i);
			mark.setAssist1(i % 6 == 0 ? -1 : i + 1);
			mark.setAssist2(i % 3 == 0 ? -1 : i + 2);
			mark.setTeam(team);
			mark.setTime(i * 10);
			mark.setPeriod(i < i / 3 ? 1 : 2);
			
			marks.add(mark);
		}

		ScoreReport content = new ScoreReport();
		content.setMarks(marks.toArray(new ScoreSnapshot[0]));
		return content;
	}

	private PenaltyReport buildPenaltyReport(int elementCount, PlayPosition team) {
		
		List<PenaltySnapshot> penalties = new ArrayList<>();
		for (int i = 1; i < elementCount + 1; i++) {
			PenaltySnapshot penalty = new PenaltySnapshot();
			penalty.setCode(i % 3 == 0 ? "AAA" : "BBB");
			penalty.setDuration(i % 5 == 0 ? 5 : 2);
			penalty.setOnStoppage(false);
			penalty.setPenaltyTime(i * 10);
			penalty.setPending(false);
			penalty.setPeriod(i < i / 3 ? 1 : 2);
			penalty.setPlayer(i);
			penalty.setRemainder(0);
			penalty.setStartTime(i * 10);
			penalty.setStopTime(i * 10 + 2 * 60);
			penalty.setTeam(team);
			
			penalties.add(penalty);
		}
		PenaltyReport content = new PenaltyReport();
		content.setPenalties(penalties.toArray(new PenaltySnapshot[0]));
		return content;
	}
	
	private TeamReport buildTeamReport(int elementCount, PlayPosition team) {
		
		List<PlayerSnapshot> players = new ArrayList<>();
		for (int i = 0; i < elementCount; i++) {
			int n = (elementCount-i)*2 + 8;
			PlayerSnapshot player = new PlayerSnapshot();
			player.setGoalkeeper(i == 0 || i == 1  && elementCount > maxReportCount / 3);
			player.setLicence(String.format("P%08d", n));
			player.setName(String.format("N%02d P%08d", n, n));
			player.setOfficial(false);
			player.setShirt(n);
			player.setTeam(team);
			players.add(player);
		}
		
		for (int i = 1; i < elementCount % 8+1; i++) {
			PlayerSnapshot player = new PlayerSnapshot();
			player.setGoalkeeper(false);
			player.setLicence(String.format("Q%08d", i));
			player.setName(String.format("Q%02d R%08d", i, i));
			player.setOfficial(true);
			player.setShirt(i);
			player.setTeam(team);
			players.add(player);
		}
		
		TeamReport  content = new TeamReport();
		content.setPlayerList(players.toArray(new PlayerSnapshot[0]));
		content.setTeamName(team.toString());
		return content;
	}
	
	
	private MatchDataSnapshot buildMatchReport(int elementCount) {
		OfficialSnapshot chrono = OfficialSnapshot.builder().licence("Chrono").name("Mr Chrono").build();
		OfficialSnapshot marker = OfficialSnapshot.builder().licence("Marker").name("Mr Mark Marker").build();		
		OfficialSnapshot referee1 = OfficialSnapshot.builder().licence("referee1").name("Mr Ref One").build();
		OfficialSnapshot referee2 = OfficialSnapshot.builder().licence("referee2").name("Mr Ref Two").build();

		GoalkeeperSwapSnapshot[] swapA = new  GoalkeeperSwapSnapshot[3];
		swapA[0] = new GoalkeeperSwapSnapshot();
		swapA[0].setSheet(1);
		swapA[0].setTime(elementCount * 10);
		swapA[1] = new GoalkeeperSwapSnapshot();
		swapA[1].setSheet(2);
		swapA[1].setTime(elementCount * 10 + 40);
		swapA[2] = new GoalkeeperSwapSnapshot();
		swapA[2].setSheet(3);
		swapA[2].setTime(elementCount * 10 + 40);
		
		TeamDataSnapshot teamA = new TeamDataSnapshot();
		teamA.setGoalkeeperSwaps(swapA);
		teamA.setTimeoutPeriod1(elementCount);
		teamA.setTimeoutPeriod2(elementCount + 45);
		
		GoalkeeperSwapSnapshot[] swapB = new  GoalkeeperSwapSnapshot[3];
		swapB[0] = new GoalkeeperSwapSnapshot();
		swapB[0].setSheet(1);
		swapB[0].setTime(elementCount * 20);
		swapB[1] = new GoalkeeperSwapSnapshot();
		swapB[1].setSheet(0);
		swapB[1].setTime(0);
		swapB[2] = new GoalkeeperSwapSnapshot();
		swapB[2].setSheet(0);
		swapB[2].setTime(0);
		
		TeamDataSnapshot teamB = new TeamDataSnapshot();
		teamB.setGoalkeeperSwaps(swapB);
		teamB.setTimeoutPeriod1(elementCount);
		teamB.setTimeoutPeriod2(0);

		MatchDataSnapshot content = new MatchDataSnapshot();
		content.setBeginTime(elementCount + 20 * 60);
		content.setChrono(chrono);
		content.setClaim(false);
		content.setCompetition("N3");
		content.setDate("22/01/2020");
		content.setEndTime(elementCount + 20 * 60 + 75);
		content.setExtension(false);
		content.setGroup("G3");
		content.setIncidentReport(false);
		content.setLocation("A la maison");
		content.setMarker(marker);
		content.setMatchNumber(elementCount);
		content.setReferee1(referee1);		
		content.setReferee2(referee2);
		content.setReservesBeforeMatch(false);
		content.setTeam(new SimpleLateralizedPair<TeamDataSnapshot>(teamA, teamB));
		
		return content;
	}

}
