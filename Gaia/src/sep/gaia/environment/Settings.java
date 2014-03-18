package sep.gaia.environment;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.Cache.CacheEntry;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.resources.tiles2d.TileCache.CachedTile;
import sep.gaia.util.Logger;

public class Settings {

	private static Settings instance;
	
	private String settingsFilePath;
	
	private Map<String, Object> entries = new HashMap<>();
	
	private Settings() {
		Environment env = Environment.getInstance();
		settingsFilePath = env.getString(EnvVariable.SETTINGS_FILE_PATH);
	}
	
	/**
	 * Checks if a certain property is set.
	 * @param key The key of the property to be checked.
	 * @return <code>true</code> if the property is existent.
	 */
	public boolean isSet(String key) {
		return entries.containsKey(key);
	}
	
	public int getInt(String key) throws ClassCastException  {
		return (Integer) entries.get(key);
	}
	
	public String getString(String key) throws ClassCastException  {
		return (String) entries.get(key);
	}
	
	public boolean getBoolean(String key) throws ClassCastException  {
		return (Boolean) entries.get(key);
	}
	
	public Date getDate(String key) throws ClassCastException  {
		return (Date) entries.get(key);
	}
	
	public void setValue(String key, Object value) throws IllegalArgumentException {
		if(value instanceof Integer || value instanceof String 
				|| value instanceof Boolean || value instanceof Date) {
			
			entries.put(key, value);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public boolean commit() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("settings");
			doc.appendChild(rootElement);
	
			
			for(String key : entries.keySet()) {
				Object value = entries.get(key);
				
				Element currentTileTag = doc.createElement("entry");
				rootElement.appendChild(currentTileTag);
				
				Element keyTag = doc.createElement("k");
				keyTag.appendChild(doc.createTextNode(key));
				
				Element valueTag = doc.createElement("v");
				valueTag.appendChild(doc.createTextNode(value.toString()));
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
	
	private String getValueType(Object o) {
		if(o instanceof Integer) {
			return "int";
		} else if(o instanceof String) {
			return "string";
		} else if(o instanceof Boolean) {
			return "bool";
		} else if(o instanceof Date) {
			return "date";
		} else {
			return "unknown";
		}
	}
}
