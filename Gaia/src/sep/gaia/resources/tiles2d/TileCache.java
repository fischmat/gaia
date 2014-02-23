/**
 * 
 */
package sep.gaia.resources.tiles2d;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GLProfile;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.controller.settings.SliderListener;
import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.Cache;
import sep.gaia.util.IntegerVector3D;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * This class is responsible for storing the <code>TileResource</code> objects
 * on the hdd. How it performs this task is not visible for the other classes.
 * 
 * The complexity of the caching strategy will maybe changing during later
 * milestones.
 * 
 * At this point, the strategy is planed as a mix of LRU (Least recently used)
 * and LFU (Least frequently used).
 * 
 * For each <code>Style</code> a directory will be created. The single
 * <code>TileResource</code> files will be stored accoring to their coordinates.
 * 
 * For example: <code>TileResource</code> tileA with <code>Style</code> styleB
 * and coordinates x and y and zoom z. The file tree will loke like this:
 * "styleA" |-> "z" -> "x" -> "y.gtr" "y.gtr" is the serialized
 * <code>TileResource</code> object, "gtr" is the file ending and stands for
 * "GAIA Tile Resource".
 * 
 * @author Johannes Bauer (specification), Matthias Fisch (implementation)
 */
public class TileCache extends Cache<TileResource> {

	/**
	 * Represents a tile persistantly cached.
	 * Additionally to the attributes of a common tile-resource (see <code>TileResource</code>)
	 * instances of this class hold the file where the tile image-information is stored.
	 * @author Matthias Fisch
	 *
	 */
	protected class CachedTile extends TileResource {
		private static final long serialVersionUID = -3505995907161115884L;

		/**
		 * The file where the tiles image-information is stored.
		 */
		private File file;
		
		/**
		 * How often the tile was used yet.
		 */
		private int useCount;
		
		/**
		 * Initializes the description for a cached tile.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param zoom The zoom-level of the tile.
		 * @param useCount How often this resource was used in the past.
		 * @param file The file where the image-information is stored.
		 * @throws IllegalArgumentException Thrown if the x- or y-coordinate is not in
		 * range [1, 2^<code>zoom</code>]. Also <code>zoom</code> must be in range [0, 18].
		 */
		public CachedTile(int x, int y, int zoom, int useCount, File file)
				throws IllegalArgumentException {
			super(x, y, zoom);
			this.useCount = useCount;
			this.file = file;
		}
		
		/**
		 * Initializes the description for a cached tile.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param zoom The zoom-level of the tile.
		 * @param useCount How often this resource was used in the past.
		 * @param filePath The path to the file where the image-information is stored.
		 * @throws IllegalArgumentException Thrown if the x- or y-coordinate is not in
		 * range [1, 2^<code>zoom</code>]. Also <code>zoom</code> must be in range [0, 18].
		 */
		public CachedTile(int x, int y, int zoom, int useCount, String filePath)
				throws IllegalArgumentException {
			super(x, y, zoom);
			this.useCount = useCount;
			this.file = new File(filePath);
		}

		/**
		 * Returns the file where the tiles image-information is stored.
		 * @return The file where the tiles image-information is stored.
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Sets the file where the tiles image-information is stored.
		 * @param file The file where the tiles image-information is stored.
		 */
		public void setFile(File file) {
			this.file = file;
		}
		
		/**
		 * Returns how often the tile was used yet.
		 * @return How often the tile was used yet.
		 */
		public int getUseCount() {
			return useCount;
		}

		/**
		 * Sets how often the tile was used yet.
		 * @param useCount How often the tile was used yet.
		 */
		public void setUseCount(int useCount) {
			this.useCount = useCount;
		}
	}
	
	/**
	 * The manager associated with the cache.
	 */
	private TileManager manager;
	
	/**
	 * The OpenGL-profile to use when creating texture-data.
	 */
	private GLProfile profile;
	
	/**
	 * Current size of the image-files of all styles in bytes.
	 */
	private long currentSizeOnDisk;
	
	/**
	 * The maximum size of image-files of all styles in bytes.
	 */
	private long maximumSizeOnDisk = SliderListener.getMaximum();

	/**
	 * Initializes the cache with the management-objects for the tiles to cache, as well
	 * as the OpenGL-profile to be used for loading the stored tiles texture-data.
	 * The maximum size of the cache on disk is set to maximum.
	 * @param manager The TileManager who holds the <code>TileLoader</code> 
	 * @param profile The OpenGL-profile to use when creating texture-data.
	 */
	public TileCache(TileManager manager, GLProfile profile) {
		this.manager = manager;
		this.profile = profile;
		if(!load()) {
			Logger.getInstance().error("Failed to load cache-index!");
		}
	}
	
