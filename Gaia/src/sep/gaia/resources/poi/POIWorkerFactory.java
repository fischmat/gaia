package sep.gaia.resources.poi;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.WorkerFactory;

/**
 * Class for generating new workers. This functionality is used by the Loader-instance
 * responsible for loading POIs.
 * @author Matthias Fisch
 *
 */
public class POIWorkerFactory implements WorkerFactory<POIQuery, PointOfInterest> {

	/**
	 * Returns a worker for processing <code>query</code>.
	 * @return A worker for processing <code>query</code>.
	 */
	@Override
	public AbstractLoaderWorker<POIQuery, PointOfInterest> createWorker(
			POIQuery query, Cache<PointOfInterest> cache) {
		return new POILoaderWorker(query, cache);
	}

}
