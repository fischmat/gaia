package sep.gaia.resources;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class for keeping resources in memory and accepting requests for them. Each
 * instance has a limit of resources it will maximally manage. If the count of
 * resources exceeds this value, resources are picked to by removed, according
 * to a specific removal-strategy.
 * 
 * @param <Q>
 *            The type of the queries the cached resources should be associated
 *            to.
 * 
 * @author Johannes Bauer (Spezifikation: Matthias Fisch)
 * 
 */
public abstract class Cache<R extends DataResource> {

	/**
	 * Percent of the lowest evaluating entries to be removed from the cache if
	 * it has reached its maximum number of entries.
	 */
	protected static final int REMOVE_LOWEST_PERC = 10;

	/**
	 * The resources currently kept in the cache associated with their
	 * respective use-count.
	 */
	private Map<String, CacheEntry> cache = new HashMap<>();

	/**
	 * Number of maximum entries held by the cache. By default the highest
	 * possible integer value.
	 */
	private int maxEntries = Integer.MAX_VALUE;

	// //////////////////////////////////////////////////////////////////////
	// // CONSTRUCTOR ////
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the cache without a limitation for the count of contained
	 * entries.
	 */
	public Cache() {
	}

	/**
	 * Initializes the cache limiting the count of resources to cache at
	 * maximum.
	 * 
	 * @param maxEntries
	 *            The count of resources this cache should manage at maximum.
	 */
	public Cache(int maxEntries) {
		this.maxEntries = maxEntries;
	}

	// //////////////////////////////////////////////////////////////////////
	// // METHODS ////
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element mapped to <code>key</code>. If a collection of resources
	 * was associated with the key only the first will be returned. In this case use
	 * <code>getAll()</code> to get the whole collection.
	 * @param key The key the resource is mapped to.
	 * @return The resource mapped to <code>key</code> or <code>null</code> if there is
	 * none.
	 */
	public R get(String key) {
		CacheEntry entry = cache.get(key);
		if(entry != null) {
			return entry.get();
		} else {
			return null;
		}
	}

