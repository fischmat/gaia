package sep.gaia.resources;

/**
 * Class for generating new workers. This functionality is used by <code>AbstractLoader</code>
 * when a query was split and will be processed. Implement this interface when defining own
 * loaders and corresponding workers.
 * @author Matthias Fisch
 *
 */
public interface WorkerFactory<Q extends Query, R extends DataResource> {

	/**
	 * Creates a new worker-object prepared to process <code>query</code>.
	 * @param query The query the created worker is supposed to process.
	 * @param cache The cache the created worker should use.
	 * @return The created worker.
	 */
	public AbstractLoaderWorker<Q, R> createWorker(Q query, Cache<R> cache);
}
