package sep.gaia.resources.weather;

import java.awt.Image;

import sep.gaia.resources.DataResource;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.TextureData;

/**
 * Concrete class <code>WeatherResource</code> inherits from
 * <code>DataResource</code>. It contains everything that is necessary to create
 * a graphical weather status in the view with an icon that represents the
 * current weather (e.g. a shining sun symbol for a warm and nice weather).
 * 
 * A <code>WeatherResource</code> dummy contains only its geographical position
 * with longitude and latitude coordinates. The specific weather data, such as
 * temperature, weather code can be set by the
 * <code>Loader<Query, WeatherResource></code> class. The <code>valid</code>
 * flag inheritated by the parent <code>DataResource</code> class indicates,
 * whether the <code>WeatherResource</code> contains all weather information in
 * it.
 * 
 * The weather information is taken of <code>openweathermap.org</code> api.
 * Theoretically its api-independent instead of the
 * <code>weatherConditionCode</code> member, which is a
 * <code>openweathermap.org</code> specific value.
 * 
 * Each <code>WeatherResource</code> represents one geographical location in the
 * world. As float variables for its location makes it hard to compare two such
 * locations because of rounding issues, each <code>WeatherResource</code>
 * object is identified by its associated tile number at constant zoom level
 * (key).
 * 
 * @see <a href=
 *      "http://bugs.openweathermap.org/projects/api/wiki/Weather_Data">WeatherData
 *      </a>
 * 
 * @author Johannes Bauer
 */
