package sep.gaia.resources.wikipedia;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.Query;
import sep.gaia.resources.WorkerFactory;

/**
 * This class generates <code>WikipediaLoaderWorker</code> objects.
 * 
 * @see <code>WikipediaLoaderWorker</code>
 * @author Michael Mitterer
 */
public class WikipediaWorkerFactory implements WorkerFactory<Query, WikipediaData> {

	@Override
	public AbstractLoaderWorker<Query, WikipediaData> createWorker(Query query,
			Cache<WikipediaData> cache) {
		return new WikipediaLoaderWorker(query, cache);
	}

}
