/**
 *
 */
package sep.gaia.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Singleton-class for loading image-files that can be used
 * as a graphical representation of POIs. The symbol-files
 * are loaded from a directory in the applications directory.
 * A config-file is read for mapping names of categories to their respective
 * symbols.
 * @author Matthias Fisch
 *
 */
public class SymbolLoader {

	/**
	 * The one POISymbolLoader existing or <code>null</code> if none was created yet.
	 */
	private static SymbolLoader instance;

	/**
	 * A collection mapping keys to their
	 */
	private Map<String, File> symbolFiles = new HashMap<>();

	/**
	 * Returns the one existing loader. If no instance exists when calling the method,
	 * a loader is created.
	 */
	public void getInstance() {

	}

	/**
	 * Reads and parses the symbol-mapping-file (<code>KEY_SYMBOL_MAP_FILE</code>).
	 * The keys are mapped to their respective image in <code>symbolFiles</code>.
	 * @return <code>true</code> if the file was formatted properly. <code>false</code>
	 * on error.
	 */
	private boolean parseSymbolMap() {
		return true;
	}

	/**
	 * Returns the image <code>code</code> is mapped to.
	 * The symbol-file a key is mapped to is read from a configuration-file
	 * (cf. environment-variable <code>KEY_SYMBOL_MAP_FILE</code>). 
	 * Note that this method will block until the file is loaded and should be 
	 * executed asynchronously if the symbol-file is large.
	 * @param key The key of the symbol to be loaded.
	 * @return The symbol <code>key</code> is mapped to or <code>null</code> if the key does
	 * not exist.
	 */
	public BufferedImage getSymbol(String key) {
		return null;
	}
}