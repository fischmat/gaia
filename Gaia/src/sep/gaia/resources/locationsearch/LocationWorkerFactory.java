package sep.gaia.resources.locationsearch;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.WorkerFactory;

/**
 * This class is to generate new workers necessary to initialize the 
 * request of the <code>http://nominatim.openstreetmap.org</code> api.
 * This functionality is used by the Loader instances responsible 
 * for loading locations.
 * 
 * @author Fabian Buske
 * 
 */
public class LocationWorkerFactory implements WorkerFactory<LocationQuery, Location> {

	@Override
	public AbstractLoaderWorker<LocationQuery, Location> createWorker(
			LocationQuery query, Cache<Location> cache) {
		return new LocationWorker(query);
	}

}
