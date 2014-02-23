package sep.gaia.controller.menubar;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import sep.gaia.util.Logger;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user presses the "Help" item in the menubar to open the help documentation.
 * 
 * @author Michael Mitterer
 */
public class HelpMenubarListener implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().open(new File("Manual.pdf"));
			} catch (IOException e1) {
				Logger.getInstance().message("Das Handbuch konnte nicht geöffnet werden.");
			}
	}

}
