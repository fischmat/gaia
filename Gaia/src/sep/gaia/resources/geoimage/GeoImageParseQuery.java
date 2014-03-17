package sep.gaia.resources.geoimage;

import java.util.LinkedList;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.Query;

/**
 * A query for image-location-parsing. Specifies a root-directory from where to scan recursively from.
 * @author Matthias Fisch
 *
 */
public class GeoImageParseQuery extends Query {

	/**
	 * The root-directory for the scan.
	 */
	private String parseRoot;
	
	
	/**
	 * @param parseRoot A root-directory from where to scan recursively from.
	 */
	public GeoImageParseQuery(String parseRoot) {
		super(new LinkedList<DataResource>());
		this.parseRoot = parseRoot;
	}

	/**
	 * @return A root-directory from where to scan recursively from.
	 */
	public String getParseRoot() {
		return parseRoot;
	}

	/**
	 * @param parseRoot A root-directory from where to scan recursively from.
	 */
	public void setParseRoot(String parseRoot) {
		this.parseRoot = parseRoot;
	}
}
