package sep.gaia.resources.tiles2d;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GLProfile;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.Cache;
import sep.gaia.resources.DataResource;
import sep.gaia.resources.DataResourceManager;
import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.resources.QuerySplitter;
import sep.gaia.resources.tiles2d.Style.SubServer;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.IntegerBoundingBox;

/**
 * <code>StateObservable</code>: <code>TileManager</code> is responsible for the
 * 2d map tiles. It's parent class <code>DataResourceManager</code> is a
 * <code>StateObserver</code>. So <code>TileManager</code> observes the
 * <code>TileState</code> and if it gets notified, it will update it's
 * <code>usedResourcesSet</code> immediately and search in it's cache classes
 * <code>TemporaryCache</code> and <code>PersistantCache</code> for the now
 * current necessary <code>TileResources</code>. If it requests a cache miss,
 * <code>TileManager</code> will query the necessary <code>TileResource</code>
 * objects to the <code>TileLoader</code> object for loading the missing tiles
 * of the API servers.
 * 
 * <code>ResourceObservable</code>: As <code>TileManager</code> is a subclass of
 * <code>DataResourceManager</code>, it's also a <code>ResourceObservable</code>
 * and therefore observed by <code>ResourceAdapter</code> implementations.
 * <code>TileManager</code> will immediately notify its observers, after its
 * <code>usedResourceSet</code> is updated.
 * 
 * @see <code>DataResourceManager</code>
 * @see <code>TileState</code>+
 * @see <code>TileLoader</code>
 * 
 * @author Johannes Bauer (specification), Matthias Fisch, Johannes Bauer (implementation)
 * 
 */