public class WeatherResource extends DataResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 701077077353252427L;

	/**
	 * The zoom level at which the <code>WeatherResource</code> are associated
	 * to tile numbers.
	 */
	private static final int WEATHER_ZOOM = 14;

	private static long clock = 0;
	
	/**
	 * Latitude coordinate which this weather information refers to. It is fix
	 * when the object is created.
	 */
	private final float lat;

	/**
	 * Longitude coordinate which this weather information refers to. It is fix
	 * when the object is created.
	 */
	private final float lon;

	/**
	 * Current temperature at the place, where this <code>WeatherResource</code>
	 * refers to in degree Celsius. Other measures are not supported.
	 */
	private float temperatur;

	/**
	 * Name of the place where this <code>WeatherResource</code> object refers
	 * to.
	 */
	private String name = "";

	private String key = "";

	/**
	 * The <code>weatherConditionCode</code> is a
	 * <code>openweathermap.org</code> specific code, that indicates the current
	 * weather status in a qualitative way (e.g. "moderate rain").
	 * 
	 * Each weather condition code refers also to an icon.
	 * 
	 * @see <a
	 *      href="http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes">Weather
	 *      Condition Codes</a>
	 */
	private int weatherConditionCode;

	/**
	 * Depends on the <code>weatherConditionCode</code> variable. The icon is
	 * drawn in the view later on.
	 * 
	 * @see int weatherConditionCode
	 */
	private Image icon;

	/**
	 * The final "image" that represents one <code>WeatherResource</code>.
	 * <code>TextureData</code> object in order to easy paintable by OpenGL.
	 */
	private TextureData textureData;

	/**
	 * The constructor creates a dummy object. Dummy object means, this
	 * <code>WeatherResource</code> has specific geographical coordinates, but
	 * contains not any weather information yet.
	 * 
	 * @param lat
	 *            The geographical, latitude coordinate.
	 * @param lon
	 *            The geographical, longitude coordinate.
	 */
	public WeatherResource(float lat, float lon, int tileZoom) {
		// Every DataResource is a dummy at its birth.
		this.setDummy(true);
		this.lat = lat;
		this.lon = lon;
		this.key = (AlgoUtil
				.geoToTile(new FloatVector3D(lat, lon, tileZoom)))
				.toString();
		clock++;
	}

	/**
	 * Get the geographical, latitude coordinate of this
	 * <code>WeatherResource</code>.
	 * 
	 * @return The latitude coordinate.
	 */
	public float getLat() {
		return lat;
	}

	/**
	 * Get the geographical, longitude coordinate of this
	 * <code>WeatherResource</code>.
	 * 
	 * @return The longitude coordinate.
	 */
	public float getLon() {
		return lon;
	}

	/**
	 * Returns the Image icon which refers to the current weather status. The
	 * weather status is defined by the Weather Condition Code.
	 * 
	 * @return The icon of this <code>WeatherResource</code>.
	 * @see <code>weatherConditionCode</code>
	 */
	public Image getIcon() {
		return icon;
	}

	/**
	 * Sets the Image icon.
	 * 
	 * @param icon
	 *            The icon that indicates the Weather Condition. It is
	 *            referenced from the Weather Condition Code.
	 */
	protected void setIcon(Image icon) {
		this.icon = icon;
	}

	/**
	 * Returns the current temperature in degree Celsius for the place, where
	 * this <code>WeatherResource</code> points at.
	 * 
	 * @return The current temperature in degree Celsius for the places
	 *         specified by longitude and latitude coordinates.
	 */
	public float getTemperature() {
		return Math.round(this.temperatur * 100) / 100;
	}

	/**
	 * Sets the current temperature in degree Celsius for the place, where this
	 * <code>WeatherResource</code> points at.
	 * 
	 * @param temp
	 *            The temperature in degree Celsius.
	 */
	protected void setTemperature(float temp) {
		temperatur = temp;
	}

	/**
	 * The coordinates are to specify the exact position of the object. However
	 * a name can be set to have a human-friendly identifier. This name is also
	 * shown in the View.
	 * 
	 * @return The Name of the location (e.g. city) of this
	 *         <code>WeatherResource</code> object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The coordinates are to specify the exact position of the object. However
	 * a name can be set to have a human-friendly identifier. This name is also
	 * shown in the View.
	 * 
	 * @param name
	 *            The Name of the location (e.g. city) of this
	 *            <code>WeatherResource</code> object.
	 */
	protected void setName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	/**
	 * The Weather Condition Code specifies the current "state" of the weather.
	 * E.g. "801" stands for "few clouds". Each Weather Condition Code has a
	 * separate graphical icon, which can be referred by the Weather Condition
	 * Code.
	 * 
	 * @return The Weather Condition Code.
	 * @see <a
	 *      href="http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes">Weather
	 *      Condition Code</a>.
	 */
	public int getWeatherConditionCode() {
		return weatherConditionCode;
	}

	/**
	 * The Weather Condition Code specifies the current "state" of the weather.
	 * E.g. "801" stands for "few clouds". Each Weather Condition Code has a
	 * separate graphical icon, which can be referred by the Weather Condition
	 * Code.
	 * 
	 * @param code
	 *            The id of the Weather Condition (Weather Condition Code) as an
	 *            integer.
	 * @see <code>weatherConditionCode</code>
	 */
	protected void setWeatherConditionCode(int code) {
		this.weatherConditionCode = code;
	}

	/**
	 * Create a drawable "image" of this <code>WeatherResource</code> object.
	 * This "image" contains all information needed and available, such as
	 * temperature, weather-icon and the name of the location.
	 * 
	 * @see <code>TextureData</code>
	 * @return A <code>TextureData</code> object which can be bound and drawn
	 *         immediately by OpenGL.
	 */
	public TextureData getTexture() {

		// Check if there already exists a TextureData instance.
		if (textureData == null && !this.isDummy()) {
			// Create texture
			/* TODO */
		}

		// Return the TextureData or null, if this WeatherResource is a dummy
		// yet.
		return textureData;
	}
	
	public void setTexture(TextureData texData) {
		this.textureData = texData;
	}

	@Override
	protected long incrementTimestamp() {
		this.setTimestamp(clock);
		clock++;
		return this.getTimestamp();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "Weather resource {\n Name: " + name + "\n Temperature: "
				+ temperatur + "\n Weather Code: " + weatherConditionCode
				+ "\n}";
	}
}
