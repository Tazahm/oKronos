package tz.okronos.controller.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.report.event.ReportEvent;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.notif.ReportFileNotif;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportImmediateBuildRequest;
import tz.okronos.controller.report.event.request.ReportLoadRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.controller.report.event.request.ReportSaveAsRequest;
import tz.okronos.controller.report.model.ReportMain;
import tz.okronos.controller.report.model.ReportRoot;
import tz.okronos.controller.shutdown.event.notif.ShutdownVetoNotif;
import tz.okronos.controller.shutdown.event.request.ShutdownRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.TimerEnabler;
import tz.okronos.event.request.ResetPlayRequest;


/**
 *  Periodically prints a report on file.
 *  The report contains the current play state : time, score, penalties ...
 *  <p>
 *  <ul>
 *   <li>The instance handles a timer (buildTimer) to print periodically the report.</li>
 *   <li>It subscribes to any messages and arms the timer (if no already done) each time it receives a new one.</li>
 *   <li>When the timer expires the instance sends a request (ReportBuildRequest) in order to receive the data to be saved from the data controllers.</li>
 *   <li>It waits some time for the answers thanks to a second timer (buildTimer).</li>
 *   <li>It saves any incoming answer into a map (reportRoot).</li>
 *   <li>When the second timer expires, the instance print the report from the answers.</li>
 * </ul> 
 * <p>
 * The data is a XML file as a list of data item. Each item is an answer indexed with its name (contains into the answer). 
 * <p>
 * NB: the instance is not synchronized (except the map of answers) and synchronization issues are not handled.
 */
@Slf4j
@Configuration
public class ReportActionController extends AbstractController {
	public static final String MainId = "main";
	public static final String EndLoadId = "loadEnd";
	
	private static SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-S");
	private static SimpleDateFormat friendlyDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private TimerEnabler requestTimer;
	private TimerEnabler buildTimer;
	private File reportFile;
	private File secondFile;
	private Date resetDate;
	private int lastRequestId;
	private ReportRoot reportRoot;
	private boolean shutdownInProgress;
	private ObjectMapper mapper;
	
	private ReadOnlyBooleanWrapper loadReportInProgressWrapper = new ReadOnlyBooleanWrapper();

    
    @PostConstruct 
    public void init()  {
    	context.registerEventListener(this);
		
		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(true);
		mapper = new XmlMapper(xmlModule);
	}

	@Subscribe public void onResetPlayRequest(final ResetPlayRequest event) {
		reset();
	}

	@Subscribe public  void onShutdownRequest(final ShutdownRequest event) {
		shutdownInProgress = true;
		context.postEvent(new ShutdownVetoNotif()
				.setRequester(this.getClass().getName()).setLock(true));
		
		removeTimers();
		requestContent();
	}

	@Subscribe public void onEvent(final Object event) {
		if (! (event instanceof ReportEvent)) {
			armRequestTimer();	
		}
	}

	@Subscribe public void onReportBuildAnswer(final ReportBuildAnswer answer) {
		reportRoot.add(answer);
	}

	@Subscribe public void onReportImmediateBuildRequest(final ReportImmediateBuildRequest request) {
		requestContent();
	}
	
	@Subscribe public  void onReportSaveAsRequest(final ReportSaveAsRequest event) {
		secondFile = KronoHelper.urlToNewFile(event.getUrl());
		if (secondFile == null || ! secondFile.getParentFile().canWrite()) {
			log.warn("Cannot write in " + event.getUrl());
			secondFile = null;
		}
		requestContent();
	}

