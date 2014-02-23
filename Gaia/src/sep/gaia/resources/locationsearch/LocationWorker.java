package sep.gaia.resources.locationsearch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.util.Logger;

/**
 * A <code>LocationWorker</code> instance searches for locations for the given
 * geographical position. The result comes from the Nominatim API. (
 * <code>http://nominatim.openstreetmap.org</code>)
 * 
 * After this Worker has done its job, it notifies its caller by a callback
 * function, implemented in the super class <code>AbstractLoaderWorker</code>.
 * 
 * @see <a href="http://nominatim.openstreetmap.org">Nominatim API</a>
 * @see <code>AbstractLoaderWorker<Query, DataResource>
 * 
 * @author Johannes Bauer, Michael Mitterer
 * 
 */
public class LocationWorker extends
		AbstractLoaderWorker<LocationQuery, Location> {

	/**
	 * URL to the server which requests the api calls. The parameter are marked
	 * with "$q" which is replaced by the query string.
	 */
	private static final String API_REQUEST_URL = "http://nominatim.openstreetmap.org/search?q=$q&format=xml&polygon=0&addressdetails=1";

	/**
	 * Creates a new <code>LocationWorker</code> with its "task" - the passed
	 * Query instance.
	 * 
	 * @param subQuery
	 *            The query which contains the locations that are searched for.
	 */
	public LocationWorker(LocationQuery subQuery) {
		super(subQuery);
	}

	@Override
	public void run() {
		LocationQuery query = getSubQuery();

		//
		if (query == null || query.getSearch() == null
				|| query.getSearch().isEmpty()) {
			// Nothing to do.
			return;
		}

		// Now parse the results and collect them in a set.
		Set<Location> locations = new HashSet<Location>();

		URI requestURI = null;
		try {
			// Generate request URI.
			requestURI = new URI(API_REQUEST_URL.replace("$q",
					query.getSearch()));
			
			// GET request and response from Nominatim API.
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpUriRequest getRequest = new HttpGet(requestURI);
			HttpResponse response = httpClient.execute(getRequest);

			// Check for status code.
			if (response.getStatusLine().getStatusCode() != 200) {
				return;
			}

			// Create builder instance for parsing the xml answer.
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = docBuilder.parse(response.getEntity().getContent());

			// Parse the xml document.
			// Get head.
			NodeList nodes = doc.getChildNodes().item(0).getChildNodes();

			for (int i = 1; i < nodes.getLength(); i++) {
				NamedNodeMap nodePlace = nodes.item(i).getAttributes();
				String name = nodePlace.getNamedItem("display_name")
						.getTextContent();
				float lat = Float.parseFloat(nodePlace.getNamedItem("lat")
						.getTextContent());
				float lon = Float.parseFloat(nodePlace.getNamedItem("lon")
						.getTextContent());
				float[] coords = { lon, lat };
				
				String country = "";
				for (int j = 1; j < nodePlace.getLength(); j++) {
					if (nodePlace.item(i).getNodeName().toString().equals("country")) {
						country = nodePlace.item(i).getNodeValue();
					}
				}

				// Generate Location instance.
				Location location = new Location(name, country, coords);
				
				locations.add(location);
			}
			
		} catch (ParserConfigurationException e) {
			
		} catch (SAXException e) {
			Logger.getInstance().error("Error while parsing nominatim result.");
		} catch (IOException e) {
			Logger.getInstance().error(
					"Error while reading nominatim result from InputStream.");
		} catch (URISyntaxException e) {
			Logger.getInstance().error(
					"Malformed request URI for Nominatim location search: "
							+ requestURI);
		}

		this.setResults(locations);
	}

}