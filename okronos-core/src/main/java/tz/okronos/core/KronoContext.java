package tz.okronos.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.fxsubscribe.FxSubcriberController;
import tz.okronos.annotation.fxsubscribe.FxSubscribe;

   
/**
 * Central place for all data common to all classes of the application.
 * Contains, among others :
 * - the controllers,
 * - the configuration properties,
 * - the event bus,
 * - the shared data (play properties).
 * Contains equally some shortcut methods, among other for property access,
 * event sending, file retrieval.
 */
@Slf4j
public class KronoContext {
	// Do not create logger before log configuration. See KronoApp class.
	private static final Logger LOGGER = LoggerFactory.getLogger(KronoContext.class);	
	/**
	 *  Kind of resources, each one is contain in some separated folder.
	 */
	public static enum ResourceType {
		/** Configuration data. */
		CONFIG("configPaths"),
		/** Images. */
		IMAGES("imagesPaths"),
		/** Media : tracks and musics. */
		MEDIA("mediaPaths");
		
		/** Identifier used into the init property file. */
		private String label;
		/** List of directories that contains the resources. */
		private List<File> paths;
		
		/**
		 * Builds a resource type.
		 * @param label the label.
		 */
		private ResourceType(String label) {
			this.label = label;
		}
		String getLabel() {
			return label;
		}
		List<File> getPaths() {
			return paths;
		}
		void setPaths(List<File> paths) {
			this.paths = paths;
//          Cannot log as logger is not yet initialized.
//			System.out.println("Paths for " + name() + " : " 
//			    + paths.stream().map(p->pathToString(p)).collect(Collectors.joining(", ")));
		}
	}
	
	/** Configuration properties. */
	@Getter private Properties properties = new Properties();
	
	/** The event bus. */
	@Setter @Getter private EventBus eventBus;
	
	/** Manages the event subscription into the FX main loop. */
	@Setter @Getter FxSubcriberController fxSubcriberController;
	
	/** The root folder of the application. */
	@Getter private File appDirectory;
	
	/** A class loader that searches the resources into the config directories. */
	@Getter  private ConfigClassLoader configClassLoader;
	
	/** For internationalization. */
	@Getter private ResourceBundle resourceBundle;
	
	@Setter @Getter private Stage primaryStage;	

	
	/**
	 * Find a configuration property.
	 * @param key the key.
	 * @param def the default value.
	 * @return the value, or the default value if no match.
	 */
	public String getProperty(String key, String def) {
		return properties.getProperty(key, def);
	}

	/**
	 * Find a configuration property of type boolean. Allowed values are 
	 * yes, no, true, false.
	 * @param key the key.
	 * @param def the default value.
	 * @return the value, or the default value if no match or is the property is not a boolean.
	 */
	public boolean getBooleanProperty(String key, boolean def) {
		String prop = properties.getProperty(key);
		if (prop == null) {
			LOGGER.warn("No property '{}', use default.", key);
			return def;
		}
		prop = prop.trim();
		if (prop.equalsIgnoreCase("yes")) return true;
		if (prop.equalsIgnoreCase("true")) return true;
		if (prop.equalsIgnoreCase("no")) return false;
		if (prop.equalsIgnoreCase("false")) return false;
		
		LOGGER.warn("Invalid boolean for property '{}', use default.", key);
		return def;
	}

	/**
	 * Find a configuration property of type integer.
	 * @param key the key.
	 * @param def the default value.
	 * @return the value, or the default value if no match or is the property is not an integer.
	 */
	public int getIntProperty(String key, int def) {
		String prop = properties.getProperty(key);
		if (prop == null) {
			LOGGER.warn("No property '{}', use default.", key);
			return def;
		}
		
		int res;
		try {
			res = Integer.parseInt(prop.trim());
		} catch (NumberFormatException nfe) {
			LOGGER.warn("Invalid number for property '{}', use default.", key);
			res = def;
		}
		return res;
	}

	/**
	 * Get a property of type list of string. Such a property is a comma separated list of items
	 * (blank characters around the comma are stripped).
	 * @param key the key.
	 * @return the list.
	 */
	public List<String> getPropertyList(String key) {
		String prop = getProperty(key);
		if (prop == null) return null;
		String[] propArray = prop.split("\\s*,\\s*");
		return Arrays.asList(propArray);
	}

