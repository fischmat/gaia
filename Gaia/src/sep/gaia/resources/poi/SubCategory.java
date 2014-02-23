package sep.gaia.resources.poi;

/**
 * Bean-class for describing a sub-category of POIs. A name and a condition to be met by
 * POIs in the sub-category, are stored.
 * A sub-category can be activated and deactivated. If a category has the activated-flag set,
 * its <code>POIManager</code> will take it in consideration when loading new POIs
 * and when passing available resources to its observers.
 * @author Max Witzelsperger (Specification: Matthias Fisch)
 *
 */
public class SubCategory {
	
	/**
	 * The sub-categories name.
	 */
	private String name; 
	
	/**
	 * Filter with conditions that all POIs in the collection <code>pois</code> must fulfill.
	 * This member can be <code>null</code> if there should be no restrictions to the POIs stored in the
	 * subcategory.
	 */
	private POIFilter conditions;
	
	/**
	 * Flag specifying wether the sub-category is activated. If a category has the activated-flag set,
	 * its <code>POIManager</code> will take it in consideration when loading new POIs
	 * and when passing available resources to its observers.
	 */
	private boolean activated;

	/**
	 * Initializes the sub-category by specifying its name, the criteria POIs to be contained must fulfill
	 * and the set of POIs themselves.
	 * @param name The sub-categories name.
	 * @param conditions Filter with conditions that all POIs in the collection <code>pois</code> must fulfill.
	 * This parameter can be <code>null</code> if there should be no restrictions to the POIs stored in the
	 * subcategory.
	 * @param enabled <code>true</code> if the subcategory is activated.
	 * @throws IllegalArgumentException Thrown if <code>name == null</code> or any of the
	 * POIs in <code>pois</code> does not meet the criteria in <code>conditions</code>.
	 */
	public SubCategory(String name, POIFilter conditions, boolean enabled) 
																throws IllegalArgumentException {
		super();
		
		if (name == null) {
			throw new IllegalArgumentException("The name must not be null.");
		}
		this.name = name;
		this.conditions = conditions;
		this.activated = enabled;
	}

	/**
	 * Returns the sub-categories name.
	 * @return The sub-categories name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the sub-categories name.
	 * @param name The sub-categories name.
	 * @throws IllegalArgumentException Thrown if <code>name == null</code>.
	 */
	public void setName(String name) throws IllegalArgumentException {
		if(name != null) {
			this.name = name;
		} else {
			throw new IllegalArgumentException("Subcategories must have a name.");
		}
	}

	/**
	 * Returns a filter that all POIs in the sub-category must fulfill to be part of it.
	 * @return The conditions to be part of the sub-category.
	 */
	public POIFilter getConditions() {
		return conditions;
	}

	/**
	 * Sets a filter that all POIs in the sub-category must fulfill to be part of it.
	 * @param conditions A filter that all POIs in the sub-category must fulfill to be part of it.
	 */
	public void setConditions(POIFilter conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return <code>true</code> if the sub-category is enabled. Otherwise
	 * <code>false</code> is returned.
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Specifies if the sub-category is activated.
	 * @param activated <code>true</code> to activate or <code>false</code>
	 * to deactivate the category.
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
}
