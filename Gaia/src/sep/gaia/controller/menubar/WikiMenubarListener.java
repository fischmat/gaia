package sep.gaia.controller.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.wikipedia.WikipediaManager;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to enable/disable the availability of the wikipedia
 * informations by pressing the item in the menubar.
 * 
 * @author Michael Mitterer
 */
public class WikiMenubarListener implements ActionListener {
	/**
	 * This is used to know if the wikipedia informations are available
	 */
	private boolean showWiki = true;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WikipediaManager wikiManager = (WikipediaManager) ResourceMaster.getInstance().getResourceManager("Wikipedia");
		if (showWiki) {
			wikiManager.disable();
			showWiki = false;
		} else {
			wikiManager.enable();
			showWiki = true;
		}
	}
}
