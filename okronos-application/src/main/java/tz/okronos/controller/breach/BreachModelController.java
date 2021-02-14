package tz.okronos.controller.breach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.eventbus.Subscribe;

import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.breach.model.BreachDesc;
import tz.okronos.controller.breach.model.BreachModel;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.KronoContext.ResourceType;
import tz.okronos.event.request.ResetPlayRequest;


/**
 * Handles the description of breaches. Breaches are used to build a penalty.
 */
@Slf4j
@Configuration
public class BreachModelController extends AbstractModelController<BreachModel> {
	@Autowired private BreachModel model;
	
	
	public BreachModelController() {		
	}
	
	@PostConstruct public void init() {
		context.registerEventListener(this);
	}

  	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reload();
  	}

  	private void reloadUnchecked() throws FileNotFoundException, IOException {
  		final String name = "penalties.csv";
  		final File file = context.getLocalFile(name, ResourceType.CONFIG);
  		
		CsvSchema schema = CsvSchema.builder()
			.addColumn("code")
			.addColumn("label")
			.build();
		  CsvMapper mapper = new CsvMapper();
		  ObjectReader objectReader = mapper.readerFor(BreachDesc.class).with(schema);
		  ArrayList<BreachDesc> breaches = new ArrayList<>();
		  try (Reader reader = new FileReader(file)) {
		     MappingIterator<BreachDesc> mi = objectReader.readValues(reader);
		     while (mi.hasNext()) {
		    	 BreachDesc breach = mi.next();
		    	 breaches.add(breach);
		     }
		  }
		  
		  model.getBreachListWrapper().clear();
		  model.getBreachListWrapper().addAll(breaches);
	}

  	private void reload() {
  		try {
  			reloadUnchecked();
  		} catch (IOException ex) {
			log.error(ex.getMessage());
		}
  	}

	@Override
	protected BreachModel getModel() {
		return model;
	}
  	
}
