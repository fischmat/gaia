package sep.gaia.resources.poi;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.jogamp.opengl.util.texture.TextureData;

/**
 * Represents a major-category for point-of-interests. Each category has its
 * own mnemonic name, conditions to be met by POIs to be part of it,
 * as well as sub-categories containing the POIs themselves.
 * If a category has the activated flag set, its <code>POIManager</code> will
 * take it in consideration when loading new POIs and when passing newly
 * available resources to its observers.
 * 
 * @author Max Witzelsperger (Specification: Matthias Fisch)
 *
 */
public class POICategory {

	/**
	 * The name of the category.
	 */
	private String name;

	/**
	 * Collection of sub-categories of this category.
	 * They can be used for specifying stronger criteria to be met.
	 */
	private Collection<SubCategory> subcategories;

	/**
	 * The symbol to be used for this category.
	 */
	private TextureData symbol;

	/**
	 * Flag whether the category is activated. If a category has the activated flag set,
	 * its <code>POIManager</code> will take it in consideration when loading new POIs
	 * and when passing available resources to its observers.
	 */
	private boolean activated;

	/**
	 * The conditions a POI must fulfill to be part of the category.
	 */
	private POIFilter filter;

	/**
	 * Initializes a major-category for POIs.
	 * @param name The name of the category.
	 * @param subcategories Collection of sub-categories of this category.
	 * They can be used for specifying stronger criteria to be met.
	 * @param enabled Flag wether the category is activated. If a category has the actived flag set,
	 * its <code>POIManager</code> will take it in consideration when loading new POIs
	 * and when passing newly available resources to its observers.
	 * @param filter The conditions a POI must fulfill to be part of the category.
	 */
	public POICategory(String name, Collection<SubCategory> subcategories,
			boolean enabled, POIFilter filter) {
		super();
		this.name = name;
		this.activated = enabled;
		this.filter = filter;
		this.setSubcategories(subcategories);
	}

	/**
	 * Returns if the category and its sub-categories are activated. If a category has the actived flag set,
	 * its <code>POIManager</code> will take it in consideration when loading new POIs
	 * and when passing newly available resources to its observers.
	 * @return <code>true</code> if the category is activated. Otherwise <code>false</code>
	 * is returned.
	 */
	public boolean isActivated() {
		return activated;
	}


	/**
	 * Sets if the major-category and its sub-categories are activated.
	 * If a category has the actived flag set,
	 * its <code>POIManager</code> will take it in consideration when loading new POIs
	 * and when passing newly available resources to its observers.
	 * @param active <code>true</code> if the category should be activated. Otherwise <code>false</code>
	 * is returned.
	 */
	public void activate(boolean active) {
		this.activated = active;
		// Set all subcategories to the same state of activation:
		for(SubCategory subCategory : subcategories) {
			subCategory.setActivated(active);
		}
	}
	
	/**
	 * Getter of the category name
	 * 
	 * @return the name of the category
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a copy of the subcategory <code>cat</code> with the union of
	 * the set of limitations in the major-category and the set of limitations in
	 * the sub-category. If the filters in this major-category and <code>cat</code>
	 * specify an attribute with the same key but a different value, the value
	 * of <code>cat</code> is used for the union.
	 * @param cat The sub-category to merge limitations with.
	 * @return A copy of <code>cat</code> with the merged limitation-sets.
	 */
	private SubCategory applyMajorLimitations(SubCategory cat) {
		
		// the limitations to be taken from cat
		Map<String, String> limitations = cat.getConditions().getLimitations();
				
		for (String key : filter.getLimitations().keySet()) {
			
			// only in this case, take the k-v-pair from the limitation list of this
			if (!limitations.containsKey(key)) {
				
				limitations.put(key, filter.getLimitations().get(key));
			}
		}
		
		// holds the unit of the limitations in cat and this
	    POIFilter extendedConditions = new POIFilter(limitations);
	    
	    // create a copy of cat with additional conditions from this
		SubCategory copyCat = new SubCategory(cat.getName(),
				extendedConditions, cat.isActivated());
		
		return copyCat;
	}
	
	/**
	 * Returns a collection of all sub-categories of this category.
	 * The limitations of the major-category are included in those in
	 * a subcategory.
	 * @return A collection of all sub-categories.
	 */
	public Collection<SubCategory> getSubcategories() {
		return subcategories;
	}

	/**
	 * Sets the collection of all sub-categories.
	 * Entries in <code>subcategories</code> must not contain the limitations
	 * of this major-category in their limitation-set.
	 * @param subcategories Collection of all sub-categories
	 */
	public void setSubcategories(Collection<SubCategory> subcategories) {
		
		this.subcategories = new LinkedList<>();
		
		// apply all conditions from this to the passed subcategories
		for (SubCategory cat : subcategories) {
			
			this.subcategories.add(this.applyMajorLimitations(cat));
		}
	}
}
