/**
 * 
 */
package sep.gaia.resources.tiles2d;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * <code>TileResource</code> objects gets their content by openstreetmap.org
 * tile servers. There are many different server with different styled tiles. To
 * change these styles in GAIA, the user can specify which style he wants to
 * see. Every style has its <code>Style</code> object, so the
 * <code>TileManager</code> can make a difference. Each style has on source
 * where it's been loaded. This URL or list of URLs are specified here, too.
 * 
 * @author Johannes Bauer, Matthias Fisch
 */
public class Style {

	/**
	 * Bean describing a subserver for a style.
	 * @author Matthias Fisch
	 *
	 */
	public static class SubServer {
		
		/**
		 * The hostname of the subserver.
		 */
		private String hostname;
		
		/**
		 * The maximum number of connections possible at the same time.
		 */
		private int maxConnections;

		/**
		 * Initializes the subserver.
		 * @param hostname The hostname of the subserver.
		 * @param maxConnections The maximum number of connections possible at the same time.
		 */
		public SubServer(String hostname, int maxConnections) {
			super();
			this.hostname = hostname;
			this.maxConnections = maxConnections;
		}

		/**
		 * Returns the hostname of the subserver.
		 * @return The hostname of the subserver.
		 */
		public String getHostname() {
			return hostname;
		}

		/**
		 * Sets the hostname of the subserver.
		 * @param hostname The hostname of the subserver.
		 */
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		/**
		 * Returns the maximum number of connections possible at the same time.
		 * @return The maximum number of connections possible at the same time.
		 */
		public int getMaxConnections() {
			return maxConnections;
		}

		/**
		 * Sets the maximum number of connections possible at the same time.
		 * @param maxConnections The maximum number of connections possible at the same time.
		 */
		public void setMaxConnections(int maxConnections) {
			this.maxConnections = maxConnections;
		}
	}
	
	/**
	 * Tile servers can often be requested parallel on different URLs. This list
	 * contains all the hostnames which one tile server can be contacted.
	 */
	private List<SubServer> subServers;
	
	/**
	 * The syntax to use for this style.
	 */
	private String syntax;

	/**
	 * The minimum zoom provided for this style.
	 */
	private int minZoom;
	
	/**
	 * The maximum zoom provided for this style.
	 */
	private int maxZoom;
	
	/**
	 * Human-friendly name to be able to differ the different styles.
	 */
	private String label;

	/**
	 * Constructor defines the list of subservers and the label, as well as
	 * the syntax to be used when generating URLs. The
	 * <code>label</code> is also used by the <code>TileManager</code> for
	 * making directories when caching the <code>TileResource</code> objects on
	 * HDD.
	 * 
	 * @param label The human-friendly name of the style.
	 * @param subServers  The list of hostnames on which the tile server can be contacted.
	 * @param syntax The syntax-string to be used when generating a URL.
	 * Valid parameters are:
	 * <ul>
	 * <li>$s: The name of the subserver.</li>
	 * <li>$z: The zoomlevel.</li>
	 * <li>$x: The x-coordinate of the tiles.</li>
	 * <li>$y: The y-coordinate of the tiles.</li>
	 * </ul>
	 *            
	 */
	public Style(String label, List<SubServer> subServers, String syntax) {
		this.subServers = subServers;
		this.syntax = syntax;
		this.label = label;
	}
	
