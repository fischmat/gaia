package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.state.GLState;
import sep.gaia.util.FloatVector3D;

/**
 * This class is to implement the <code>ActionListener</code> interface to 
 * check if the user wants to change the rotation (direction down)
 *   by pressing the rotation down button in the tool bar. 
 *
 */
public class RotationDownListener implements ActionListener {
	
	/**
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * RotationDownListener constructor.
	 * 
	 * @param state The current <code>GLState</code>.
	 */
	public RotationDownListener(GLState state) {
		this.state = state;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (state.is2DMode() && state.getRotation().getX() > 0) {
    		state.rotate(new FloatVector3D(-10, 0, 0));
    	}
	}

}
