package sep.gaia.renderer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import sep.gaia.state.GLState;

/**
 * This interface is used to implement different possibilities how the canvas should be rendered.
 * 
 * @author Michael Mitterer
 */
public abstract class RenderMode {
	
	protected GLState state;
	
	private static boolean lastFrame2D;

	/** 
	 * This is to draw the canvas.
	 * @param gl Set of OpenGL-calls provided.
	 */
	public void draw(GL2 gl) {
		if(state != null) {
			lastFrame2D = state.is2DMode();
		}
	}
	
	/**
	 * Called if the canvas drawn at resizes and thus the camera must be
	 * updated.
	 * @param gl The set of GL-calls available.
	 * @param width The new width of the canvas in pixel.
	 * @param height The new height of the canvas in pixel.
	 */
	public abstract void setupCamera(GL2 gl, GLU glu, float x, float y, float z, int width, int height);
	
	/**
	 * Invoked by the renderer when the draw-area was resized and at startup.
	 * @param gl The set of GL-calls available.
	 * @param x The x-position of the area.
	 * @param y The y-position of the area.
	 * @param w The width of the area.
	 * @param h The height of the area.
	 */
	public abstract void reshape(GL2 gl, int x, int y, int w, int h);

	/**
	 * Returns whether the last frame rendered was in 2D-Mode.
	 * @return Whether the last frame rendered was in 2D-Mode.
	 */
	protected static boolean wasLastFrame2D() {
		return lastFrame2D;
	}
	
}