	@Subscribe public void onReportLoadRequest(final ReportLoadRequest event) {
		try {
			loadReportInProgressWrapper.set(true);
			load(event.getUrl()); }
		catch (IOException | ClassNotFoundException ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		// TODO this is an unsecured way to synchronize the end of loading
		// for the clients - find better.
		if (EndLoadId.equals(request.getCategoryId())) {
			Platform.runLater(()->loadReportInProgressWrapper.set(false));
		}
  	}

    @Bean
    ReadOnlyBooleanProperty loadReportInProgressProperty() {
    	return loadReportInProgressWrapper.getReadOnlyProperty();
    }

	private  void armRequestTimer() {
		if (requestTimer == null) {
			log.debug("Arm the request timer");
			requestTimer = new TimerEnabler();
			int delay = context.getIntProperty("reportDelay", 10);
		    requestTimer.schedule(() -> requestContent(), delay * 1000);
		}
	}
	
	private  void removeTimers() {
		if (requestTimer != null) {
			log.debug("Remove the request timer");
			requestTimer.cancel();
			requestTimer = null;
		}
		
		if (buildTimer != null) {
			log.debug("Remove the build timer");
			buildTimer.cancel();
			buildTimer = null;
		}
	}
	
	private  void requestContent() {
		log.debug("Request content");
		if (reportFile == null) return;
		
		removeTimers();
		reportRoot = new ReportRoot();
		
		context.postEvent(new ReportBuildRequest()
				.setRequestId(++lastRequestId));
		
		buildTimer = new TimerEnabler();
		buildTimer.schedule(() -> buildContentNoException(), context.getIntProperty("reportBuildDelay", 1) * 1000);
	}
	
	private void buildContentNoException() {
		try { buildContent(); }
		catch (IOException ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	private  void buildContent() throws IOException {
		log.debug("Build content");
		if (reportFile == null) return;
		if (reportRoot == null) return;
		
		// Adds main data
		ReportMain main = new ReportMain();
		Date gendate = new Date();
		main.setStartDate(resetDate);
		main.setFriendlyStartDate(friendlyDateFormat.format(resetDate));
		main.setGenerationDate(gendate);
		main.setFriendlyGenerationDate(friendlyDateFormat.format(gendate));
		
		ReportBuildAnswer answer = new ReportBuildAnswer();
		answer.setCategoryId(MainId);
		answer.setContent(main);
		reportRoot.add(answer);	
		
		// Writes the new map.
		reportRoot.buildElements();
		mapper.writerWithDefaultPrettyPrinter().writeValue(reportFile, reportRoot);
		
		reportRoot = null;
		buildTimer.cancel();
		buildTimer = null;
		
		// Notifies.
		context.postEvent(new ReportFileNotif()
				.setUrl(KronoHelper.toExternalForm(reportFile))
				.setRequestId(lastRequestId));

		// Handles shutdown if needed.
		if (shutdownInProgress) {
			context.postEvent(new ShutdownVetoNotif()
				.setRequester(this.getClass().getName()).setLock(false));
		}

		// Duplicates the destination file is required by the CopyAs request.
		if (secondFile != null) {
			Files.copy(reportFile.toPath(), secondFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			log.info("Save report into " + secondFile.getAbsolutePath());
			secondFile = null;
		}
	}
	
	private  void reset() {
		log.debug("Reset");
		removeTimers();
		
		reportFile = null;
		resetDate = new Date();
		String histoPath = context.getProperty("histoPath", "gen/histo");
    	File histoDir = context.getFile(histoPath);
    	if (! histoDir.exists()) {
    		try {
				Files.createDirectories(histoDir.toPath());
			} catch (IOException ex) {
				log.error(ex.getMessage(), ex);
				return;
			}
    	}
    	String fileName = "report-" + fileNameDateFormat.format(new Date()) + ".xml";
		reportFile = new File(histoDir, fileName);
		log.info("Output : {}", reportFile.getAbsoluteFile());		
	}
	
	private  void load(final String externalForm) throws IOException, ClassNotFoundException {
		File file = KronoHelper.urlToFile(externalForm);
        int requestId = ++lastRequestId;

	    // Read the file
		ReportRoot newRoot =  mapper.readValue(file, ReportRoot.class);
		
        for (final ReportBuildAnswer element : newRoot.getElements()) {
        	context.postEvent(new ReportReinitRequest()
        		.setCategoryId(element.getCategoryId())
        		.setContent(element.getContent())
        		.setRequestId(requestId));
        }
        
        // Closes the load
        context.postEvent(new ReportReinitRequest()
        		.setCategoryId(EndLoadId)
        		.setRequestId(requestId));
	}
}
