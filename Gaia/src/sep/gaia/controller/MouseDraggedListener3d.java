package sep.gaia.controller;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import sep.gaia.state.GLState;
import sep.gaia.util.FloatVector3D;

/** 
 * This is to implement the <code>MouseListener</code> Interface and the
 * <code>MouseMotionListener</code> Interface to check if the user wants to
 * rotate the virtual earth by clicking and dragging the mouse in the desired
 * direction.
 * 
 * @author Johannes Bauer (Spezifikation: Michael Mitterer)
 */
public class MouseDraggedListener3d implements MouseMotionListener {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	
	/**
	 * The tracked x-coordinate of the mouse 
	 */
	private float mouseX = 0;
	
	/**
	 * The tracked y-coordinate of the mouse
	 */
	private float mouseY = 0;
	
	/**
	 * The scroll amount should be divided by this constant in order
	 * for the movement to be 'softer'
	 */
	private static final int ADJUSTMENT_FACTOR = 5;
	
	/**
	 * MouseDraggedListener constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
    public MouseDraggedListener3d(GLState state) {
    	this.state = state;
    }
    
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (state.is2DMode()) {
			return;
		}
		
		// current mouse coordinates
		float x = (float) e.getX();
		float y = (float) e.getY();
		
		float scrollX = (this.mouseX - x) / ADJUSTMENT_FACTOR;
		float scrollY = (this.mouseY - y) / ADJUSTMENT_FACTOR;
		
		// rotate around the amount by which the mouse has moved
		state.rotate(new FloatVector3D(scrollY, scrollX, 0));
		
		// keep track of the mouse position
		this.mouseX = x;
		this.mouseY = y;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		// keep track of the mouse
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

}
