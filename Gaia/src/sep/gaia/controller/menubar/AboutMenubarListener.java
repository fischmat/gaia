package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import sep.gaia.ui.AboutWindow;
import sep.gaia.ui.MainWindow;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user presses the "About" item in the menubar to open the
 * <code>AboutWindow</code>.
 * 
 * @author Michael Mitterer
 */
public class AboutMenubarListener implements ActionListener {
	/**
	 * The <code>MainWindow</code> reference.
	 */
	private JFrame mainWindow;
	
	/**
	 * AboutMenubarListener constructor
	 * 
	 * @param mainWindow The <code>MainWindow</code> reference
	 */
	public AboutMenubarListener(JFrame mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new AboutWindow(mainWindow);
	}

}
