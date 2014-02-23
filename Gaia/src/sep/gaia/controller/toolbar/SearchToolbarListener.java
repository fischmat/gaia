package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import sep.gaia.resources.Loader;
import sep.gaia.resources.locationsearch.Location;
import sep.gaia.resources.locationsearch.LocationQuery;
import sep.gaia.resources.locationsearch.LocationSearch;
import sep.gaia.ui.LocationWindow;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to search a location by entering a String in the search field
 * in the toolbar.
 * 
 * @author Michael Mitterer
 */
public class SearchToolbarListener implements ActionListener {
	/** 
	 * The JTextField reference.
	 */
	private JTextField text;
	
	private LocationSearch search;
	private Loader<LocationQuery, Location> loader;
	private LocationWindow window;
	
	/**
	 * SearchToolbarListener constructor
	 * 
	 * @param text The search text
	 */
	public SearchToolbarListener(JTextField text) {
		this.text = text;
		search = new LocationSearch();
		loader = search.getLoader();
		window = new LocationWindow();
		window.setSearch(search);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (text.getText() != null) {
			loader.removeListener(window);
			search.queryforLocations(text.getText().replace(' ', '+'));
			loader.addListener(window);
		}
	}

}