	/**
	 * Returns the collection of resources associated with <code>key</code>.
	 * @param key The key the collection is mapped to.
	 * @return The collection of resources mapped to <code>key</code> or <code>null</code> 
	 * if there is none.
	 */
	public Collection<R> getAll(String key) {
		CacheEntry entry = cache.get(key);
		if(entry != null) {
			return entry.getAll();
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param resource
	 *            The resource to be added to the cache.
	 * @return True, if the resource was successfully added, else not.
	 * @throws IllegalArgumentException
	 *             Thrown if <code>resource</code> is <code>null</code> or the
	 *             dummy-flag in the resource is set and it is not a valid
	 *             resource.
	 */
	public boolean add(R resource) throws IllegalArgumentException {
		if (!cache.containsKey(resource.getKey())) {

			// Create new entry for the resource and add it.
			CacheEntry currentEntry = new CacheEntry(resource, 0);
			cache.put(resource.getKey(), currentEntry);
			return true;
		}

		// There's already a resource in cache with the key of the
		// passed resource. Don't add it.
		return false;
	}

	/**
	 * Checks if the maximum number of held resources is reached and deletes
	 * some of them according to the following removal-strategy: The lowest
	 * <code>REMOVE_LOWEST_PERC</code> of all resources with the lowest
	 * use-count will be removed. If there are entries with the same use-count
	 * the average timestamp of all resources contained in the entry will be
	 * used.
	 * @return A collection of all the entries removed from cache.
	 */
	protected Collection<CacheEntry> manage() {
		if (cache.size() >= maxEntries) {

			return purge();
			
		} else {
			// If nothing had to be removed return a empty collection:
			return new LinkedList<>();
		}
	}
	
	/**
	 * Removes entries from the cache according to the following removal-strategy: The lowest
	 * <code>REMOVE_LOWEST_PERC</code> of all resources with the lowest
	 * use-count will be removed. If there are entries with the same use-count
	 * the average timestamp of all resources contained in the entry will be
	 * used.
	 * By contrast to <code>manage()</code> this method removes entries regardless if a certain limit
	 * is reached.
	 * @return A collection of all the entries removed from cache.
	 */
	protected Collection<CacheEntry> purge() {
		// Number of elements to be remove.
		int entriesToRemove = cache.size() / REMOVE_LOWEST_PERC;

		// Sort cache entries according to their natural order.
		List<CacheEntry> sortedEntries = new LinkedList<>(cache.values());
		Collections.sort(sortedEntries);

		// Chose first entries: these are the entries to remove.
		List<CacheEntry> toRemove = sortedEntries.subList(0, entriesToRemove);
		// Remove the entries and remember resources removed:
		for (CacheEntry current : toRemove) {
			cache.remove(current.get().getKey());
		}
		return toRemove;
	}

	/**
	 * Returns the number of elements currently cached. This implementation
	 * returns the number of entries. Override this method if another way of
	 * determination of the current size is required, e.g. if sets of resources
	 * are cached.
	 * 
	 * @return The number of elements currently cached.
	 */
	protected int getEntryCount() {
		return cache.size();
	}

	/**
	 * Returns the collection of all entries in the cache.
	 * 
	 * @return The collection of all entries in the cache.
	 */
	protected Collection<CacheEntry> getEntries() {
		return cache.values();
	}
	
	/**
	 * Removes all entries from the cache.
	 */
	public void clear() {
		cache.clear();
	}
	
	/**
	 * Returns the number of associations between a key and a resource currently cached.
	 * @return The number of elements cached.
	 */
	public int size() {
		return getEntries().size();
	}
	
	/**
	 * Returns the maximum count of key-resources-associations permitted in cache.
	 * @return The maximum count of key-resources-associations
	 */
	public int getMaxEntries() {
		return maxEntries;
	}
	
	/**
	 * Sets the maximum count of key-resources-associations permitted in cache.
	 * @param maxEntries The maximum count of key-resources-associations permitted in cache.
	 */
	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}
	
	
	// //////////////////////////////////////////////////////////////////////
	// // PRIVATE CLASSES ////
	// //////////////////////////////////////////////////////////////////////


	/**
	 * Bean associating a collection of resources and a counter how often it has
	 * been used. The timestamp of the resources creation is stored in it (see
	 * definition of <code>DataResource</code>). For easier handling this class
	 * provides methods for both handling a single resource and a collection of
	 * resources (see <code>getSingleResource()</code> and
	 * <code>getResourcesAll()</code>).
	 * 
	 * @author Johannes Bauer (Spezifikation: Matthias Fisch)
	 * 
	 */
	protected class CacheEntry implements Comparable<CacheEntry>, Serializable {
		private static final long serialVersionUID = -5457874035707874562L;

		/**
		 * The resource stored.
		 */
		private final Collection<R> resources;

		/**
		 * Counts the uses of the resource stored in this entry.
		 */
		private int useCount;

		/**
		 * Initializes the entry with a single resource to be held.
		 * 
		 * @param resource
		 *            The resource to be held.
		 * @param useCount
		 *            How often <code>resource</code> was used in the past.
		 */
		public CacheEntry(R resource, int useCount) {
			this.resources = new HashSet<R>();
			resources.add(resource);
		}

		/**
		 * Initializes the entry with the resource to be held.
		 * 
		 * @param resources
		 *            The resources to be held.
		 * @param useCount
		 *            How often <code>resource</code> was used in the past.
		 */
		public CacheEntry(Collection<R> resources, int useCount) {
			this.resources = new HashSet<R>();
			this.resources.addAll(resources);
		}
		
		/**
		 * Returns the resources stored in this entry if there is only a single
		 * one. Calling this method does not increment the count of uses. To
		 * accomplish this call <code>incrementUseCount()</code> separately.
		 * 
		 * @return The resource stored in this entry or the first if there are
		 *         more than one resources contained. If no resource is held
		 *         this method will return <code>null</code>.
		 */
		public R get() {
			for(R resource : resources) {
				return resource;
			}
			return null;
		}
		
		/**
		 * Returns all resources stored in this entry.
		 * @return All resources stored in this entry or <code>null</code> if there are
		 * none.
		 */
		public Collection<R> getAll() {
			return resources;
		}

		/**
		 * Returns how often the resource stored in this entry has been used.
		 * 
		 * @return How often <code>resource</code> has been used.
		 */
		public int getUseCount() {
			return useCount;
		}

		/**
		 * Notifies this entry that the resource stored in it has been used.
		 */
		public void incrementUseCount() {
			++useCount;
		}

		/**
		 * Compares the entry with <code>o</code>. A entry is greater than
		 * another entry if its usecount is greater. If the usecount of both
		 * entries is equal the entry with the highest average timestamp of all
		 * its resources is the greatest. Two entries are equal if their
		 * usecount and average timestamps are equal.
		 * 
		 * @return A negative integer, zero, or a positive integer as this entry
		 *         is less than, equal to, or greater than <code>o</code>.
		 */
		@Override
		public int compareTo(CacheEntry o) {
			// First compare the useCount.
			if (useCount < o.useCount) {
				return -1;
			} else if (useCount > o.useCount) {
				return 1;
			} else {
				// Compare the timestamps of the contained resource objects.
				if (get().getTimestamp() < o.get().getTimestamp()) {
					return -1;
				} else if (get().getTimestamp() > o.get().getTimestamp()) {
					return 1;
				}
			}

			// useCount and timestamps are equal.
			return 0;
		}
	}
}
