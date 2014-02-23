package sep.gaia.controller.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.ui.MainWindow;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to open the <code>POIEditorWindow</code>.
 * The configuration file will be updated immediately.
 * 
 * @author Michael Mitterer
 */
public class POISettingsListener implements ActionListener {
	/** 
	 * The <code>GeoState</code> reference.
	 */
	private GeoState state;
	
	/**
	 * The calling JDialog
	 */
	private JDialog settings;
	
	/**
	 * POISettingsListener constructor
	 * 
	 * @param state The current <code>GeoState</code>
	 * @param settings The calling JDialog
	 */
	public POISettingsListener(GeoState state, JDialog settings) {
		this.state = state;
		this.settings = settings;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
