package tz.okronos.controller.pdfexport;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Date;

import javax.annotation.PostConstruct;

// WARNING to prevent compilation errors with eclipse, the javax.xml package has been removed
// from xml-apis-1.0.b2.jar and xml-apis-1.3.04.jar

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;


import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.Subscribe;

import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.pdfexport.event.notif.PdfExportCompletionNotif;
import tz.okronos.controller.pdfexport.event.request.ExportPdfRequest;
import tz.okronos.controller.report.event.notif.ReportFileNotif;
import tz.okronos.controller.report.event.request.ReportImmediateBuildRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.KronoContext.ResourceType;


@Slf4j
@Configuration
public class PdfExportController extends AbstractController {
    
    // configure fopFactory as desired
    //private FopFactory fopFactory;
    private String sourceUrl;
    private String destUrl;
    
	@PostConstruct 
    public void init()  {
		// fopFactory = FopFactory.newInstance(new File(".").toURI());
		context.registerEventListener(this);
	}
    
    
	@Subscribe public void onExportRequest(final ExportPdfRequest request) {
		destUrl = request.getUrl();
		context.postEvent(new ReportImmediateBuildRequest());
	}
	
	@Subscribe public void onReportFileNotif(final ReportFileNotif notif) {
		sourceUrl = notif.getUrl();
		export();
	}
		
	private void export() {
		if (destUrl == null) return;
		
		try {
			toPdf(destUrl);
			context.postEvent(new PdfExportCompletionNotif().setUrl(destUrl));
		} catch (IOException | FOPException | TransformerException exception) {
			log.error(exception.getMessage(), exception);
		}
		finally {
			destUrl = null;
		}
	}
	
	/**
     * Converts a ProjectTeam object to a PDF file.
     * @param team the ProjectTeam object
     * @param xslt the stylesheet file
     * @param pdf the target PDF file
     * @throws IOException In case of an I/O problem
     * @throws FOPException In case of a FOP problem
     * @throws TransformerException In case of a XSL transformation problem
     */
    public void toPdf(final String outputUrl)
                throws IOException, FOPException, MalformedURLException, TransformerException {
    	
    	// Read source file
    	if (sourceUrl == null) {
    		log.warn("No source file for PDF export");
    		return;
    	}    	
    	File sourceFile = KronoHelper.urlToFile(sourceUrl);
    	if (sourceFile == null || ! sourceFile.canRead()) {
    		log.warn("Unreachable source file for PDF export");
    		return;
    	}
    	//InputSource source = new  
    	
     	File output = KronoHelper.safeUrlToNewFile(outputUrl);
    	File xslt = context.getLocalFile("gameSheet.xslt", ResourceType.CONFIG);
//    	FopFactoryBuilder builder = new FopFactoryBuilder(new URI(sc.getContextPath()), resolver);
//    	FopFactory fopFactory = builder.build();        	
    	FopFactory fopFactory = FopFactory.newInstance(xslt.getParentFile().toURI());
    	
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();        
        foUserAgent.setCreator("oKronos");
        String organizational = context.getProperty("organizational");    	
        if (organizational != null && ! organizational.isEmpty()) {
        	foUserAgent.setAuthor(organizational);
        }
        foUserAgent.setCreationDate(new Date());
        foUserAgent.setTitle("Rapport Officiel da match Roller Hockey");
        foUserAgent.setKeywords("XML XSL-FO");
        
        // Setup output
        OutputStream stream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(output));
        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, stream);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslt));
            // transformer.setURIResolver(new PdfUriResolver(context));

            // Setup input for XSLT transformation
            Source src = new StreamSource(sourceFile);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        } finally {
        	stream.close();
        }
    }    
}
