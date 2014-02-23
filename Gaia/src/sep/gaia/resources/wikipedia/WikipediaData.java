package sep.gaia.resources.wikipedia;

import java.util.Date;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.poi.PointOfInterest;

/**
 * Concrete class <code>WikipediaData</code> inherits from
 * <code>DataResource</code>. It contains everything that is necessary to create
 * a wikipedia short description dialog.
 * 
 * A <code>WikipediaData</code> dummy contains only the title of the related
 * <code>PointOfInterest</code>. The specific wikipedia short description can
 * be set by the <code>Loader<Query, WikipediaData></code> class. The
 * <code>valid</code> flag inheritated by the parent <code>DataResource</code>
 * class indicates, whether the <code>WikipediaData</code> contains the short
 * description.
 * 
 * The wikipedia information is taken of <code>mediawiki.org</code> api.
 * 
 * @author Michael Mitterer
 */
public class WikipediaData extends PointOfInterest {
	
	/**
	 * Constructs a new <code>WikipediaData</code> object.
	 * 
	 * @param url the url of the wiki
	 * @param name the name of <code>this</code>
	 * @param longitude the longitude of the position of <code>this</code>
	 * @param latitude the latitude of the position of <code>this</code>
	 */
	public WikipediaData(String url, String name, float longitude, float latitude) {
		super(name, longitude, latitude);
		this.setUrl(url);
	}

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** 
	 * The short description of the related <code>PointOfInterest</code>
	 * retrieved from Wikipedia
	 */
	private String summaryText;
	
	/**
	 * The url of the related <code>PointOfInterest</code>
	 */
	private String url;
	
	/**
	 * The time stamp of <code>this</code>
	 */
	private long timestamp = new Date().getTime() / 1000;

	private int usecount;
	
	/**
	 * The constructor creates a dummy object. Dummy object means, this
	 * <code>WikipediaData</code> has the title of the related
	 * <code>PointOfInterest</code>, but contains no short description yet.
	 * 
	 * @param title The title of the related <code>PointOfInterest</code>
	 */
	/*public WikipediaData (float longitude, float latitude) {
		this.longitude = longitude;
		this.timestamp += (long) (1/1000);
		this.usecount = 0;
	}*/
		
	/**
	 * Gives back the title of the related <code>PointOfInterest</code>
	 * 
	 * @return The title of the related <code>PointOfInterest</code>
	 */
	/*public String getPoiTitle() {
		return this.poiTitle;
	}*/

	/** 
	 * (Kein Javadoc)
	 * @return 
	 * @see DataResource#incrementTimestamp()
	 */
	public long incrementTimestamp() {
		this.timestamp += (long) (1/1000);
		return this.timestamp;
	}
	
	/**
	 * This is to increment the usecount of this element.
	 */
	public void incrementUsecount() {
		this.usecount++;
	}
	
	/**
	 * Gives back the usecount of this element.
	 * 
	 * @return The usecount
	 */
	public int getUsecount() {
		return this.usecount;
	}
	
	/**
	 * Set the short description of the related <code>PointOfInterest</code>
	 * 
	 * @param summary The short description retrieved from Wikipedia
	 */
	public void setSummaryText(String summary) {
		this.summaryText = summary;
	}
	
	/**
	 * Gives back the short description of the related
	 * <code>PointOfInterest</code>
	 * 
	 * @return The short description retrieved from Wikipedia
	 */
	public String getSummaryText() {
		return this.summaryText;
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString () {
		return url;
	}
}