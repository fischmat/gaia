package sep.gaia.resources.poi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of conditions a POI must fulfill to be part of the system with the respective filter.
 * Those conditions are stored as key-value-pairs as they are used by the Overpass-API and are thus defining
 * attributes the POIs must have.
 * For the values of the attributes POSIX-extended regular expressions can be used. 
 * @author Matthias Fisch (specification), Matthias Fisch (implementation)
 *
 */
public class POIFilter {
	
	/**
	 * A map associating keys with their respective value defining an attribute of a POI.
	 * The values must be POSIX-extended regular expressions.
	 */
	private Map<String, String> limitations = new HashMap<>();
	
	/**
	 * Initializes an empty filter.
	 */
	public POIFilter() { }
	
	/**
	 * Initializes the Filter with the limitations defined by the key-value-pairs
	 * in <code>limitations</code>, where the value is expected to be a POSIX-extended
	 * regular expression.
	 * @param limitations The key value-pairs.
	 */
	public POIFilter(Map<String, String> limitations) {
		this.limitations.putAll(limitations);
	}
	
	/**
	 * Adds a attribute a POI must have to be part of the system with this filter.
	 * @param key The key (name) of the attribute to add.
	 * @param value The value of the attribute to add. This parameter is treated as a regular
	 * expression.
	 */
	public void addLimitation(String key, String value) {
		// Add the value to the collection of all limitations:
		limitations.put(key, value);
	}
	
	/**
	 * Checks if the attrbutes in <code>poi</code> fulfill the conditions of this filter.
	 * @param poi The POI to check.
	 * @return <code>true</code> if <code>poi</code> meets the criteria of this filter.
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean isMatching(PointOfInterest poi) {
		Map<String, String> poiAttributes = poi.getAttributes();
		for(String key : limitations.keySet()) {
			String poiAttrValue = poiAttributes.get(key);
			String value = limitations.get(key);
			
			// Check if the attribute does not exist in the POI or differs in value:
			if(poiAttrValue == null || !poiAttrValue.matches(value)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the attributes of any of the POIs in <code>pois</code> fulfill the conditions of this filter.
	 * @param pois The POIs to check.
	 * @return <code>true</code> if all POIs in <code>pois</code> meet the criteria of this filter.
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean isMatching(Collection<PointOfInterest> pois) {
		for(PointOfInterest poi : pois) {
			if(!isMatching(poi)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the attrbutes in <code>poi</code> fulfill the conditions of this filter.
	 * In contrary to <code>isMatching()</code> this method only returns <code>true</code>
	 * if <code>poi</code> has no other attributes than those specified by this filter. 
	 * @param poi The POI to check.
	 * @return <code>true</code> if and only if <code>poi</code> meets the criteria specified by this filter.
	 */
	public boolean isExactlyMatching(PointOfInterest poi) {
		Map<String, String> poiAttributes = poi.getAttributes();
		
		for(String key : poiAttributes.keySet()) {
			String value = limitations.get(key);
			if(value == null) {
				return false;
			}
		}
		
		for(String key : limitations.keySet()) {
			String poiAttrValue = poiAttributes.get(key);
			String value = limitations.get(key);
			
			// Check if the attribute does not exist in the POI or differs in value:
			if(poiAttrValue == null || !poiAttrValue.matches(value)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the attributes of any of the POIs in <code>pois</code> fulfill the conditions of this filter.
	 * In contrary to <code>isMatching()</code> this method only returns <code>true</code>
	 * if the entries in <code>pois</code> have no other attributes than those specified by this filter. 
	 * @param pois The POIs to check.
	 * @return <code>true</code> if all POIs in <code>pois</code> meet the criteria of this filter.
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean isExactlyMatching(Collection<PointOfInterest> pois) {
		for(PointOfInterest poi : pois) {
			if(!isExactlyMatching(poi)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a map associating keys with their respective value defining an attribute of a POI.
	 * The values are POSIX-extended regular expressions
	 * @return The attributes to be met by a system described by this filter.
	 */
	public Map<String, String> getLimitations() {
		return limitations;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof POIFilter) {
			
			Map<String, String> compLimits = ((POIFilter) obj).getLimitations();
			
			for(String key : limitations.keySet()) {
				String value = limitations.get(key);
				String compValue = compLimits.get(key);
				if(compValue == null || !value.equals(compValue)) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
}
 