	/**
	 * Initializes the cache with the management-objects for the tiles to cache, as well
	 * as the OpenGL-profile to be used for loading the stored tiles texture-data.
	 * @param manager The TileManager who holds the <code>TileLoader</code> 
	 * @param profile The OpenGL-profile to use when creating texture-data.
	 * @param maximumSizeOnDisk The maximum size of image-files of all styles in bytes.
	 */
	public TileCache(TileManager manager, GLProfile profile, long maximumSizeOnDisk) {
		this.maximumSizeOnDisk = maximumSizeOnDisk;
		this.manager = manager;
		this.profile = profile;
		if(!load()) {
			Logger.getInstance().error("Failed to load cache-index!");
		}
	}

	@Override
	public TileResource get(String key) {
		// Check if the requested tile is in cache:
		TileResource result = super.get(key);
		
		if(result != null && result instanceof CachedTile) {
			CachedTile entry = (CachedTile) result;
			// Create the new tile-resource:
			IntegerVector3D coords = entry.getCoord();
			TileResource tile = new TileResource(coords.getX(), coords.getY(), coords.getZ());
			
			// The created tiles timestamp equals the results one:
			tile.setTimestamp(entry.getTimestamp());
			
			Style style = entry.getStyle();
			tile.setStyle(style); // Apply the style
			
			// Set the tiles image-data:
			String suffix = style.getImageSuffix();
			TextureData texData;
			try {
				texData = TextureIO.newTextureData(profile, entry.getFile(), false, suffix);
				
			} catch (IOException e) {
				return null;
			}
			tile.setTextureData(texData);
			
			// The resource is now valid:
			tile.setDummy(false);
			
			return tile;
			
		} else {
			return null;
		}
	}

	/**
	 * Adds an tile to the cache and writes its image-data to a file.
	 * @param tile The tile to be cached.
	 * @return <code>true</code> if <code>tile</code> was successfully added or already present.
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean add(TileResource tile) {
		
		// If not already in cache:
		if(get(tile.getKey()) == null) {
			
			// Try to write the image-data:
			File cachedFile = writeCacheFile(tile);
			
			if(cachedFile != null) {
				// Add the written files size the the counter:
				currentSizeOnDisk += cachedFile.length();
				
				// Create a new entry and add it:
				IntegerVector3D coords = tile.getCoord();
				// When inserted into the cache the resource was used once:
				CachedTile entry = new CachedTile(coords.getX(), coords.getY(), coords.getZ(), 1, cachedFile);
				entry.setTimestamp(tile.getTimestamp());
				entry.setStyle(tile.getStyle());
				return super.add(entry);
				
			} else {
				return false;
			}
			
		} else {
			return true;
		}
		
	}
	
	/**
	 * Writes the image-data of a tile-resource to a file. The path to the file is:
	 * <i>&lt;CACHE_ROOT_DIR&gt;/&lt;URL-encoded style&gt;/z/x/y.&lt;style image-suffix&gt;</i>.
	 * If any of the directories is not existent it will be created.
	 * @param tile The tile which data should be written.
	 * @return The file written to or <code>null</code> if an error occured.
	 */
	private File writeCacheFile(TileResource tile) {
		if(tile != null && tile.getTextureData() != null) {
			// Generate the path were the file must be stored:
			String separator = System.getProperty("file.separator");
			StringBuilder path = new StringBuilder();
			path.append(Environment.getInstance().getString(EnvVariable.CACHE_ROOT_DIR));
			path.append(separator);
			try {
				path.append(URLEncoder.encode(tile.getStyle().getLabel(), "UTF-8"));
				
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			path.append(separator);
			// Sub-directory for all tiles in the respective zoom-level:
			path.append(Integer.toString(tile.getCoord().getZ()));
			path.append(separator);
			// Sub-directory for all tiles in the respective x-coordiante:
			path.append(Integer.toString(tile.getCoord().getX()));
			path.append(separator);
			
			File folder = new File(path.toString());
			// Create any not existent directories:
			if(!folder.exists()) {
				folder.mkdirs();
			}
			
			// Complete the path by adding the files name:
			String suffix = "." + tile.getStyle().getImageSuffix();
			path.append(tile.getCoord().getY());
			path.append(suffix);
			
			// Create the file and write data:
			File cacheFile = new File(path.toString());
			
			// Create the file if does not exist yet:
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {
				return null;
			}
			
			if(!cacheFile.canWrite()) {
				return null;
			}
			try {
				TextureIO.write(tile.getTextureData(), cacheFile);
				
			} catch (IOException e) {
				return null;
			}
			return cacheFile;
			
		} else {
			return null;
		}
	}
	
	@Override
	public Collection<CacheEntry> manage() {
		
		if(currentSizeOnDisk >= maximumSizeOnDisk) {
			// Synchronously clean memory-index and get all entries removed there:
			final Collection<CacheEntry> removed = super.purge();
			
			Runnable deleteFilesRoutine = new Runnable() {
				
				@Override
				public void run() {
					// Remove the correspondent file for each entry:
					for(CacheEntry entry : removed) {
						TileResource resource = entry.get();
						
						if(resource instanceof CachedTile) {
							CachedTile cachedTile = (CachedTile) resource;
							
							File cacheFile = cachedTile.getFile();
							// Substract the freed size from counter:
							currentSizeOnDisk -= cacheFile.length();
							
							cacheFile.delete();
						}
					}
				}
			};
			
			// Asynchronously delete files:
			new Thread(deleteFilesRoutine).start();
			
			return removed;
			
		} else {
			// If nothing had to be removed return an empty collection:
			return new LinkedList<>();
		}
	}
	
	/**
	 * Reads information about the currently avaliable files from a index-file.
	 * For the format of the file see tilecache.xsd.
	 */
	private boolean load() {
		// Get the application-environment:
		Environment environment = Environment.getInstance();
		// Get the XML-file containing the cached tiles:
		String xmlPath = environment.getString(EnvVariable.TILE_CACHE_INDEX_FILE);
		// Get the XML-Schema-Definition describing valid formats for the XML-file:
		String schemaPath = environment.getString(EnvVariable.TILE_CACHE_INDEX_FILE_SCHEMA);
		
		Schema schema;
		// Get a factory for creation of new XML-schemas:
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			// Create the schema from the XSD-file:
			schema = schemaFactory.newSchema(new File(schemaPath));
		} catch (SAXException e) {
			Logger.getInstance().error("Error loading cache-index: " + e.getMessage());
			return false;
		}
		// Get a validator for the read schema:
		Validator schemaValidator = schema.newValidator();
		
		
		// Get a factory for creation of document-builders and create one:
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// Return with error if the builder was not configured correctly:
			Logger.getInstance().error("Error loading cache-index: " + e.getMessage());
			return false;
		}
		
