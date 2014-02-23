package sep.gaia.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.resources.poi.POICategory;
import sep.gaia.resources.poi.POIManager;
import sep.gaia.resources.poi.SubCategory;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to change the categories of the <code>PointOfInterest</code>
 * objects which should be displayed in the view by setting a
 * <code>ComboBox</code> in the poibar.
 * 
 * @author Michael Mitterer
 */
public class POIBarListener implements ActionListener {
	/** 
	 * The major poi category
	 */
	private SubCategory cat;
	
	/**
	 * The POI-manager managing the linked subcategory.
	 */
	private POIManager manager;
	
	/**
	 * POIBarListener constructor
	 * 
	 * @param cat the major poi category
	 */
	public POIBarListener(SubCategory cat, POIManager manager) {
		this.cat = cat;
		this.manager = manager;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(manager != null && cat != null) {
			// Toggle the activation of the category:
			manager.setSubCategoryActive(cat, !cat.isActivated());
		}
	}
}
