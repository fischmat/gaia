package sep.gaia.resources.poi;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import sep.gaia.resources.Cache;

/**
 * Defines a class for caching Point-Of-Interests (POI) in memory.
 * Besides the limitations for POIs must be equal, cached data
 * will be returned if the bounding-box is equal to or is
 * contained in the one POIs are queried for. This gives advantage
 * if a subset of previously cached POIs of the same category are
 * queried, e.g. if the view is zoomed in.
 * @author Max Witzelsperger (Specification: Matthias Fisch)
 *
 */
public class POICache extends Cache<PointOfInterest> {

	private class POICacheEntry {
		
		private POIQuery query;
		
		private Collection<PointOfInterest> pois;

		/**
		 * Constructs an entry for a <code>POICache</code>, which contains
		 * a query and a list of pois.
		 * 
		 * @param query the poi query to be saved by this entry
		 * @param pois the list of pois to be saved by this entry
		 */
		public POICacheEntry(POIQuery query, Collection<PointOfInterest> pois) {
			super();
			this.query = query;
			this.pois = pois;
		}

		protected POIQuery getQuery() {
			return query;
		}

		protected Collection<PointOfInterest> getPois() {
			return pois;
		}
	}
	
	private Collection<POICacheEntry> entries = new LinkedList<>();
	
	/**
	 * Checks if there are POIs cached for <code>query</code> by two criteria.
	 * POI-Data is returned if the limitations of <code>query</code> match
	 * those of the prior cached query. Also the bounding-box in <code>query</code>
	 * must be contained in or be equal to the bounding-box of the query held in the cache.
	 * 
	 * @param query The query defining a bounding-box to search in and conditions to be met.
	 * @return The collection of POIs suitable for <code>query</code> or <code>null</code>
	 * if no POIs are available or only a subset is cached.
	 */
	public Collection<PointOfInterest> get(POIQuery query) {
		
		if(query != null) {
			for (POICacheEntry entry : entries) {
				
				POIQuery cachedQuery = entry.getQuery();
				boolean attributeMatch = cachedQuery.getLimitations().equals(query.getLimitations());
				boolean boxContained = cachedQuery.getBoundingBox().contains(query.getBoundingBox());
				
				if (attributeMatch && boxContained) {
					
					return entry.getPois();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the count of all POIs associated with any query cached.
	 * Note that various entries referencing the same POI are count as one.
	 * @return Total count of POIs cached.
	 */
	@Override
	protected int getEntryCount() {
		return entries.size();
	}
	
	/**
	 * Checks all POIs in <code>pois</code> if they are equal to any of the POIs cached.
	 * If a POI is equal to one already cached, the reference to the latter will be added 
	 * to the collection returned. If a POI in <code>pois</code> is not equal to any of the
	 * POIs cached, its reference will be added to the collection returned.
	 * @param pois The POIs to be checked.
	 * @return A collection semantically equal to <code>pois</code> but with a maximum of
	 * references to objects already cached.
	 */
	private Collection<PointOfInterest> getIntersection(Collection<PointOfInterest> pois) {
		
		Collection<PointOfInterest> intersection = new LinkedList<>();
		
		// check for every poi if it is cached
		for (PointOfInterest currentPOI : pois) {
			
			// indicates whether currentPOI is in the cache
			boolean contains = false;
			
			// Iterate through all POI-collections in the cache
			Iterator<POICacheEntry> entryIter = entries.iterator();
			while (!contains && entryIter.hasNext()) {
				
				POICacheEntry nextEntry = entryIter.next();
				
				// search poiList for currentPOI
				Iterator<PointOfInterest> poiIter = nextEntry.getPois().iterator();
				while (poiIter.hasNext()) {
					
					PointOfInterest nextPOI = poiIter.next();
					if (nextPOI.equals(currentPOI)) {
						
						intersection.add(nextPOI); // take reference from cache
						contains = true;
					}
				}
			}
			
			if (!contains) {
				// poi not found in cache, so take original reference
				intersection.add(currentPOI);
			}			
		}
		return intersection;
	}
	
	/**
	 * Adds a collection of POIs, identifiable by <code>query</code>, to the cache.
	 * Each POI in <code>pois</code> will be checked for equality to a POI already cached
	 * and if possible the latter is referenced instead of the one in <code>pois</code>
	 * in order to save memory.
	 * The use-count of the collection is initially set to zero.
	 * Note that the collection as a whole is the entry inserted and not the particular
	 * elements of it.
	 */
	public void addResources(POIQuery query, Collection<PointOfInterest> pois)
			throws IllegalArgumentException {

		Collection<PointOfInterest> intersectPOIs = this.getIntersection(pois);
		
		entries.add(new POICacheEntry(query, intersectPOIs));
	}
	
}
