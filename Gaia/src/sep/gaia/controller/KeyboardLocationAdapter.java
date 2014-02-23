package sep.gaia.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import sep.gaia.resources.Loader;
import sep.gaia.resources.locationsearch.Location;
import sep.gaia.resources.locationsearch.LocationQuery;
import sep.gaia.resources.locationsearch.LocationSearch;
import sep.gaia.ui.LocationWindow;


/**
 * This class is to implement the <code>KeyAdapter</code> interface to check
 * if the user wants to search for a location by entering a string
 * into the search field of the tool bar.
 * 
 * @author fabian
 *
 */
public class KeyboardLocationAdapter extends KeyAdapter {

	/**
	 * The JTextField reference.
	 */
	private JTextField text;
	
	/**
	 * The LocationSearch reference.
	 */
	private LocationSearch search;
	
	/**
	 * The loader reference.
	 */
	private Loader<LocationQuery, Location> loader;
	
	/**
	 * The window reference.
	 */
	private LocationWindow window;
	
	/**
	 * The constructor to initialize the search.
	 * 
	 * @param text The location to be searched for.
	 */
	public KeyboardLocationAdapter(JTextField text) {
		this.text = text;
		search = new LocationSearch();
		loader = search.getLoader();
		window = new LocationWindow();
		window.setSearch(search);
	}
	
	/**
	 * This method checks if the return key is pressed and 
	 * a location has been entered by the user to be searched for.
	 */
	public void keyPressed(KeyEvent e) {
		if (text.getText() != null && e.getKeyCode() == KeyEvent.VK_ENTER) {
			loader.removeListener(window);
			search.queryforLocations(text.getText().replace(' ', '+'));
			loader.addListener(window);
		}
	}
}
