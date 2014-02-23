package sep.gaia.resources.poi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

/**
 * A class for asyncroniously performing a part of request for POIs.
 * Instances are created and managed by <code>POILoader</code>
 * @author Matthias Fisch (specification), Matthias Fisch (implementation)
 *
 */
public class POILoaderWorker extends AbstractLoaderWorker<POIQuery, PointOfInterest> {
	
	private static final String INTERPRETER_URI = "http://overpass-api.de/api/interpreter";
	
	/**
	 * The cache to check before loading POIs.
	 */
	private POICache cache;
	
	/**
	 * Initializes the worker by specifying the part of a bigger query, this
	 * instance has to process.
	 * @param subQuery The part of a query this worker has to process.
	 * @param cache The cache to check before loading POIs.
	 */
	public POILoaderWorker(POIQuery subQuery, Cache<PointOfInterest> cache) {
		super(subQuery);
		if(cache instanceof POICache) {
			this.cache = (POICache) cache;
		}
	}

	/**
	 * Performs the partial query and stores the result in <code>result</code>.
	 * A XML-document is generated from the key-value-pairs in the sub-query for
	 * both nodes and ways and the specific bounding-box. This document is sent to 
	 * the Overpass-API via HTTP-POST and the resulting XML-document is sent back.
	 * If no error is reported, the result is a XML-document listing all nodes and
	 * ways suitable for the request. POIs are generated from the nodes and by
	 * picking a node from the ways returned.
	 * After the result is stored this thread terminates.
	 * <br><br>
	 * For information about the Overpass-API in general confer 
	 * <a href="http://wiki.openstreetmap.org/wiki/Overpass_API">this document</a> and 
	 * for information about the language to be used read
	 * <a href="http://wiki.openstreetmap.org/wiki/Overpass_API/Language_Guide">this guide</a>.
	 */
	@Override
	public void run() {
		
		POIQuery query = getSubQuery();
		if(query != null) {
			
			// Check the cache if there is data already present:
			if(cache != null) {
				Collection<PointOfInterest> pois = cache.get(query);
				
				// If there was something found, set it as the workers result and quit:
				if(pois != null) {
					setResults(pois);
					return;
				}
			}
			
			try {
				HttpClientBuilder clientBuilder = HttpClientBuilder.create();
				HttpClient httpclient = clientBuilder.build();
			    HttpPost httppost = new HttpPost(INTERPRETER_URI);

			    // Add the API-request as POST-data:
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		        String xmlQuery = generateQueryXML(query);
		        nameValuePairs.add(new BasicNameValuePair("data", xmlQuery));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Do the request:
		        HttpResponse response = httpclient.execute(httppost);
		        
		        // Prepare XML:
		        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    	Document responseDoc = dBuilder.parse(response.getEntity().getContent());
		    	
		    	// Parse the response:
		        Collection<PointOfInterest> pois = parseResponse(responseDoc);
		        
		        // Set the name of the POIs category:
		        for(PointOfInterest poi : pois) {
		        	poi.setCategory(query.getCategoryName());
		        }
		        
		        // If a cache exists, add the loaded resources to it:
		        if(cache != null) {
		        	cache.addResources(query, pois);
		        }
		        
		        // Set the read POIs as the workers result:
		        setResults(pois);
		        
			} catch(IOException e) {
				Logger.getInstance().warning("Overpass-Query failed.");
			} catch (ParserConfigurationException e) {
				Logger.getInstance().warning("Configuring XML-parser failed! " + e.getMessage());
			} catch (IllegalStateException | SAXException e) {
				Logger.getInstance().warning("Error parsing XML! " + e.getMessage());
			}
			
		}
	}

	/**
	 * Generates a Overpass API-request in XML-format. The request complies to the limitations and the
	 * bounding box in <code>query</code> and is designed to retrieve both nodes and recursed ways.
	 * @param query The query to generate XML for.
	 * @return The generated XML-query.
	 */
	private static String generateQueryXML(POIQuery query) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			
			// The root of a OSM-query is always osm-script:
			Element script = doc.createElement("osm-script");
			doc.appendChild(script);
			
			// First element is the union containing the queries:
			Element unionElement = doc.createElement("union");
			script.appendChild(unionElement);
			
