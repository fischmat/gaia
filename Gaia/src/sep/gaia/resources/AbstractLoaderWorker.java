package sep.gaia.resources;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A thread processing a part of a query in order to take e.g. advantage of
 * higher bandwidth-usage. After a worker was created, a task has been assigned
 * and it was started, it will perform its query and finish afterwards.
 * @author Matthias Fisch
 *
 */
public abstract class AbstractLoaderWorker<Q extends Query, R extends DataResource> extends Thread {

	/**
	 * The part of the query this worker is supposed to process.
	 */
	private Q subQuery;

	/**
	 * The resources generated from the queries result.
	 */
	private Collection<R> results = new LinkedList<>();
	
	/**
	 * The cache this worker should use
	 */
	private Cache<R> cache;
	
	/**
	 * Flag if this worker was already evaluated by its loader.
	 */
	private boolean evaluated;

	/**
	 * Initializes the worker.
	 * @param subQuery The part of the query this worker is supposed to process.
	 */
	public AbstractLoaderWorker(Q subQuery) {
		super();
		this.subQuery = subQuery;
	}
	
	/**
	 * Initializes the worker.
	 * @param subQuery The part of the query this worker is supposed to process.
	 * @param cache The cache this worker should use or <code>null</code> if no cache
	 * should be used.
	 */
	public AbstractLoaderWorker(Q subQuery, Cache<R> cache) {
		super();
		this.subQuery = subQuery;
		this.cache = cache;
	}

	/**
	 * Performs the partial query and stores the result in <code>result</code>.
	 * After the result is stored this thread terminates.
	 */
	public abstract void run();
	
	
	/**
	 * Returns the part of the query this worker is supposed to process.
	 * @return The part of the query this worker is supposed to process.
	 */
	public Q getSubQuery() {
		return subQuery;
	}

	/**
	 * Returns if this worker was already evaluated by a loader.
	 * @return <code>true</code> if the worker was evaluated. Otherwise <code>false</code>
	 * is returned.
	 */
	public boolean isEvaluated() {
		return evaluated;
	}

	/**
	 * Sets the result of the query.
	 * @param results Collection of all resources resulted from the query.
	 */
	protected void setResults(Collection<R> results) {
		this.results = results;
	}

	/**
	 * Returns the cache this worker should use.
	 * @return The cache this worker should use or <code>null</code> if no cache
	 * should be used.
	 */
	protected Cache<R> getCache() {
		return cache;
	}

	/**
	 * Returns the resources generated from the queries result and marks the worker as evaluated.
	 * @return The resources generated from the queries result or <code>null</code>
	 * if no result could be generated.
	 */
	public Collection<R> getResults() {
		if(!isAlive()) {
			evaluated = true;
		}
		return results;
	}
}
