package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.controller.KeyboardZoomAdapter;
import sep.gaia.state.StateManager;
import sep.gaia.state.GLState;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to change the zoom level by pressing the zoom in or zoom out
 * button in the toolbar.
 * 
 * @author Johannes Bauer (Spezifikation: Michael Mitterer)
 */
public class ZoomOutListener implements ActionListener {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * ZoomToolbarListener constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
	public ZoomOutListener(GLState state) {
		this.state = state;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		state.zoom(KeyboardZoomAdapter.ZOOM_OUT);
	}
}
