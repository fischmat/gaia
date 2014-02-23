package sep.gaia.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An abstract class for accepting queries for resources, asynchronously loading and returning them.
 * Queries can be added to a prioritized queue and generated resources fetched from a second queue.
 * The processing cycle can be stopped by calling <code>interrupt()</code>. May delay until the currently
 * processed query is done.
 * @author Matthias Fisch
 *
 * @param <Q> The type of the query-objects used.
 * @param <R> The type of the resources to be generated.
 */
public final class Loader<Q extends Query, R extends DataResource> extends Thread {

	/**
	 * Amount of milliseconds to wait until next check, after <code>queryQueue</code> was evaluated
	 * empty.
	 */
	private static final int DELAY_WAIT_FOR_QUERY = 25;

	/**
	 * Amount of milliseconds to wait between checks for workers having finished.
	 */
	private static final int DELAY_WAIT_FOR_WORKER = 10;

	/**
	 * The queue used for communication from a concrete manager to its concrete loader.
	 */
	private DuplicateFreeQueue<Q> queryQueue = new DuplicateFreeQueue<>();

	/**
	 * A queue containing the loaded resources to be communicated outside.
	 */
	private DuplicateFreeQueue<R> responseQueue = new DuplicateFreeQueue<>();

	/**
	 * The cache preferably used for resource-access.
	 */
	private Cache<R> cache;

	/**
	 * A list of workers processing a query.
	 */
	private List<AbstractLoaderWorker<Q, R>> activeWorkers = new LinkedList<>();

	/**
	 * Lock for blocking asynchronous operations when <code>activeWorkers</code>
	 * is write-accessed.
	 */
	private Lock activeWorkersLock = new ReentrantLock();
	
	/**
	 * The factory used to create new workers.
	 */
	private WorkerFactory<Q, R> workerFactory;
	
	/**
	 * An object for splitting queries into smaller parts for asynchronous processing.
	 * If no splitting is required this member must be <code>null</code>.
	 */
	private QuerySplitter<Q> querySplitter;

	/**
	 * List of instances listening for loaded data. Are notified if new data is available.
	 */
	private List<LoaderEventListener<R>> listeners = new LinkedList<>();


	/**
	 * Initializes the loader with a cache preferably used to obtain resources.
	 * @param cache The cache to be checked before loading data or <code>null</code> if no
	 * cache should be used.
	 * @param workerFactory Object for generating new workers in order to process specific queries.
	 * @param splitter An object for splitting queries into smaller parts for asynchronous processing.
	 * If no splitting is required this member must be <code>null</code>.
	 */
	public Loader(Cache<R> cache, WorkerFactory<Q, R> workerFactory, QuerySplitter<Q> splitter) {
		super();
		this.cache = cache;
		this.workerFactory = workerFactory;
		this.querySplitter = splitter;
	}

	/**
	 * Creates the workers, starts all of them and waits for them to finish. Once a worker
	 * has finished all listeners must be notified about the presence of new data.
	 * If the interrupt-flag is set by calling <code>interrupt()</code> all workers will
	 * be interrupted too.
	 */
	@Override
	public void run() {

		// A worker-factory is required for generating new workers:
		if(workerFactory == null) {
			return;
		}

		// As long the thread is not interrupted from outside:
		while(!isInterrupted()) {

			Q nextQuery = queryQueue.pop(); // Retrieve the next query to handle
			// Check if there are queries to process:
			if(nextQuery != null) {
				/*
				 * When a worker terminates, it only has the resources resulting from
				 * the splitted query in its result-collection stored. But the convention 
				 * is that listeners receive the whole collection of resources with those
				 * not loaded yet marked as dummy. So the collection of dummies of the
				 * query must be stored:
				 */
				Collection<DataResource> dummies = nextQuery.getResources();

				// Create sub-queries to be processed separatly:
				Collection<Q> subQueries;
				if(querySplitter != null) {
					subQueries = querySplitter.splitQuery(nextQuery);
					
				} else {
					// "Fake" a returned collection with only the one query in it:
					subQueries = new LinkedList<Q>();
					// Add another subquery.
					subQueries.add(nextQuery);
				}

				Iterator<Q> subQueryIter = subQueries.iterator();
				while(subQueryIter.hasNext()) {
					// Get next sub-query:
					Q nextSubQuery = subQueryIter.next();
					// Create a new worker for processing the sub-query:
					AbstractLoaderWorker<Q, R> worker = workerFactory.createWorker(nextSubQuery, cache);
					activeWorkers.add(worker); // Add worker to the set of active workers

					// Finally start the worker:
					worker.start();
				}
				
				// Check for workers having finished as long as there are active workers:
				while(!activeWorkers.isEmpty() && !isInterrupted()) {
					
					// Remember workers evaluated:
					List<AbstractLoaderWorker<Q, R>> evaluated = new ArrayList<>(activeWorkers.size());
					
					// Request the lock on the activeWorkers-collection:
					activeWorkersLock.lock();
					
					// Check each particular worker:
					ListIterator<AbstractLoaderWorker<Q, R>> iter = activeWorkers.listIterator();
					while(iter.hasNext()) {
						
						// Get next worker:
						AbstractLoaderWorker<Q, R> currentWorker = iter.next();
						
						// If the worker has finished now:
						if(!currentWorker.isAlive() && !currentWorker.isEvaluated()) {
							
							// Add to list of evaluated workers:
							evaluated.add(currentWorker);

							// Get the results the worker has generated:
							Collection<R> results = currentWorker.getResults();
							if(results != null) {
								// Add the results to cache:
								if(cache != null) {
									for(R result : results) {
										cache.add(result);
									}
								}
								
								updateCollection(dummies, results);
								
								// Now add all those resource from the initial query not yet contained:
								for(DataResource resource : dummies) {
									if(!results.contains(resource)) {
										results.add((R)resource);
									}
								}
								
								// Notify all listeners about the availability of new data:
								notifyAllAsnyc(results);
							}
						}

						try {
							// Remove entries from cache if necessary:
							if(cache != null) {
								cache.manage();
							}
							
							// Wait certain amount of time until next check:
							sleep(DELAY_WAIT_FOR_WORKER);
							
						} catch(InterruptedException e) {
							interrupt(); // Reset interruption-flag
						}
					}
					
					// Remove those workers evaluated:
					activeWorkers.removeAll(evaluated);
					
					// Allow other threads to access the activeWorkers-collection:
					activeWorkersLock.unlock();
				}
				
			} else {
				// If there are no queries to process, go to sleep:
				try {
					sleep(DELAY_WAIT_FOR_QUERY);
				} catch (InterruptedException e) {
					// Reset interruption-flag:
					interrupt();
				}
			}
		}
	}
	