public final class TileManager extends DataResourceManager<TileResource>
		implements LoaderEventListener<TileResource>, QuerySplitter<TileQuery> {

	private class SubServerTileList {

		private SubServer subServer;

		private List<DataResource> tiles = new LinkedList<>();

		public SubServerTileList(SubServer subServer) {
			super();
			this.subServer = subServer;
		}

		public boolean add(TileResource tile) {
			return tiles.add(tile);
		}

		public List<DataResource> getResources() {
			return tiles;
		}

	}

	/**
	 * The label used to identify a TileManager.
	 */
	public static final String MANAGER_LABEL = "TileManager";

	private static final int PRELOAD_COUNT = 3;

	private TileCache cache;

	/**
	 * The style current used - specifies also the API server(s) that are
	 * currently used.
	 * 
	 * @see <code>Style</code>
	 */
	private Style currentStyle;

	/**
	 * List of all available styles and therefore all tile servers which GAIA
	 * knows.
	 */
	private List<Style> styles;

	/**
	 * Responsible for loading requested <code>TileResource</code> objects.
	 */
	private Loader<TileQuery, TileResource> loader;

	/**
	 * Current used and loaded tiles. Used as a additional "cache".
	 */
	private Collection<TileResource> loadedTiles = new LinkedList<>();

	private GLProfile glProfile;
	
	/**
	 * Lock for current used and loaded tiles.
	 */
	private Lock loadedResourcesLock = new ReentrantLock();

	/**
	 * Initializes a tile-manager using a persistent cache.
	 * 
	 * @param glProfile
	 *            The OpenGL-Profile to use.
	 */
	public TileManager(GLProfile glProfile) {
		super(MANAGER_LABEL, false, false);

		this.cache = new TileCache(this, glProfile);

		/*
		 * A loader must be created for performing queries for tiles. Also it
		 * will use a TileCache as a on-disk-cache.
		 */
		loader = new Loader<TileQuery, TileResource>(this.cache,
				new TileWorkerFactory(glProfile), this);

		loader.addListener(this); // Listen for new data available

		loader.start();

		loadStylesFromXML(); // Read the available styles from file
	}

	/**
	 * Initializes a tile-manager using a persistent cache.
	 * 
	 * @param glProfile
	 *            The OpenGL-Profile to use.
	 */
	public TileManager(GLProfile glProfile, int maximumCacheSize) {
		super(MANAGER_LABEL, false, false);

		/*
		 * A loader must be created for performing queries for tiles. Also it
		 * will use a TileCache as a on-disk-cache.
		 */
		TileCache cache = new TileCache(this, glProfile);
		cache.setMaxEntries(maximumCacheSize);

		loader = new Loader<TileQuery, TileResource>(cache,
				new TileWorkerFactory(glProfile), this);

		loader.addListener(this); // Listen for new data available

		loader.start();

		loadStylesFromXML(); // Read the available styles from file
	}

	/**
	 * Reads all available styles from the XML-formatted file. The location of
	 * this is specified by the variable <code>TILE_STYLE_FILE</code> of
	 * <code>Environment</code>. The read XML is checked for fulfilling the
	 * format specified in the XML-schema stored at the path with the
	 * environment-variable <code>TILE_STYLE_FILE_SCHEMA</code>. The styles
	 * generated are stored in <code>styles</code>. The first style in the index
	 * will picked as the initial style.
	 * 
	 * @return <code>true</code> if the file could be successfully read and both
	 *         the XML and its schema was formatted correctly.
	 *         <code>false</code> will be returned otherwise.
	 */
	public boolean loadStylesFromXML() {

		this.styles = new LinkedList<>();

		// Get the application-environment:
		Environment environment = Environment.getInstance();
		// Get the XML-file containing the styles:
		String xmlPath = environment.getString(EnvVariable.TILE_STYLE_FILE);
		// Get the XML-Schema-Definition describing valid formats for the
		// XML-file:
		String schemaPath = environment
				.getString(EnvVariable.TILE_STYLE_FILE_SCHEMA);

		Schema schema;
		// Get a factory for creation of new XML-schemas:
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			// Create the schema from the XSD-file:
			schema = schemaFactory.newSchema(new File(schemaPath));
		} catch (SAXException e) {
			return false;
		}
		// Get a validator for the read schema:
		Validator schemaValidator = schema.newValidator();

		// Get a factory for creation of document-builders and create one:
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// Return with error if the builder was not configured correctly:
			return false;
		}

		// Now get the actual document:
		Document doc;
		try {
			doc = docBuilder.parse(new File(xmlPath));
		} catch (SAXException | IOException e) {
			// If the XML could not be parsed or if the file does not exist:
			return false;
		}

		// Validate the XML:
		try {
			schemaValidator.validate(new DOMSource(doc));
		} catch (SAXException | IOException e) {
			// If validation fails or if the underlying XMLReader throws
			// IOException:
			return false;
		}

		// Get all style-tags:
		NodeList styleNodes = doc.getElementsByTagName("style");

		// Iterate all style-tags:
		for (int i = 0; i < styleNodes.getLength(); i++) {
			// The variables to be read:
			String name = null; // The name of the style
			String syntax = null; // The syntax of the server-url
			List<Style.SubServer> subservers = new LinkedList<>(); // List of
																	// possible
																	// subservers
			int minZoom = 1; // The minimum zoomlevel the server accepts
			int maxZoom = 18; // The maximum zoomlevel the server accepts

			// Get the current style-tag:
			Node currentStyleTag = styleNodes.item(i);
			// Get the childs of this tag:
			NodeList childs = currentStyleTag.getChildNodes();
			for (int j = 0; j < childs.getLength(); j++) {
				// Get current child:
				Node currentChild = childs.item(j);
				// The content of the tag and the nodes tag:
				String inner = currentChild.getTextContent();
				String tag = currentChild.getNodeName();

				// Fill variables by tag-type:
				if (tag.equals("name")) {
					name = inner;

				} else if (tag.equals("syntax")) {
					syntax = inner;

				} else if (tag.equals("subserver")) {

					String subServerHost = null;
					int subServerMaxCons = 1;

					// The subserver-child containes childs itself. Iterate
					// them:
					NodeList subServerAttributes = currentChild.getChildNodes();
					for (int k = 0; k < subServerAttributes.getLength(); k++) {
						Node subServerAttribute = subServerAttributes.item(k);

						inner = subServerAttribute.getTextContent();
						tag = subServerAttribute.getNodeName();

						// Check for the valid tags and set the respective
						// variable:
						if (tag.equals("host")) {
							subServerHost = inner;

						} else if (tag.equals("maxcons")) {
							try {
								subServerMaxCons = Integer.parseInt(inner);

							} catch (NumberFormatException e) {
								// On error, stay at 1
							}
						}
					}

					// If could be evaluated correctly:
					if (subServerHost != null && subServerMaxCons > 0) {
						Style.SubServer subServer = new Style.SubServer(
								subServerHost, subServerMaxCons);
						subservers.add(subServer);
					}

				} else if (tag.equals("minzoom")) {
					minZoom = Integer.parseInt(inner);

				} else if (tag.equals("maxzoom")) {
					maxZoom = Integer.parseInt(inner);

				}
			}

			// If all variables have been set:
			if (name != null && syntax != null && !subservers.isEmpty()) {
				// Create the style
				Style style = new Style(name, subservers, syntax, minZoom,
						maxZoom);
				styles.add(style); // Add to collection of all styles
			}
		}
		// Select the first style as the initial style:
		if (!styles.isEmpty()) {
			currentStyle = styles.get(0);
		}
		return true;
	}

	/**
	 * Returns all styles that <code>TileManager</code> holds.
	 * 
	 * @return All available styles that GAIA knows. These <code>Style</code>s
	 *         are necessary for knowing the URL of the tile server and the
	 *         <code>label</code> member of each style specifies the directory
	 *         name for saving the <code>TileResource</code> objects to the
	 *         <code>PersistentCache</code>. If there are no styles at all, e.g.
	 *         because parsing the specifying file failed, returns an empty
	 *         list.
	 */
	public List<Style> getAvailableStyles() {
		// If no styles were read yet, load them from the style index-file:
		if (styles == null) {
			loadStylesFromXML();
		}
		return styles;
	}

	@Override
	public Collection<TileQuery> splitQuery(TileQuery query) {
		Collection<TileQuery> splittedQueries = new LinkedList<>();

		if (query != null) {

			Collection<DataResource> resources = query.getResources();

			List<SubServerTileList> tileLists = new LinkedList<>();
			for (SubServer subServer : currentStyle.getSubServers()) {
				int maxConnections = subServer.getMaxConnections();
				for (int i = 0; i < maxConnections; i++) {
					tileLists.add(new SubServerTileList(subServer));
				}
			}

			ListIterator<SubServerTileList> tileListsIter = tileLists
					.listIterator();
			for (DataResource resource : resources) {
				if (resource instanceof TileResource) {
					SubServerTileList list = tileListsIter.next();
					list.add((TileResource) resource);

					if (!tileListsIter.hasNext()) {
						tileListsIter = tileLists.listIterator();
					}
				}
			}

			for (SubServerTileList list : tileLists) {
				List<DataResource> listResources = list.getResources();
				if (!listResources.isEmpty()) {
					splittedQueries.add(new TileQuery(listResources));
				}
			}
		}
		return splittedQueries;
	}

	@Override
	public void onUpdate(State state) {
		// This manager only processes updates from TileState:
		if (currentStyle != null && state != null && state instanceof GLState
				&& isEnabled()) {

			GLState glState = (GLState) state;

			int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());

			IntegerBoundingBox toLoad = AlgoUtil.glToTile(
					glState.getBoundingBox(), glState.getZoom());

			loader.requestWorkersStop();
			loader.clearQueryQueue();

			// Generate dummies for current needed tiles.
			Collection<DataResource> dummies = new LinkedList<>();
			for (int y = toLoad.getUpperLeft().getY(); y <= toLoad
					.getLowerLeft().getY(); y++) {
				for (int x = toLoad.getUpperLeft().getX(); x <= toLoad
						.getUpperRight().getX(); x++) {
					TileResource dummy = createDummy(x, y, tileZoom);
					dummies.add(dummy);
				}
			}

			loadedResourcesLock.lock();
			
			// Now check if needed resources a already used by consumers.
			Iterator<DataResource> iter = dummies.iterator();
			Collection<DataResource> alreadyLoaded = new LinkedList<>();
			while (iter.hasNext()) {
				DataResource current = iter.next();
				if (loadedTiles.contains(current)) {
					alreadyLoaded.add(current);
				}
			}

			// Now check for loaded tiles which aren't needed any more.
			Iterator<TileResource> tileIter = loadedTiles.iterator();
			Collection<DataResource> notNeeded = new LinkedList<>();
			while (tileIter.hasNext()) {
				DataResource current = tileIter.next();
				if (!dummies.contains(current)) {
					notNeeded.add(current);
				}
			}

			// Remove resources that a not needed anymore.
			loadedTiles.removeAll(notNeeded);
			
			loadedResourcesLock.unlock();

			// Remove resources that a already loaded.
			dummies.removeAll(alreadyLoaded);

			TileQuery query = new TileQuery(dummies);
			loader.request(query);
		}
	}

	@Override
	public void onResourcesAvailable(Collection<TileResource> resources) {
		
		loadedResourcesLock.lock();
		// Only notify if the manager is enabled:
		if (isEnabled()) {
			for (TileResource tile : resources) {
				if (!tile.isDummy() && !loadedTiles.contains(tile)) {
					loadedTiles.add(tile);
				}
			}

			// Add all already loaded tile resources.
			resources.addAll(loadedTiles);

			notifyAll(resources);
		}
		loadedResourcesLock.unlock();
	}

	@Override
	public void requestLoaderStop() {
		loader.interrupt();
	}

	@Override
	public void onExit() {
		if (loader != null) {
			Cache<TileResource> cache = loader.getCache();
			if (cache instanceof TileCache) {
				TileCache tileCache = (TileCache) cache;
				if (tileCache != null) {
					// Write the caches index-file:
					tileCache.writeBack();
				}
			}
		}
	}

	private TileResource createDummy(int x, int y, int zoom) {
		TileResource tile = new TileResource(x, y, zoom);
		tile.setDummy(true);
		tile.setStyle(currentStyle);
		return tile;
	}

	/**
	 * Returns all styles available.
	 * 
	 * @return All styles available.
	 */
	public List<Style> getStyles() {
		return styles;
	}

	/**
	 * Sets the active style.
	 * 
	 * @param currentStyle
	 *            The active style.
	 */
	public void setCurrentStyle(Style currentStyle) {
		this.currentStyle = currentStyle;

		// Force update
		GLState glState = (GLState) StateManager.getInstance().getState(
				StateType.GLState);
		onUpdate(glState);
	}

	/**
	 * Returns the active style.
	 * 
	 * @return The active style.
	 */
	public Style getCurrentStyle() {
		return currentStyle;
	}

	public TileCache getCache() {
		return cache;
	}
	
	
}