package sep.gaia.environment;

/**
 * A singleton holding the environment-properties of the current runtime.
 * @author Matthias Fisch
 *
 */
public class Environment {

	/**
	 * Enumerates all possible identifiers.
	 * @author Matthias Fisch
	 *
	 */
	public enum EnvVariable {LOG_FILE, // The path to the log-file. 
							 POI_CATEGORY_FILE, // The path to the POI-category-file.
							 POI_CATEGORY_FILE_SCHEMA, // The path to the schema for the pois category-file
							 KEY_SYMBOL_MAP_FILE, // The path to the file holding symbol-mapping
							 TILE_STYLE_FILE, // The path to the file holding all available styles
							 TILE_STYLE_FILE_SCHEMA, // The path to the schema for tile-style-xmls
							 MARKER_TEXTURE_FILE, // The path to the marker-symbol
							 TEXTURE_3D_INDEX, // Index-file listing the textures available for 3D-mode
							 TEXTURE_3D_INDEX_SCHEMA, // The path to the schema for the 3d-texture-index
							 MARKER_INDEX_FILE, // The path to the file holding all markers
							 MARKER_INDEX_FILE_SCHEMA, // The path to the schema for the markers index-file
							 TILE_CACHE_INDEX_FILE, // The path the the schema for the persistent caches index-file
							 TILE_CACHE_INDEX_FILE_SCHEMA, // The path to the schema for the persistent caches index-file
							 SCREENSHOT_FOLDER, // The path to the location of saved screenshots.
							 CACHE_ROOT_DIR, // The path to the caches root-directory
							 WEATHER_FONT, // Path to the weather font which is used by writing weather information the screen.
							 WEATHER_ICONS_DAY, // Weather icons for the day.
							 WEATHER_ICONS_NIGHT, // Weather icons for the night.
							 WIKIPEDIA_TEXTURE_FILE, // The path to the wikipedia symbol
							 POI_TEXTURE_DIR, // The path to the directory where image-files for POI-textures are stored
							 IMAGES,
							 ICONS,
							 COMPASS_TEXTURE_FILE,
							 SCREENSHOT_BLEND_TEXTURE,
							 CREDITS_TEXTURE_FILE,
							 OSM_COPYRIGHT_TEXTURE_FILE,
							 VERSION_STRING,
							 SETTINGS_FILE_PATH
							 };
	
	/**
	 * The one instance of this class existing.
	 */
	private static Environment instance;
	
	/**
	 * Default constructor hided because only a single instance
	 * should be existent.
	 */
	private Environment() {
		
	}
	
	/**
	 * Returns the one instance of the class.
	 * @return The one instance of the class.
	 */
	public static Environment getInstance() {
		if(instance != null) {
			return instance;
		} else {
			instance = new Environment();
			return instance;
		}
	}
	
	
	/**
	 * Returns the value of a environment-variable.
	 * @param var The Identifier of the variable to be retrieved.
	 * @return The value of the variable with identifier <code>var</code> or an empty
	 * string if the variable is not set.
	 */
	public String getString(EnvVariable var) {
		switch(var) {
		case LOG_FILE: return "log.txt";
		case POI_CATEGORY_FILE: return "config" + System.getProperty("file.separator") + "categories.xml";
		case POI_CATEGORY_FILE_SCHEMA: return "config" + System.getProperty("file.separator") + "categories.xsd";
		case KEY_SYMBOL_MAP_FILE: return "symbols" + System.getProperty("file.separator") + "symbolmap.xml";
		case TILE_STYLE_FILE: return "config" + System.getProperty("file.separator") + "tilestyles.xml";
		case TILE_STYLE_FILE_SCHEMA: return "config" + System.getProperty("file.separator") + "tilestyles.xsd";
		case MARKER_TEXTURE_FILE: return "res" + System.getProperty("file.separator") + "marked.png";
		case TEXTURE_3D_INDEX: return "config" + System.getProperty("file.separator") + "textures3d.xml";
		case TEXTURE_3D_INDEX_SCHEMA: return "config" + System.getProperty("file.separator") + "textures3d.xsd";
		case MARKER_INDEX_FILE: return "config" + System.getProperty("file.separator") + "markerFile.xml";
		case MARKER_INDEX_FILE_SCHEMA: return "config" + System.getProperty("file.separator") + "marker.xsd";
		case TILE_CACHE_INDEX_FILE: return "config" + System.getProperty("file.separator") + "tilecache.xml";
		case TILE_CACHE_INDEX_FILE_SCHEMA: return "config" + System.getProperty("file.separator") + "tilecache.xsd";
		case SCREENSHOT_FOLDER: return "screenshots";
		case CACHE_ROOT_DIR: return "cache";
		case WEATHER_FONT: return "res" + System.getProperty("file.separator") + "fonts" + System.getProperty("file.separator") + "weather_font.ttf";
		case WEATHER_ICONS_DAY: return "res" + System.getProperty("file.separator") + "weather_icons" + System.getProperty("file.separator") + "day";
		case WEATHER_ICONS_NIGHT: return "res" + System.getProperty("file.separator") + "weather_icons" + System.getProperty("file.separator") + "night";
		case WIKIPEDIA_TEXTURE_FILE: return "res" + System.getProperty("file.separator") + "wikipedia.png";
		case POI_TEXTURE_DIR: return "res" + System.getProperty("file.separator") + "poi" + System.getProperty("file.separator");
		case IMAGES: return "res" + System.getProperty("file.separator") + "images";
		case ICONS: return "res" + System.getProperty("file.separator") + "icons";
		case COMPASS_TEXTURE_FILE: return "res" + System.getProperty("file.separator") + "compass.png";
		case SCREENSHOT_BLEND_TEXTURE: return "res" + System.getProperty("file.separator") + "scrsht_blend.png";
		case CREDITS_TEXTURE_FILE: return "res" + System.getProperty("file.separator") + "credits.png";
		case OSM_COPYRIGHT_TEXTURE_FILE: return "res" + System.getProperty("file.separator") + "osmcpy.png";
		case VERSION_STRING: return "Version 1.0.0.0 Stable";
		case SETTINGS_FILE_PATH: return "config" + System.getProperty("file.separator") + "settings.xml";
		default: return "";
		}
	}
}