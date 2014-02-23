package sep.gaia.resources.poi;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * A set of <code>PointOfInterest</code>-objects.
 * Defines methods to form unions, intersection and differences.
 * Also a subset of the stored POIs can be generated by applying a
 * filter.
 * @author Max Witzelsperger (Specification: Matthias Fisch)
 *
 */
public class POISet extends HashSet<PointOfInterest> {
	
	private static final long serialVersionUID = -6420080944595038237L;
	
	/**
	 * Initializes an empty set.
	 */
	public POISet() {
		super();
	}


	/**
	 * Initializes the set with a collection of POIs.
	 * All POIs in <code>pois</code> are added to the set.
	 * @param pois Collection of POIs to be added to the set.
	 */
	public POISet(Collection<PointOfInterest> pois) {
		super(pois);
	}
	
	/**
	 * Returns a subset of <code>this</code> with the characteristic that all pois in it
	 * meet the conditions in <code>conditions</code>.
	 * The set itself is not modified by this call.
	 * @param conditions The filter to be applied on the POIs of the set.
	 * @return A set with POIs conforming to <code>conditions</code>.
	 */
	public POISet filter(POIFilter conditions) {
		Collection<PointOfInterest> filteredPois = new LinkedList<>();
		
		for (PointOfInterest poi : this) {
			
			if (conditions.isMatching(poi)) {
				filteredPois.add(poi);
			}
		}
		
		return new POISet(filteredPois);
	}
}
 