	/**
	 * 
	 * @param key the key.
	 * @return the list.
	 */
	
	/**
	 * Get a property of type list of object. Such a property is a comma separated list of object of type R,
	 * the object is build from the string found between the commas. (blank characters around the comma 
	 * are stripped).
	 * @param <R> the type of the items.
	 * @param key the key of the property.
	 * @param transformer the function used to build an item from a string.
	 * @return the list.
	 */
	public <R> List<R> getPropertyList(String key, Function<String,R> transformer) {
		return getPropertyList(key, transformer, t -> t != null);
	}

	/**
	 * Get a property of type list of object. Such a property is a comma separated list of object of type R,
	 * the object is build from the string found between the commas. (blank characters around the comma 
	 * are stripped).
	 * @param <R> the type of the items.
	 * @param key the key of the property.
	 * @param transformer the function used to build an item from a string.
	 * @param filter a predicate used to accept / reject the items.
	 * @return the list.
	 */
	public <R> List<R> getPropertyList(String key, Function<String,R> transformer, 
			Predicate<? super R> filter) {
		List<String> listString = getPropertyList(key);
		List<R> listR = listString.stream()
			.map(transformer)
			.filter(filter)
			.collect(Collectors.toList());
		return listR;
	}

	/**
	 * Log the paths of the resources (the paths cannot be logged when build because
	 * the logger is not yet initialized).
	 */
	public void logPaths() {
		//LOGGER = LogManager.getLogger(KronoContext.class);
		for (ResourceType res : ResourceType.values()) {
			LOGGER.info("Paths for {} : {}", res.name(), pathToString(res));
		}
	}
	
	// Shortcuts.
	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Shortcut to post an event on the event bus.
	 * @param event the event.
	 */
	public void postEvent(Object event) {
		eventBus.post(event);
	}
	
	/**
	 * Read the configuration data.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readProperties() throws FileNotFoundException, IOException {
		readInitialProperties();
		readAppProperties();
		initLogguer();
		loadBundle();
		logPaths();
	}
	
	/**
	 * Gets the URL of a resource of a given type. The first one found is returned.
	 * @param name the file name.
	 * @param resType the type.
	 * @return the URL, or NULL if not found.
	 */
	public URL getResource(String name, ResourceType resType) {
		File res = findInPath(resType.getPaths(), name);
		if (res == null) {
    		throw new IllegalArgumentException(resType.name() +":"+ name);
    	}

		try {
			return res.toURI().toURL();
		} catch (MalformedURLException ex) {
			LOGGER.error("Shouldn't append", ex);
		}
		return null;
	}
	
	/**
	 * Get a file relatively of the application root folder.
	 * If the name of the file is absolute, do not use the root folder.
	 * @param name the name of the file.
	 * @return the file, maybe refer an inexistent one.
	 */
	public File getFile(String name) {
		if (name == null || name.isBlank()) return null;
		
		File candidate = new File(name);
		if (candidate.isAbsolute()) return candidate;
		
		return new File(appDirectory, name);
	}

	/**
	 * Gets the file of a given resource type.
	 * @param name the name of the file.
	 * @param resType the type.
	 * @return the file, NULL if not found.
	 */
	public File getLocalFile(String name, ResourceType resType) {
		if (name == null || name.isBlank()) return null;
		
		return findInPath(resType.getPaths(), name);
	}

	/**
	 * Get all the folder that can contains a resource of a given type.
	 * @param rType the type.
	 * @return the list of paths. All paths exist.
	 */
	public List<File> getPaths(ResourceType rType) {
		return rType.getPaths();
	}

	/**
	 * Shortcut to get a internationalized string.
	 * @param key the key.
     * @exception NullPointerException if <code>key</code> is <code>null</code>
     * @exception MissingResourceException if no object for the given key can be found
	 * @return the matching value.
	 */
	public String getItString(String key) {
		return resourceBundle.getString(key);
	}

	/**
	 * Loads the bundle for internationalization.
	 */
	public void loadBundle() {
		resourceBundle = ChainablePropertyBundle.loadBundlesByKey(this, "labelFiles");
	}

