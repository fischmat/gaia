package sep.gaia.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sep.gaia.state.GLState;
import sep.gaia.util.FloatVector3D;

/**
 * This class is to implement the <code>ActionListener</code> interface to 
 * check if the user wants to change the rotation (direction up)
 *   by pressing the rotation up button in the tool bar. 
 *
 */
public class RotationUpListener implements ActionListener {

	/**
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * The RotationUpListener constructor.
	 * 
	 * @param state The current <code>GLState</code>.
	 */
	public RotationUpListener(GLState state) {
		this.state = state;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (state.is2DMode() && state.getRotation().getX() < 45) {
    		state.rotate(new FloatVector3D(10, 0, 0));
    	}
	}
	
	
}
