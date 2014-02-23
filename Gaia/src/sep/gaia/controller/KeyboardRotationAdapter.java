package sep.gaia.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import sep.gaia.state.GLState;
import sep.gaia.util.FloatVector3D;


/**
 * This is to extent a <code>KeyAdapter</code> to check if the user wants to
 * rotate the virtual earth by pressing one of the arrow keys.
 * 
 * @author Johannes Bauer (Spezifikation: Michael Mitterer)
 */
public class KeyboardRotationAdapter extends KeyAdapter {

	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * KeyboardRotationAdapter constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
    public KeyboardRotationAdapter(GLState state) {
    	this.state = state;
    }
	
	/**
	 * Check if one of the rotation keys is pressed.
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch (key) { 
        case KeyEvent.VK_PAGE_DOWN:
        	if (state.is2DMode() && state.getRotation().getX() > 0) {
        		state.rotate(new FloatVector3D(-10, 0, 0));
        	}
        	break;
        case KeyEvent.VK_PAGE_UP:
        	if (state.is2DMode() && state.getRotation().getX() < 45) {
        		state.rotate(new FloatVector3D(10, 0, 0));
        	}
        	break;
        default: break;
		}
	}
}
