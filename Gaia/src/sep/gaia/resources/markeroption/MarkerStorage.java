package sep.gaia.resources.markeroption;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.poi.POISet;
import sep.gaia.resources.poi.PointOfInterest;
import sep.gaia.util.Logger;

/**
 * This class is to store <code>MarkerResource</code> into and load
 * <code>MarkerResource</code> out of a specific file which in the methods of
 * this class will be referred to as POI-File. It is called by the
 * <code>MarkerResourceManager</code> when new markers are to be stored or
 * existing markers are to be loaded.
 * 
 * @author Michael Mitterer, Fabian Buske
 * 
 */
public class MarkerStorage {
	/**
	 * The MarkerResourceManager which requests the loading and storing process.
	 */
	private MarkerResourceManager markerResourceManager;

	/**
	 * Constructor to manage the storing and loading process of the POI-File
	 * 
	 * @param markerResourceManager
	 *            The reference to the MarkerResourceManager which requests the
	 *            loading and storing process.
	 */
	public MarkerStorage(MarkerResourceManager markerResourceManager) {
		this.markerResourceManager = markerResourceManager;
	}

	/**
	 * This method generates a set of <code>MarkerResource</code> objects which
	 * is saved in the POI-File.
	 * 
	 * @return The set of <code>MarkerResource</code> saved in the POI-File.
	 */
	public static POISet loadXML() {

		POISet set = new POISet();
		try {
			// Get the applications environment:
			Environment environment = Environment.getInstance();
			
			String schemaPath = environment.getString(EnvVariable.MARKER_INDEX_FILE_SCHEMA);
			String indexPath = environment.getString(EnvVariable.MARKER_INDEX_FILE);
			
			try {	
				File schemaFile = new File(schemaPath);
				Source source = new StreamSource(new File(indexPath));
				
				// Check that the marker xml file fulfills our xsd schema (file)
				Schema schema = SchemaFactory.newInstance(
						XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
				
				javax.xml.validation.Validator validator = schema.newValidator();
				validator.validate(source);
				
				
				// Load the current xml file
				File file = new File(
						"./config/markerFile.xml");
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(file);
	
				NodeList temp = doc.getChildNodes().item(0).getChildNodes();
	
				// Build MarkerResource objects which are equal to the marker
				// elements from the file and add them to the new POISet
				for (int counter = 0; counter < temp.getLength(); counter++) {
					NodeList list = temp.item(counter).getChildNodes();
					MarkerResource marker = new MarkerResource(list.item(0)
							.getTextContent(), Float.parseFloat(list.item(1)
							.getTextContent()), Float.parseFloat(list.item(2)
							.getTextContent()), Float.parseFloat(list.item(3)
							.getTextContent()), Long.parseLong(list.item(4)
							.getTextContent()));
					set.add(marker);
				}
				
			} catch(FileNotFoundException e) {
				Logger.getInstance().error("The index-file for markers or its XSD was not found. " 
											+ e.getMessage());
				return set;
			} catch(SAXException e) {
				Logger.getInstance().error("XML-schema of marker-index invalid. "
											+ "Detailed description: " + e.getMessage());
				return set;
			}
		} catch (Exception e) {
			Logger.getInstance().message(
					"The markers couldn't be loaded out of the xml file.");
		}

		return set;
	}

	/**
	 * This method stores a set of <code>MarkerResource</code> into the
	 * POI-File.
	 * 
	 * @param set
	 *            The set of points of interest to be stored.
	 * 
	 * @return <code>true</code> if and only if the attempted storage was
	 *         successful.
	 */
	public static boolean storeXML(POISet set) {
		try {
			// The new empty document
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// The marker list
			Element markerList = doc.createElement("marker_list");
			doc.appendChild(markerList);

			// The elements of the marker list
			for (PointOfInterest currentMarker : set) {
				MarkerResource mark = (MarkerResource) currentMarker;
				Element marker = doc.createElement("marker");
				markerList.appendChild(marker);

				// The elements of a marker are set here by loading the values
				// from the MarkerResource objects
				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode(mark.getName()));
				marker.appendChild(name);
				Element lon = doc.createElement("long");
				lon.appendChild(doc.createTextNode(Float.toString(mark
						.getLongitude())));
				marker.appendChild(lon);
				Element lat = doc.createElement("lat");
				lat.appendChild(doc.createTextNode(Float.toString(mark
						.getLatitude())));
				marker.appendChild(lat);
				Element zoom = doc.createElement("zoom");
				zoom.appendChild(doc.createTextNode(Float.toString(mark
						.getZoom())));
				marker.appendChild(zoom);
				Element time = doc.createElement("time");
				marker.appendChild(time);
				time.appendChild(doc.createTextNode(Long.toString(mark
						.getTimestamp())));
				marker.appendChild(time);
			}

			// Save the given markers in a new file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult res = new StreamResult(new File(
					"./config/markerFile.xml"));
			transformer.transform(source, res);
			return true;
		} catch (ParserConfigurationException pce) {
			return false;
		} catch (TransformerException tfe) {
			return false;
		}
	}
}