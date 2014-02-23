package sep.gaia.resources.poi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sep.gaia.resources.DataResource;
import sep.gaia.util.FloatVector3D;

/**
 * Represents a geographical point with certain attributes that might be of
 * interest for a user. Points of Interest (POI) can be ordered in categories and
 * subcategories. The attributes are stored as mapped key-value pairs in textual representation.
 * @author Matthias Fisch (Implementation Fabian Buske, Max Witzelsperger)
 *
 */
public class PointOfInterest extends DataResource {

	/**
	 * The default serial version id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the POI in its local language.
	 */
	private String name;
	
	/**
	 * The coordinates of the POI as a vector of the following form: (long, lat, 0).
	 * After the POI was processed by the <code>POIAdapter</code> the components of the vector
	 * may vary.
	 */
	private FloatVector3D coordinates;
	
	/**
	 * The attributes of the POI as key-value-pairs from
	 * OpenStreetMap. The keys are mapped to their respective
	 * values. The value of the name-key is not mapped but
	 * stored in the <code>name</code>-attribute.
	 */
	private Map<String, String> attributes = new HashMap<>();
	
	/**
	 * The name of the category this POI is in.
	 */
	public String category;
	
	/**
	 * The time stamp of <code>this</code>
	 */
	private long timestamp = new Date().getTime() / 1000;
	
	
	/**
	 * Initializes the POI with its name and position but without any attributes set.
	 * @param name The name of the POI.
	 * @param longitude The longitude-value of the position of the POI.
	 * @param latitude The latitude-value of the position of the POI.
	 */
	public PointOfInterest(String name, float longitude, float latitude) {
		super();
		this.name = name;
		this.coordinates = new FloatVector3D(longitude, latitude, 0);
		this.timestamp += (long) (1 / 1000);
	}

	
	/**
	 * Initializes the POI with its name, position and a collection of attributes.
	 * @param name The name of the POI.
	 * @param longitude The longitude-value of the position of the POI.
	 * @param latitude The latitude-value of the position of the POI.
	 * @param attributes A collection of attributes for this POI. Contains OSM-conform
	 * keys mapped to their respective values.
	 */
	public PointOfInterest(String name, float longitude, float latitude,
			Map<String, String> attributes) {
		super();
		this.name = name;
		this.coordinates = new FloatVector3D(longitude, latitude, 0);
		this.attributes = attributes;
		this.timestamp += (long) (1 / 1000);
	}
	
	

	/**
	 * Returns the name of the POI in its local language.
	 * @return The name of the POI in its local language.
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Sets the name of the POI in its local language.
	 * @param name The name of the POI in its local language.
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Returns the longitude-value of the position of the POI.
	 * @return The longitude-value of the position of the POI.
	 */
	public float getLongitude() {
		return this.coordinates.getX();
	}


	/**
	 * Sets the longitude-value of the position of the POI.
	 * @param longitude The longitude-value of the position of the POI.
	 */
	public void setLongitude(float longitude) {
		this.coordinates.setX(longitude); // TODO why setter ?
	}


	/**
	 * Returns the latitude-value of the position of the POI.
	 * @return The latitude-value of the position of the POI.
	 */
	public float getLatitude() {
		return this.coordinates.getY();
	}


	/**
	 * Sets the latitude-value of the position of the POI.
	 * @param latitude The latitude-value of the position of the POI.
	 */
	public void setLatitude(float latitude) {
		this.coordinates.setY(latitude); // TODO why setter ?
	}


	/**
	 * Returns all attributes as keys mapped to their respective value.
	 * @return Attributes as keys mapped to their respective value.
	 */
	public Map<String, String> getAttributes() {
		return this.attributes;
	}


	/**
	 * Sets the attributes as keys mapped to their respective value.
	 * @param attributes The attributes as keys mapped to their respective value.
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Returns the value of the attribute with key <code>key</code>.
	 * @param key The key the value to be returned is mapped to.
	 * @return The value <code>key</code> is mapped to or <code>null</code>
	 * if no attribute with the specified key exists for this POI.
	 */
	public String getValue(String key) {
		if (this.attributes.containsKey(key)) {
			return attributes.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns if an attribute with a key <code>key</code> is existent for this POI. 
	 * @param key The key of the attribute to check for.
	 * @return <code>true</code> if the attribute exists for this POI. 
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean hasAttrbute(String key) {
		if (this.attributes.containsKey(key)) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	protected long incrementTimestamp() {
		this.timestamp += (long) (1/1000);
		return this.timestamp;
	}

	/**
	 * Returns the coordinates of this POI as a 3-dimensional vector.
	 * @return The coordinates of this POI as a 3-dimensional vector.
	 */
	public FloatVector3D getCoordinates() {
		return this.coordinates;
	}

	/**
	 * Returns the name of the category this POI is in.
	 * @return The name of the category this POI is in.
	 */
	public String getCategoryKey() {
		return this.category;
	}

	/**
	 * Sets the name of the category this POI is in.
	 * @param category The name of the category this POI is in.
	 */
	public void setCategory(String category) {
		this.category = category;
	}


	@Override
	public long getTimestamp() {
		return this.timestamp;
	}


	@Override
	public String getKey() {
		return this.name;
	}


	@Override
	public String toString() {
		return name;
	}
	
	
}
 