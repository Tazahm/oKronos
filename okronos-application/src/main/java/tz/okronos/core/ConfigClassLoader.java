package tz.okronos.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import tz.okronos.core.KronoContext.ResourceType;

/**
 *  A class loader that searches the resources into the config directories.
 */
public class ConfigClassLoader extends URLClassLoader {
	@Autowired private KronoContext context;
	
	public ConfigClassLoader() {
		super("ConfigClassLoader", new URL[] {}, new String().getClass().getClassLoader());
	}
	
    @PostConstruct 
    public void init()  {
		addUrlFromContext();
	}

	private void addUrlFromContext() {
		context.getPaths(ResourceType.CONFIG).stream().map(this::toURLInsecure).filter(u->u!=null)
		    .forEach(u->addURL(u));
	}

	private URL toURLInsecure(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
