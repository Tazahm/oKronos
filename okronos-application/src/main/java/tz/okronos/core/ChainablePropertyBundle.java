package tz.okronos.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import lombok.extern.slf4j.Slf4j;
import tz.okronos.core.KronoContext.ResourceType;

/**
 * Property bundle that can be chain with others, so that the search of a property
 * is made on all bundles of the chain. The first match stops the search.
 */
@Slf4j
public class ChainablePropertyBundle extends PropertyResourceBundle {
	
	/**
	 * Loads a bundle.
	 * @param context the context.
	 * @param name its file name.
	 * @return the bundle, null if not found.
	 */
	public static ChainablePropertyBundle loadBundle(KronoContext context, String name) {
		File file = context.getLocalFile(name, ResourceType.CONFIG);
		if (file == null || ! file.canRead()) return null;
		try (FileReader reader = new FileReader(file, Charset.forName("UTF-8"))) {
            return new ChainablePropertyBundle(reader);
        } catch (IOException ex) {
        	log.warn(ex.getMessage(), ex);
        }
		return null;
	}
	
	/**
	 * Loads the bundles and chains them together. The first in the list is returned, it has greater
	 * precedence on the second, and so following.
	 * @param context the context.
	 * @param names the names.
	 * @return the first bundle.
	 */
	public static ChainablePropertyBundle loadBundles(KronoContext context, List<String> names) {
		ChainablePropertyBundle last = null;
		ChainablePropertyBundle first = null;
		for (String name : names) {
			ChainablePropertyBundle current = loadBundle(context, name);
			if (current != null) {
				if (last != null) {
					last.setParent(current);
				}
				if (first == null) {
					first = current;
				}
				last = current;
			}
		}
		
		return first;
	}
	
	/**
	 * Loads the bundles and chains them together. The first in the list is returned, it has greater
	 * precedence on the second, and so following.
	 * @param context the context.
	 * @param listName the names of the list property that gives the name of the files.
	 * @return the first bundle.
	 */
	public static ChainablePropertyBundle loadBundlesByKey(KronoContext context, String listName) {
		return loadBundles(context, context.getPropertyList(listName));
	}

	/**
	 * Builds a bundle.
	 * @param reader the reader.
	 * @throws IOException if unreachable.
	 */
	public ChainablePropertyBundle(Reader reader) throws IOException {
		super(reader);
	}
	
	// @Override
	public void setParent(ResourceBundle parent) {
		super.setParent(parent);
	}
            
}
