package sep.gaia.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import sep.gaia.state.StateManager;
import sep.gaia.state.GLState;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

/**
 * This is to extent a <code>KeyAdapter</code> to check if the user wants to
 * rotate the virtual earth by pressing one of the arrow keys.
 * 
 * @author Michael Mitterer
 */
public class KeyboardMoveAdapter extends KeyAdapter {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * KeyboardRotationAdapter constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
    public KeyboardMoveAdapter(GLState state) {
    	this.state = state;
    }
	
	/**
	 * Check if one of the arrow keys is pressed.
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		int tileZoom = AlgoUtil.glToTileZoom(state.getZoom());
		float moveDist = AlgoUtil.glCoordsPerPixelRange((int)AlgoUtil.TILE_LENGTH_IN_PIXELS, tileZoom);
		
		FloatVector3D translation = null;
		
		switch (key) { 
        case KeyEvent.VK_UP:
        	translation = new FloatVector3D(0, moveDist, 0);
        	break;
        	
        case KeyEvent.VK_DOWN:
        	translation = new FloatVector3D(0, -moveDist, 0);
        	break;
        	
        case KeyEvent.VK_LEFT:
        	translation = new FloatVector3D(-moveDist, 0, 0);
        	break;
        	
        case KeyEvent.VK_RIGHT :
        	translation = new FloatVector3D(moveDist, 0, 0);
        	break;
        	
        default: break;
		}
		
		if(translation != null) {
			float rotationZ = state.getRotation().getZ();
			translation.rotateAroundZ(-rotationZ);
			state.translate(translation);
		}
	}
}

