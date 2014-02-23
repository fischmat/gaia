package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user presses the "Exit" item in the menubar to exit the program.
 * 
 * @author Michael Mitterer
 */
public class ExitMenubarListener implements ActionListener {
	/**
	 * The <code>MainWindow</code> reference.
	 */
	private JFrame mainWindow;
	
	/**
	 * ExitMenubarListener constructor
	 * 
	 * @param mainWindow The <code>MainWindow</code> reference
	 */
	public ExitMenubarListener(JFrame mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		mainWindow.dispose();
		System.exit(0);
	}

}
