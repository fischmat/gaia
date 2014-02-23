package sep.gaia.resources.markeroption;

import java.util.Date;

import sep.gaia.resources.poi.PointOfInterest;

/**
 * This class is to implement a marker of a point of interest which stores
 * the current zoom level which the map adopts and the geographical
 * coordinates of the point. It inherits from <code>PointOfInterest</code>.
 * In addition each marker gets an individual name.
 * As soon as the marker is chosen, the map switches to the place
 * (that means the geographical position) and the
 * zoom level the marker has been stored at.
 *  
 * @author Fabian Buske
 *
 */
public class MarkerResource extends PointOfInterest {
	
	/**
	 * The serial Version ID of <code>this</code>.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The zoom level of the map at which <code>this</code> has been stored
	 */
	private float zoom;
	/**
	 * The longitude of the map at which <code>this</code> has been stored at.
	 */
	private float lon;
	/**
	 * The latitude of the map at which <code>this</code> has been stored at.
	 */
	private float lat;
	/**
	 * The time stamp of <code>this</code>
	 */
	private long timestamp = new Date().getTime() / 1000;
	/**
	 * The name of <code>this</code>.
	 */
	private String name;
	
	/**
	 * Constructor to generate a new <code>MarkerResource</code>.
	 * 
	 * @param name The name of  <code>this</code>.
	 * 
	 * @param lon The longitude of the position <code>this</code> points to.
	 * 
	 * @param lat The latitude of the position <code>this</code> points to.
	 * 
	 * @param zoom The zoom level of the map at which <code>this</code>
	 *  has been stored.
	 */
	public MarkerResource(String name, float lon, float lat, float zoom) {
		
		super(name, lon, lat);
		this.name = name;
		this.zoom = zoom;
		this.lon = lon;
		this.lat = lat;
		this.timestamp += (long) (1/1000);
	}
	
	/**
	 * Constructor to generate a new <code>MarkerResource</code>.
	 * 
	 * @param name The name of  <code>this</code>.
	 * 
	 * @param lon The longitude of the position <code>this</code> points to.
	 * 
	 * @param lat The latitude of the position <code>this</code> points to.
	 * 
	 * @param zoom The zoom level of the map at which <code>this</code>
	 *  has been stored.
	 *  
	 *  @param time The time at which <code>this</code> has been stored.
	 */
	public MarkerResource(String name, float lon, float lat, float zoom, long time) {
		
		super(name, lon, lat);
		this.name = name;
		this.zoom = zoom;
		this.lon = lon;
		this.lat = lat;
		this.timestamp = time;
	}
	
	/**
	 * This method gets the zoom level which <code>this</code> has been 
	 * stored at.
	 * 
	 * @return The zoom level of <code>this</code>.
	 */
	public float getZoom() {
		return this.zoom;
	}

	/**
	 * This method sets the zoom level at which <code>this</code> is to 
	 * be stored.
	 * 
	 * @param zoom The zoom level at which <code>this</code> is to be stored.
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	/**
	 * This method gets the geographical longitude of <code>this</code>.
	 * 
	 * @return The geographical longitude of <code>this</code>.
	 */
	public float getLon() {
		return this.lon;
	}

	/**
	 * This method sets the geographical longitude of the position at which
	 * <code>this</code> is to be  stored.
	 * 
	 * @param lon The geographical longitude of the position at which 
	 * <code>this</code> is to be stored. 
	 */
	public void setLon(float lon) {
		this.lon = lon;
	}

	/**
	 * This method returns the geographical latitude of <code>this</code>.
	 * 
	 * @return The geographical latitude of <code>this</code>.
	 */
	public float getLat() {
		return this.lat;
	}

	/**
	 * This method sets the geographical latitude of the position at which
	 * <code>this</code> has been stored.
	 * 
	 * @param lat The geographical latitude of the position at which
	 * <code>this</code> has been stored.
	 */
	public void setLat(float lat) {
		this.lat = lat;
	}

	/**
	 * This method returns the name of <code>this</code>.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method sets the name of <code>this</code>.
	 * 
	 * @param name The name <code>this</code> is to be set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	
	/**
	 * This is to rename <code>this</code>.
	 * 
	 * @param name The new name of <code>this</code>.
	 */
	public void rename(String name) {
		this.name = name;
	}

	/**
	 * This method is to update the time stamp at which <code>this</code>
	 * has been stored.
	 */
	public long updateTimestamp() {
		this.timestamp += (long) (1 / 1000);
		return this.timestamp;
	}
}