	/** 
	 * Registers an event listener into the event bus. Shall be called preferably to {@code getEventBus().register(Object) }
	 * because handles the registration into the FX main loop with {@link FxSubscribe}. 
	 * 
	 * @param object the instance to register.
	 */
	public void registerEventListener(Object object) {
		fxSubcriberController.register(object);
	}
	
	/** 
	 * Unregister from the event listener. Shall be called when registered with {@link #registerEventListener(Object)}.
	 * 
	 * @param object the instance to unregister.
	 */
	public void unregisterEventListener(Object object) {
		fxSubcriberController.unregister(object);
	}
	
	/**
	 * Reads the init file and loads its properties.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    private void readInitialProperties() 
    		throws FileNotFoundException, IOException  {
    	File configFile;
    	String configProp = System.getProperty("configfile");
    	if (configProp != null) {
    		configFile = new File(configProp);
    	} else {
	    	// Get the location of the jar file.
	    	URL jarUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
	    	File jarFile = new File(jarUrl.getFile());
	    	File iniDir = new File(jarFile.getParentFile(), "datasets");
	    	configFile = new File(iniDir, "init.properties");
    	}
    	appDirectory  = configFile.getParentFile();
    	// System.out.println("Config file: " + pathToString(configFile));
    	if (! configFile.canRead()) {
    		throw new FileNotFoundException(configFile.getPath());
    	}
    	
        this.properties.load(new FileReader(configFile));
        
        Function<String, File> transformer = n -> new File(appDirectory, n);
    	ResourceType.CONFIG.setPaths(getPropertyList(ResourceType.CONFIG.getLabel(), transformer, File::isDirectory));
    	ResourceType.IMAGES.setPaths(getPropertyList(ResourceType.IMAGES.getLabel(), transformer, File::isDirectory));
    	ResourceType.MEDIA.setPaths(getPropertyList(ResourceType.MEDIA.getLabel(), transformer, File::isDirectory));
	
    	configClassLoader = new ConfigClassLoader();
    }

    /**
     * Initializes the logback logger.
     */
    private void initLogguer() {
    // Sets log path.
    String logPath = getProperty("logPath", "gen/log");
   	File logDir = getFile(logPath);
   	System.setProperty("logPath", logDir.getAbsolutePath());

    	// assume SLF4J is bound to logback in the current environment
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    
    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      context.reset(); 
      configurator.doConfigure(getLocalFile("logback.xml", ResourceType.CONFIG));
    } catch (JoranException je) {
      // StatusPrinter will handle this
    }
    StatusPrinter.printInCaseOfErrorsOrWarnings(context);

    LOGGER.info("Logback configured.");
  }

    
    /**
     * Find the first file found into a list of directories.
     * @param paths the directories.
     * @param fileName the name of the file.
     * @return the file, or NULL if not found.
     */
    private File findInPath(List<File> paths, String fileName) {
     	for (File dir : paths) {
    		File candidate = new File(dir, fileName);
    		if (candidate.canRead()) return candidate;
    	}
    	return null;
    }
    
    /**
     * Loads the application configuration data.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void readAppProperties() 
    		throws FileNotFoundException, IOException  {
    	List<String> configNames = getPropertyList("configFiles");
    	
    	for (String configName : configNames) {
    		File configFile = findInPath(ResourceType.CONFIG.getPaths(), configName);
    		if (configFile == null) {
    			log.warn("Configuration file not found: " + configName);
    		}
    		else {
    			this.properties.load(new FileReader(configFile));
    		}
    	}
	}
    
    /**
     * For logging, display all paths of a type of resource.
     * @param rsc the resource type.
     * @return the human readable paths.
     */
	private static String pathToString(ResourceType rsc) {
		return rsc.getPaths().stream().map(p->pathToString(p)).collect(Collectors.joining(", "));
	}

    /**
     * For logging, display a file.
     * @param file the file.
     * @return the human readable file name and type.
     */
	private static String pathToString(File file) {
		String res;
		try {
			res =  file.getCanonicalPath();
		} catch (IOException ex) {
			return ex.getMessage();
		}
		res = res + " (" 
		    + (file.isDirectory() ? "dir" : "")
		    + (file.isFile() ? "file" : "")
		    + (file.canRead() ? "" : " cannot read")
		    + ")";
		return res;
	}

}
