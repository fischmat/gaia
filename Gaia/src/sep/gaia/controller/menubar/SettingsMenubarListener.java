package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import sep.gaia.state.GeoState;
import sep.gaia.ui.MainWindow;
import sep.gaia.ui.SettingsWindow;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user presses an item in the menu bar, for example to open the
 * <code>SettingsWindow</code>, exit the program or open the help documentation.
 * 
 * @author Michael Mitterer
 */
public class SettingsMenubarListener implements ActionListener {

	
	/**
	 * The <code>MainWindow</code> reference.
	 */
	private JFrame mainWindow;
	
	/**
	 * SettingsMenubarListener constructor
	 * 
	 * @param state The current <code>GeoState</code>
	 * @param mainWindow The <code>MainWindow</code> reference
	 */
	public SettingsMenubarListener(JFrame mainWindow) {
		this.mainWindow = mainWindow;
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new SettingsWindow(mainWindow);
		
	} 

}