		// Now get the actual document:
		Document doc;
		try {
			doc = docBuilder.parse(new File(xmlPath));
		} catch (SAXException | IOException e) {
			// If the XML could not be parsed or if the file does not exist:
			Logger.getInstance().error("Error loading cache-index: " + e.getMessage());
			return false;
		}
		
		// Validate the XML:
		try {
			schemaValidator.validate(new DOMSource(doc));
		} catch(SAXException | IOException e) {
			// If validation fails or if the underlying XMLReader throws IOException:
			Logger.getInstance().error("Error loading cache-index: " + e.getMessage());
			return false;
		}
		
		// Get all tile-tags:
		NodeList tileNodes = doc.getElementsByTagName("tile");
		
		// Iterate all tile-tags:
		for(int i = 0; i < tileNodes.getLength(); i++) {
			// The variables to be read:
			int x = -1;
			int y = -1;
			int zoom = -1;
			String style = null;
			int useCount = -1;
			long timeStamp = -1;
			String fileName = null;
			
			// Get the current tile-tag:
			Node currentTileTag = tileNodes.item(i);
			// Get the childs of this tag:
			NodeList childs = currentTileTag.getChildNodes();
			for(int j = 0; j < childs.getLength(); j++) {
				// Get current child:
				Node currentChild = childs.item(j);
				// The content of the tag and the nodes tag:
				String inner = currentChild.getTextContent();
				String tag = currentChild.getNodeName();
				
				if(tag.equals("x")) {
					x = Integer.parseInt(inner);
					
				} else if(tag.equals("y")) {
					y = Integer.parseInt(inner);
					
				} else if(tag.equals("zoom")) {
					zoom = Integer.parseInt(inner);
					
				} else if(tag.equals("style")) {
					style = inner;
					
				} else if(tag.equals("usecount")) {
					useCount = Integer.parseInt(inner);
					
				} else if(tag.equals("timestamp")) {
					timeStamp = Long.parseLong(inner);
					
				} else if(tag.equals("file")) {
					fileName = inner;
				}
			}
			
			// Get the style-object with the read tag:
			Style matchingStyle = null;
			if(style != null) {
				List<Style> styles = manager.getAvailableStyles();
				for(Iterator<Style> styleIter = styles.iterator(); styleIter.hasNext() && matchingStyle == null; ) {
					Style current = styleIter.next();
					if(current.getLabel().equals(style)) {
						matchingStyle = current;
					}
				}
			}
			
			// Check the validity of all read variables:
			boolean coordsValid = x != -1 && y != -1 && zoom != -1;
			boolean countersValid = useCount != -1 && timeStamp != -1;
			boolean fileValid = fileName != null;
			
			if(matchingStyle != null && coordsValid && countersValid && fileValid) {
				File file = new File(fileName);
				
				// Add the size of the file the current sum:
				currentSizeOnDisk += file.length();
				
				// Create a resource from the read variables:
				CachedTile entry = new CachedTile(x, y, zoom, useCount, file);
				entry.setStyle(matchingStyle);
				entry.setTimestamp(timeStamp);
				
				// Add the resource to memory index:
				return super.add(entry);
			}
		}
		