			// Second element says that the recused union of the prior results should be formed:
			Element recurseUnion = doc.createElement("union");
			Element itemElement = doc.createElement("item");
			recurseUnion.appendChild(itemElement);
			Element recurseElement = doc.createElement("recurse");
			recurseElement.setAttribute("type", "down");
			recurseUnion.appendChild(recurseElement);
			
			script.appendChild(recurseUnion);
			
			// The last element means, that the results (of the recursed union)
			// should be written as response:
			Element printElement = doc.createElement("print");
			script.appendChild(printElement);
			
			// First query (in the query union) askes for nodes conforming the given attributes:
			Element queryNodeElement = doc.createElement("query");
			queryNodeElement.setAttribute("type", "node");
			
			// The second element does the same for ways:
			Element queryWayElement = doc.createElement("query");
			queryWayElement.setAttribute("type", "way");
			
			// Add them to the first union:
			unionElement.appendChild(queryNodeElement);
			unionElement.appendChild(queryWayElement);
			
			// Now iterate all key-value-pairs and add "has-kv"-pairs to both queries:
			POIFilter filter = query.getLimitations();
			Map<String, String> attributes = filter.getLimitations();
			
			for(String key : attributes.keySet()) {
				String value = attributes.get(key);
				
				// The values returned by POIFilter are regular expressions, so use regv instead of v:
				Element currentKVNode = doc.createElement("has-kv");
				currentKVNode.setAttribute("k", key);
				currentKVNode.setAttribute("regv", value);
				queryNodeElement.appendChild(currentKVNode);
				
				Element currentKVWay = doc.createElement("has-kv");
				currentKVWay.setAttribute("k", key);
				currentKVWay.setAttribute("regv", value);
				queryWayElement.appendChild(currentKVWay);
				
			}
			
			// We don't want the data of the whole earth, so add bounding-boxes to the queries:
			Element nodeBBoxElement = createBBoxElement(doc, query.getBoundingBox());
			queryNodeElement.appendChild(nodeBBoxElement);
			
			Element wayBBoxElement = createBBoxElement(doc, query.getBoundingBox());
			queryWayElement.appendChild(wayBBoxElement);
			
			
			// Now the XML-tree is built, so transform it to a string and return it:
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(stream);
	 
			transformer.transform(source, result);
			
