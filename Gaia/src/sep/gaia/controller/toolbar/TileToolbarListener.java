package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to change to another tile texture by pressing an item in the
 * tile list in the toolbar.
 * 
 * @author Michael Mitterer
 */
public class TileToolbarListener implements ActionListener {
	/** 
	 * The <code>GeoState</code> reference.
	 */
	private GeoState state;
	
	/**
	 * TileToolbarListener constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
	public TileToolbarListener(GeoState state) {
		this.state = state;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}