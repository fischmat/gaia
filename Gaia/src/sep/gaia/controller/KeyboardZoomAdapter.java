package sep.gaia.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import sep.gaia.state.GLState;

/**
 * This is to extent a <code>KeyAdapter</code> to check if the user wants to
 * zoom in or out of the virtual earth by pressing the + / - keys.
 * 
 * @author Johannes Bauer (Spezifikation: Michael Mitterer)
 */
public class KeyboardZoomAdapter extends KeyAdapter {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	public static final float ZOOM_STEP_3D = 128;
	/**
	 * A constant amount by which can be zoomed out
	 */
	public static final int ZOOM_OUT = +1;
	/**
	 * A constant amount by which can be zoomed in
	 */
	public static final int ZOOM_IN = -1;
	
	/**
	 * KeyboardZoomAdapter constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
	public KeyboardZoomAdapter(GLState state) {
		this.state = state;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch (key) {
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_PLUS:
			state.zoom(KeyboardZoomAdapter.ZOOM_IN);
			break;
		case KeyEvent.VK_SUBTRACT:
		case KeyEvent.VK_MINUS:
			state.zoom(KeyboardZoomAdapter.ZOOM_OUT);
			break;
		}
    }
	

}
