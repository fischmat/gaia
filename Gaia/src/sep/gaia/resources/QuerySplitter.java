package sep.gaia.resources;

import java.util.Collection;

/**
 * Interface accepted by <code>Loader</code> for splitting a query into pieces in
 * order to process them asynchronously.
 * @author Matthias Fisch
 *
 * @param <Q> The type of queries to be split.
 */
public interface QuerySplitter<Q> {

	/**
	 * Splits the <code>query</code> into smaller parts that can be processed in separate threads.
	 * Used by <code>Loader</code> when splitting a bigger query into parts and delegating processing
	 * to workers.
	 * @return Collection of query-packages forming the <code>query</code> in union.
	 */
	public Collection<Q> splitQuery(Q query);
}
