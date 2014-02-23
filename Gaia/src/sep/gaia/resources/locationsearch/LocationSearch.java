package sep.gaia.resources.locationsearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;

/** 
 * This class takes all requests of locations and transmits them to 
 * the <code>LocationWorker</code>. After having received the response
 * from the <code>LocationWorker</code> <code>this</code> sends the data 
 * to the <code>LocationWindow</code>.
 * 
 * @author Max Witzelsperger, Michael Mitterer
 * 
 * @see <a> href= "http://nominatim.openstreetmap.org/search" </a>
 */
public class LocationSearch implements LoaderEventListener<Location> {
	
	/**
	 * The set of <code>Location</code> matching to the <code>userSearchString</code>.
	 */
	private Set<Location> searchResults;
	/**
	 * The <code>Loader</code> which accepts a <code>Query</code> for <code>Location</code>,
	 * loads them and returns them.
	 */
	private Loader<LocationQuery, Location> loader;
	
	/**
	 * Constructor as an interface between the <code>LocationWorker</code>
	 * and the <code>LocationWindow</code>. 
	 * It sends the <code>searchResults</code> receiving from the 
	 * <code>LocationWorker</code> to the <code>LocationWindow</code>.
	 * 
	 * @param searchResults The set of locations corresponding to name of 
	 * the location sent to the <code>LocationWorker</code>.
	 */
	public LocationSearch() {
		loader = new Loader<LocationQuery, Location>(null, new LocationWorkerFactory(), null);
		this.searchResults = new HashSet<Location>();
	}
	
	/**
	 * This method returns the set of locations received by the 
	 * <code>LocationWorker</code>.
	 * 
	 * @return The set of locations received by the <code>LocationWorker</code>.
	 */
	public Set<Location> getLocationResults() {
		return this.searchResults;
	}

	/** 
	 * This method is to take the <code>userSearchString</code> and transmit it to
	 * the <code>LocationWorker</code>.
	 * 
	 * @param userSearchString The location to be searched for.
	 */
	public void queryforLocations(String userSearchString) {
		loader.clearQueryQueue();
		this.searchResults.clear();
		Location resource = new Location(null, null, null);
		resource.setDummy(true);
		LocationQuery query = new LocationQuery(resource, userSearchString);
		loader.request(query);
		if (!loader.isAlive()) {
			loader.start();
			loader.addListener(this);
		}
	}

	@Override
	public void onResourcesAvailable(Collection<Location> resources) {
		this.searchResults.addAll(resources);
	}

	public Loader<LocationQuery, Location> getLoader() {
		return loader;
	}
}