		return true;
	}
	
	private void addTileTagChilds(CachedTile tile, Document doc, Element tag) {
		
		// Temporarily store the tiles coordinates. The format is (x, y, zoom):
		IntegerVector3D coords = tile.getCoord();
		
		// Add the x-tag:
		Element xTag = doc.createElement("x");
		xTag.appendChild(doc.createTextNode(Integer.toString(coords.getX())));
		tag.appendChild(xTag);
		
		// Add the y-tag_
		Element yTag = doc.createElement("y");
		yTag.appendChild(doc.createTextNode(Integer.toString(coords.getY())));
		tag.appendChild(yTag);
		
		// Add the zoom-tag:
		Element zoomTag = doc.createElement("zoom");
		zoomTag.appendChild(doc.createTextNode(Integer.toString(coords.getZ())));
		tag.appendChild(zoomTag);
		
		// Add the style-tag:
		Element styleTag = doc.createElement("style");
		styleTag.appendChild(doc.createTextNode(tile.getStyle().getLabel()));
		tag.appendChild(styleTag);
		
		// Add the usecount-tag:
		Element useCountTag = doc.createElement("usecount");
		useCountTag.appendChild(doc.createTextNode(Integer.toString(tile.getUseCount())));
		tag.appendChild(useCountTag);
		
		// Add the timestamp-tag_
		Element timestampTag = doc.createElement("timestamp");
		timestampTag.appendChild(doc.createTextNode(Long.toString(tile.getTimestamp())));
		tag.appendChild(timestampTag);
		
		// Add the file-tag:
		Element fileTag = doc.createElement("file");
		fileTag.appendChild(doc.createTextNode(tile.getFile().getAbsolutePath()));
		tag.appendChild(fileTag);
	}
	
	/**
	 * Writes the information about all cached tiles to a index-file.
	 * For the format of the file see tilecache.xsd.
	 */
	public void writeBack() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("cache");
			doc.appendChild(rootElement);
			
			Collection<CacheEntry> entries = getEntries();
			for(CacheEntry entry : entries) {
				Element currentTileTag = doc.createElement("tile");
				rootElement.appendChild(currentTileTag);
				
				TileResource result = entry.get();
				if(result != null && result instanceof CachedTile) {
					CachedTile tile = (CachedTile) result;
					addTileTagChilds(tile, doc, currentTileTag);
				}
			}
			
			// write the content into xml file:
			String cacheIndexPath = Environment.getInstance().getString(EnvVariable.TILE_CACHE_INDEX_FILE);
			
			File cacheIndexFile = new File(cacheIndexPath);
			if(!cacheIndexFile.exists()) {
				try {
					cacheIndexFile.createNewFile();
				} catch (IOException e) {
					Logger.getInstance().error("Cannot create cache-index at " + cacheIndexPath);
					return;
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(cacheIndexPath));
	 
			transformer.transform(source, result);
			
		} catch(ParserConfigurationException | TransformerException e) {
			Logger.getInstance().error("Cannot write cache-index: " + e.getMessage());
		}
	}

	/**
	 * Deletes <code>file</code>. If <code>file</code> is a directory, all contained files are deleted
	 * recursively.
	 * @param file The file to delete. 
	 */
	private void deleteFile(File file) {
		if(file.isDirectory()) {
			// Delete all contained files recursively:
			for(File current : file.listFiles()) {
				deleteFile(current);
			}
			file.delete();
			
		} else {
			file.delete();
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		
		// Delete all files in the caches root-directory:
		Environment environment = Environment.getInstance();
		File cacheRoot = new File(environment.getString(EnvVariable.CACHE_ROOT_DIR));
		for(File current : cacheRoot.listFiles()) {
			deleteFile(current);
		}
		
		currentSizeOnDisk = 0;
		
		// Clear the memory index:
		super.clear();
	}

	/**
	 * Returns the maximum size of image-files of all styles in bytes.
	 * @return The maximum size of image-files of all styles in bytes.
	 */
	public long getMaximumSizeOnDisk() {
		return maximumSizeOnDisk;
	}

	/**
	 * Sets the maximum size of image-files of all styles in bytes.
	 * @param maximumSizeOnDisk The maximum size of image-files of all styles in bytes.
	 */
	public void setMaximumSizeOnDisk(long maximumSizeOnDisk) {
		this.maximumSizeOnDisk = maximumSizeOnDisk;
	}

	/**
	 * Returns current size of the image-files of all styles in bytes.
	 * @return The current size of the image-files of all styles in bytes.
	 */
	public long getCurrentSizeOnDisk() {
		return currentSizeOnDisk;
	}
}