	/**
	 * Replaces all entries in <code>originals</code> equal to a entry in 
	 * <code>updates</code> by the latter. If no correspondent entry is present in
	 * <code>originals</code>, the entry in <code>updates</code> will simply be added.
	 * @param originals The collection to update.
	 * @param updates The updates to insert.
	 */
	private void updateCollection(Collection<DataResource> originals, Collection<R> updates) {
		Collection<DataResource> toRemove = new LinkedList<>();
		
		Iterator<R> updateIter = updates.iterator();
		while(updateIter.hasNext()) {
			R currentUpdate = updateIter.next();
			
			boolean found = false;
			Iterator<DataResource> originalIter = originals.iterator();
			while(originalIter.hasNext() && !found) {
				DataResource currentOriginal = originalIter.next();
				if(currentUpdate.equals(currentOriginal)) {
					toRemove.add(currentOriginal);
					found = true;
				}
			}
		}
		originals.remove(toRemove);
		originals.addAll(updates);
	}

	/**
	 * Calls the <code>onResourcesAvaliable()</code>-event of all listeners in a new thread.
	 * The method is designed to operate asynchronously in order to not block the loaders
	 * managing-loop (see <code>run()</code>).
	 */
	protected void notifyAllAsnyc(final Collection<R> resources) {
		// Iterate all listeners:
		ListIterator<LoaderEventListener<R>> iter = listeners.listIterator();
		while(iter.hasNext()) {
			// Get next listener:
			final LoaderEventListener<R> currentListener = iter.next();

			// Define a routine for notifying a listening objects about the availability of new data:
			Runnable notifyRoutine = new Runnable() {
				@Override
				public void run() {
					currentListener.onResourcesAvailable(resources); // Fire the event
				}
			};

			// Create a thread running the notification-routine:
			Thread notificationThread = new Thread(notifyRoutine);
			notificationThread.start(); // Start the thread
		}
	}

	/**
	 * Adds <code>query</code> to the queue of queries to be processed.
	 * The object will be inserted with default priority.
	 * @param query The query to be added to the queue.
	 */
	public void request(Q query) {
		// Add to the incoming queue with default priority:
		queryQueue.push(query);
	}

	/**
	 * Adds <code>query</code> to the queue of queries to be processed.
	 * @param query The query to be added to the queue.
	 * @param priority The priority of the query.
	 */
	public void request(Q query, int priority) {
		// Add to the incoming queue with specified priority:
		queryQueue.push(query, priority);
	}

	/**
	 * Removes all entries from the query-queue and stops all workers processing
	 * a query.
	 */
	public void clearQueryQueue() {
		queryQueue.clear(); // Remove all queries contained in the incoming queue
	}

	/**
	 * Adds an listener to be notified about newly loaded data.
	 * @param The listener to add.
	 */
	public void addListener(LoaderEventListener<R> listener) {
		// Add to the list of objects to notify:
		listeners.add(listener);
	}

	/**
	 * Removes an listener to be notified about newly loaded data.
	 * @param The listener to remove.
	 */
	public void removeListener(LoaderEventListener<R> listener) {
		// Remove from the list of objects to notify:
		listeners.remove(listener);
	}
	
	/**
	 * Requests all active workers to stop.
	 * The actual stopping of each worker may be delayed.
	 * Note that this method will block until asynchronous operations
	 * of the loader are finished.
	 */
	public void requestWorkersStop() {
		// Prevent that the loader-thread removes entries while iterating the workers:
		
		// TODO: Maybe the lock must be set?
		//activeWorkersLock.lock();
		
		for(int i = 0; i < activeWorkers.size(); i++) {
			AbstractLoaderWorker<Q, R> worker = activeWorkers.get(i);
			worker.interrupt();
		}
		
		// Free the collection of active workers for access by the loader-thread:
		//activeWorkersLock.unlock();
	}

	/**
	 * Requests the loader-thread and all its active workers to stop.
	 * The actual stopping of both the loader and its workers may be delayed.
	 */
	@Override
	public void interrupt() {
		// Stop all workers before stopping the loader-thread:
		requestWorkersStop();
		
		// Now stop the loader-thread:
		super.interrupt();
	}

	public Cache<R> getCache() {
		return cache;
	}
}
