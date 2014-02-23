package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.weather.WeatherManager;
import sep.gaia.resources.wikipedia.WikipediaManager;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to enable/disable the weather overlay by pressing the item in
 * the menubar.
 * 
 * @author Michael Mitterer
 */
public class WeatherMenubarListener implements ActionListener {
	/**
	 * This is used to know if the weather informations are available
	 */
	private boolean showWeather = true;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WeatherManager weatherManager = (WeatherManager) ResourceMaster.getInstance().getResourceManager("WeatherManager");
		
		if (showWeather) {
			weatherManager.disable();
			showWeather = false;
		} else {
			weatherManager.enable();
			showWeather = true;
		}
	}
}
