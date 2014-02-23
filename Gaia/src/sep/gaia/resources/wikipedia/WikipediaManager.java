
package sep.gaia.resources.wikipedia;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.DataResourceManager;
import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;
import sep.gaia.resources.Query;

/** 
 * Management object to load wikipedia short descriptions from wikipedia.
 * 
 * Instances of the class observe the current <code>GLState</code> and the
 * instance of <code>POIManager</code> and react to a change of the state.
 * This is important, because the <code>PointOfInterest</code> objects for
 * which wikipedia articles exist are needed.
 * @author Michael Mitterer
 */
public class WikipediaManager extends DataResourceManager<WikipediaData> implements
		LoaderEventListener<WikipediaData> {
	
	private static final float RADIUS_SCALE = 1.5f;
	
	public final String label  = "Wikipedia";
	
	private WikipediaCache cache;
	
	/**
	 * The constructor creates a new wikipedia loader and a new temporary cache.
	 * All loaded <code>WikipediaData</code> objects are hold in this cache
	 * and are deleted, if it's full.
	 */
	public WikipediaManager() {
		super("Wikipedia");
		cache = new WikipediaCache();
		loader = new Loader<Query, WikipediaData>(cache, new WikipediaWorkerFactory(), null);
		currentWikis = new HashSet<WikipediaData>();
		loader.addListener(this);
		loader.start();
	}
	
	/** 
	 * The current <code>WikipediaData</code> objects, specified by the 
	 * position (<code>FloatBoundingBox</code>) of the specific
	 * <code>PointOfInterest</code>.
	 */
	private Set<WikipediaData> currentWikis;
	
	/** 
	 * The <code>Loader<Query, WikipediaData></code> which splits
	 * <code>Query</code> objects and delegates each query to one
	 * <code>WikipediaLoaderWorker</code> thread.
	 * The threads are executed parallel to optimize the efficiency of the
	 * process.
	 */
	private Loader<Query, WikipediaData> loader;
	
	/**
	 * Initiates a load procedure at the overgiven dummy object. A
	 * <code>WikipediaLoaderWorker</code> thread is created, which will load the
	 * data into the <code>WikipediaData</code> object and then call the
	 * <code>EventLoaderListener</code> when it has finished.
	 * 
	 * @see <code>WikipediaLoaderWorker</code>
	 * @param dummy
	 *            The <code>WikipediaData</code> dummy that has to be filled
	 *            with the short description.
	 * @return The <code>WikipediaData</code> after it has been loaded with
	 *         the short description. It's not a dummy anymore, it's
	 *         <code>valid</code> flag is set true.
	 */
	public void load(WikipediaData dummy) {
		dummy.setDummy(true);
		//currentWikis.add(dummy);
		Collection<DataResource> col = new HashSet<DataResource>();
		col.add(dummy);
		//cache.add(dummy);
		Query query = new Query(col);
		loader.request(query);
		
	}
	
	/**
	 * Returns the currently used <code>Set<WikipediaData></code> object.
	 * 
	 * @return <code>Set<WikipediaData></code> object, specified by the 
	 * position (<code>FloatBoundingBox</code>) of the specific
	 * <code>PointOfInterest</code>.
	 */
	public Set<WikipediaData> getCurrentWikipediaDatas() {
		Set<WikipediaData> temp = new HashSet<WikipediaData>();
		temp.addAll(currentWikis);
		return temp;
	}

	/** 
	 * Gives back the title of each relating short description
	 * 
	 * @param title The name of a <code>PointOfInterest</code> object
	 * @return The short description retrieved from Wikipedia
	 */
	public String getWikipediaByTitle(String title) {
		for (WikipediaData wiki : this.currentWikis) {
			if (wiki.getName() == title) {
				return wiki.getSummaryText();
			}
		}
		return null;
	}

	/** 
	 * Gives back the short description of the relating coordinates
	 * 
	 * @param coords The coordinates of a <code>PointOfInterest</code> object
	 * @return The short description retrieved from Wikipedia
	 */
	public void loadWikipediaByCoords(FloatBoundingBox box, FloatVector3D position) {
		
		currentWikis.clear();
		
		loader.clearQueryQueue();

		FloatVector3D diagonal = new FloatVector3D(box.getLowerRight());
		diagonal.sub(box.getUpperLeft());
		
		FloatVector3D left = AlgoUtil.glToGeo(new FloatVector3D(0, 0, box.getUpperLeft().getZ()));
		FloatVector3D right = AlgoUtil.glToGeo(new FloatVector3D(diagonal.length(), 0, box.getUpperLeft().getZ()));
		float radius = AlgoUtil.calculateGeoDistance(left, right)*1000*RADIUS_SCALE;
		
		URL url =  null;
		
		String requestUrl = "http://api.wikilocation.org/articles?lat=" + position.getX() + "&lng=" + position.getY() + "&radius=" + radius + "&format=xml&locale=de";
		
		try {
			url = new URL(requestUrl);
		} catch (MalformedURLException e) {
			Logger.getInstance().error("Cannot reach the WikiLocation server with the syntax: " + requestUrl);
		}
		
		if(url != null) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(url.openStream());
				
				NodeList temp = doc.getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
				
				// Build Wikipedia objects and add them to the currentWikis
				for (int counter = 1; counter < temp.getLength(); counter++) {						
					NodeList list = temp.item(counter).getChildNodes();
					
					float lat = Float.parseFloat(list.item(1)
							.getTextContent());
					float lon = Float.parseFloat(list.item(2)
							.getTextContent());
					
					FloatVector3D vector = AlgoUtil.geoToGL(new FloatVector3D(lat, lon, position.getZ()));
					
					WikipediaData wiki = new WikipediaData(list.item(0)
							.getTextContent(), list.item(4)
							.getTextContent(), vector.getX(), vector.getY());
					
					String urlString = wiki.getKey();
					if (cache.get(urlString) == null) {
						load(wiki);
					} else {
						WikipediaData cacheData = cache.get(wiki.getKey());
						currentWikis.add(cacheData);
						notifyAll(currentWikis);
					}
				}
				
			} catch (IOException e) {
				Logger.getInstance().warning("Unable to load results from " + url.toString());
			} catch (ParserConfigurationException e) {
				
			} catch (SAXException e) {
				
			}
		}
	}

	@Override
	public void onUpdate(State state) {
		
		// Check if notification comes from the GL-coordinate-view of state:
		if(state != null && state instanceof GLState) {			
			
			final GLState glState = (GLState) state;
			
			Runnable loadRoutine = new Runnable() {
				
				@Override
				public void run() {
					loadWikipediaByCoords(glState.getBoundingBox(), AlgoUtil.glToGeo(glState.getPosition()));
				}
			};
			
			if(isEnabled() && AlgoUtil.glToTileZoom(glState.getZoom()) >= 11) {
				new Thread(loadRoutine).start();
			} else {
				clearAll();
			}
		}
	}

	@Override
	public void onResourcesAvailable(Collection<WikipediaData> resources) {
		if (resources != null) {
			currentWikis.addAll(resources);
			notifyAll(currentWikis);
		}
		
	}

	@Override
	public void requestLoaderStop() {
		if(loader != null) { // If loader exists:
			loader.interrupt(); // set the interrupt-flag to request termination.
		}
	}
}