	/**
	 * Constructor defines the list of subservers and the label, as well as
	 * the syntax to be used when generating URLs. The
	 * <code>label</code> is also used by the <code>TileManager</code> for
	 * making directories when caching the <code>TileResource</code> objects on
	 * HDD.
	 * 
	 * @param label The human-friendly name of the style.
	 * @param subServers  The list of hostnames on which the tile server can be contacted.
	 * @param syntax The syntax-string to be used when generating a URL.
	 * Valid parameters are:
	 * <ul>
	 * <li>$s: The name of the subserver.</li>
	 * <li>$z: The zoomlevel.</li>
	 * <li>$x: The x-coordinate of the tiles.</li>
	 * <li>$y: The y-coordinate of the tiles.</li>
	 * </ul>
	 * @param minZoom The minimum zoom provided for this style.
	 * @param maxZoom The maximum zoom provided for this style.
	 */
	public Style(String label, List<SubServer> subServers, String syntax, int minZoom, int maxZoom) {
		this.subServers = subServers;
		this.syntax = syntax;
		this.label = label;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	/**
	 * Returns a list of all available subservers of this style.
	 * 
	 * @return All available subservers of this style.
	 */
	public List<SubServer> getSubServers() {
		return subServers;
	}

	/**
	 * Returns the <code>label</code> of this <code>Style</code> object.
	 * 
	 * @return The <code>label</code> member of this <code>Style</code>.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The syntax to be used when generating URLs.
	 * For all valid parameters see the constructor.
	 * @return The syntax to be used when generating URLs.
	 */
	public String getSyntax() {
		return syntax;
	}

	/**
	 * Sets the syntax to be used when generating URLs.
	 * For all valid parameters see the constructor.
	 * @param syntax The syntax to be used when generating URLs.
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	
	/**
	 * The minimum zoom provided for this style.
	 * @return The minimum zoom provided for this style.
	 */
	public int getMinZoom() {
		return minZoom;
	}

	/**
	 * The maximum zoom provided for this style.
	 * @return The maximum zoom provided for this style.
	 */
	public int getMaxZoom() {
		return maxZoom;
	}
	
	/**
	 * Generates an URL by replacing the parameters in the syntax used for this style.
	 * This URL can be used for retrieving an image described by <code>x</code>, <code>y</code> 
	 * and <code>z</code>. These parameters are not validated.
	 * Note that this method is based on text-replacement and may result in malformed URLs
	 * if the parameters passed are not valid.
	 * @param subserver The host to use. This may may be a subserver such as <i>a.example.com</i>.
	 * @param zoom The zoomlevel to use.
	 * @param x The x-coorinate.
	 * @param y The y-coordinate.
	 * @return Returns the URL generated by inserting the passed arguments.
	 * @throws MalformedURLException Thrown if any argument <code>x</code>, <code>y</code> 
	 * or <code>z</code> are malformed such as they falsify the URL inserted at their
	 * respective position.
	 */
	public URL generateUrl(String subserver, int zoom, int x, int y) throws MalformedURLException {
		// Replace parameters "$_" in syntax step by step:
		String url = new String(syntax); // Replacement not inplace
		url = url.replace("$s", subserver);
		url = url.replace("$z", Integer.toString(zoom));
		url = url.replace("$x", Integer.toString(x));
		url = url.replace("$y", Integer.toString(y));
		
		return new URL(url);
	}
	
	/**
	 * Returns the suffix of the files specified in the syntax of the style.
	 * @return The suffix of the image-files used by this style. If no suffix can be read,
	 * an empty string is returned.
	 */
	public String getImageSuffix() {
		int dotPosition = syntax.lastIndexOf(".");
		// If a dot is contained and its not the last character:
		if(dotPosition != -1 && dotPosition < syntax.length() - 1) {
			return syntax.substring(dotPosition + 1); 
		} else {
			return "";
		}
	}
	
	@Override
	public String toString() {
		return this.label;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Style) {
			Style compare = (Style) obj;
			
			// Check if syntax matches:
			boolean syntaxMatch = syntax.equals(compare.getSyntax());
			// Check if minimum and maximum zoomlevels match:
			boolean zoomLimitMatch = minZoom == compare.getMinZoom() && maxZoom == compare.getMaxZoom();
			// Check if label matches:
			boolean labelMatch = label.equals(compare.getLabel());
			
			// Check for each subserver of compare if the same subserver is contained in subServers:
			boolean subServersMatch = true;
			for(SubServer current : compare.getSubServers()) {
				subServersMatch &= subServers.contains(current);
			}
			
			return syntaxMatch && zoomLimitMatch && labelMatch && subServersMatch;
			
		} else {
			return false;
		}
	}
	
	
}