			return stream.toString();
			
		} catch(ParserConfigurationException | TransformerException e) {
			Logger.getInstance().error("Cannot write cache-index: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns an element to limit a query to a specific geographical area.
	 * @param doc The document to create the element in.
	 * @param bbox The boundaries of the queries area.
	 * @return The bounding-box-element ready to be added to a query.
	 */
	private static Element createBBoxElement(Document doc, FloatBoundingBox bbox) {
		// The Overpass-API does use sides instead of corners, so convert them:
		float east = bbox.getUpperRight().getY();
		float west = bbox.getLowerRight().getY();
		float north = bbox.getUpperLeft().getX();
		float south = bbox.getLowerRight().getX();
		
		// Create the element and add the attributes describing the sides of the bbox:
		Element element = doc.createElement("bbox-query");
		element.setAttribute("e", Float.toString(Math.max(east, west)));
		element.setAttribute("w", Float.toString(Math.min(east, west)));
		element.setAttribute("n", Float.toString(Math.max(north, south)));
		element.setAttribute("s", Float.toString(Math.min(north, south)));
		return element;
	}
	
	/**
	 * Parses XML-data from the Overpass-API containing both nodes and ways and creates 
	 * <code>PointOfInterest</code>-objects from it. When a way is contained, one of its nodes
	 * is picked as the describing POIs location.
	 * @param doc The document to parse.
	 * @return The POIs described by the Overpass-data.
	 */
	private static Collection<PointOfInterest> parseResponse(Document doc) {
		
		// First all ways must be parsed, because later the contained nodes must be known:	
		Collection<Way> ways = new LinkedList<>();
		NodeList wayElements = doc.getElementsByTagName("way");
		
		// Iterate all way-elements:
		for(int i = 0; i < wayElements.getLength(); i++) {
			Node wayElement = wayElements.item(i);
			
			Set<String> nodeReferences = new HashSet<>();
			Map<String, String> tags = new HashMap<>();
			
			// Iterate all child-elements of the way-element:
			NodeList childs = wayElement.getChildNodes();
			for(int j = 0; j < childs.getLength(); j++) {
				Node currentChild = childs.item(j);
				
				// If its a node reference
				if(currentChild.getNodeName().equals("nd")) {
					// Add its ID to the ways node references:
					NamedNodeMap attributes = currentChild.getAttributes();
					String ref = attributes.getNamedItem("ref").getNodeValue();
					nodeReferences.add(ref);
					
				// If its an attribute tag-element:
				} else if(currentChild.getNodeName().equals("tag")) {
					// Add the k/v-attributes to the ways attributes:
					NamedNodeMap attributes = currentChild.getAttributes();
					String key = attributes.getNamedItem("k").getNodeValue();
					String value = attributes.getNamedItem("v").getNodeValue();
					tags.put(key, value);
				}
			}
			
			// Remember the way for later use:
			Way way = new Way(nodeReferences, tags);
			ways.add(way);
		}
		
		
		// Now all node-elements are parsed:
		NodeList nodeElements = doc.getElementsByTagName("node");
		Collection<PointOfInterest> pois = new ArrayList<>(nodeElements.getLength());
		
		for (int i = 0; i < nodeElements.getLength(); i++) {
			Node node = nodeElements.item(i);
			
			// Get the nodes ID and position:
			NamedNodeMap attributes = node.getAttributes();
			String id = attributes.getNamedItem("id").getNodeValue();
			float lat = Float.parseFloat(attributes.getNamedItem("lat").getNodeValue());
			float lon = Float.parseFloat(attributes.getNamedItem("lon").getNodeValue());
			
			// If the node is part of a way, add its position to the ways position-set:
			Way containedIn = null;
			Iterator<Way> wayIter = ways.iterator();
			while(wayIter.hasNext() && containedIn == null) {
				Way current = wayIter.next();
				if(current.containsNode(id)) {
					current.addNode(new FloatVector3D(lat, lon, 0));
					containedIn = current;
				}
			}
			
			// If this node is not a part of a way:
			if(containedIn == null) {
				Map<String, String> tags = new HashMap<>();
				
				// Iterate all children of the node:
				NodeList childs = node.getChildNodes();
				for(int j = 0; j < childs.getLength(); j++) {
					Node currentChild = childs.item(j);
					NamedNodeMap childAttrs = currentChild.getAttributes();
					
					// Add attribute for each k/v-element:
					if(currentChild.getNodeName().equals("tag")) {
						String key = childAttrs.getNamedItem("k").getNodeValue();
						String value = childAttrs.getNamedItem("v").getNodeValue();
						tags.put(key, value);
					}
				}
				
				// Valid POIs must have a name:
				String name = tags.get("name");
				if(name != null) {
					// Create the POI from read data and add it to results:
					PointOfInterest poi = createPoiFromTags(lat, lon, tags);
					if(poi != null) {
						pois.add(poi);
					}
				}
			}
		}
		
		// The last thing to do is to convert all generated ways to POIs:
		for(Way way : ways) {
			PointOfInterest poi = wayToPoi(way);
			if(poi != null) {
				pois.add(poi);
			}
		}
		
		return pois;
	}
	
	/**
	 * Returns a POI described by the position and attributes.
	 * @param lat The latitude of the POI.
	 * @param lon The longitude of the POI.
	 * @param tags The attributes as k/v-pairs.
	 * @return The generated POI or <code>null</code> if the passed parameters are invalid.
	 */
	private static PointOfInterest createPoiFromTags(float lat, float lon, Map<String, String> tags) {
		String name = tags.get("name");
		if(name != null) {
			PointOfInterest poi = new PointOfInterest(name, lat, lon);
			poi.setAttributes(tags);
			return poi;
		}
		return null;
	}
	
	/**
	 * Generates a POI from a way by adopting its attributes and picking one of the positions it consists of
	 * as the POIs position.
	 * @param way The way to convert.
	 * @return The POI generated.
	 */
	private static PointOfInterest wayToPoi(Way way) {
		Map<String, String> tags = way.getAttributes();
		String name = tags.get("name");
		
		Collection<FloatVector3D> nodePositions = way.getNodePositions();
		
		if(name != null && nodePositions.size() > 0) {
			
			int index = nodePositions.size()/2;
			FloatVector3D position = (FloatVector3D)nodePositions.toArray()[index];
			
			PointOfInterest poi = new PointOfInterest(name, position.getX(), position.getY());
			poi.setAttributes(tags);
			
			return poi;
		}
		return null;
	}
}
