package sep.gaia.renderer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import sep.gaia.renderer.layer.DrawableLayer;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

/**
 * The 2D <code>RenderMode</code>
 * 
 * @author Matthias Fisch (implementation)
 */
public class Mode2D extends RenderMode {

	private DrawableLayer drawableLayer;

	/**
	 * The minimal (most far away) allowed zoom level in GL-coordinates while
	 * the 2d-mode is active.
	 */
	public static final int MIN_2D_LEVEL = 5;

	/**
	 * The closest allowed zoom level in GL-coordinates while 2d-mode is active.
	 */
	public static final float MAX_2D_LEVEL = 15;

	/**
	 * Initializes the draw-mode by specifying the lowest layer to be drawn.
	 * 
	 * @param drawableLayer
	 *            The lowest layer to be drawn.
	 */
	public Mode2D(DrawableLayer drawableLayer) {
		super();
		this.drawableLayer = drawableLayer;
		state = (GLState) StateManager.getInstance().getState(
				StateType.GLState);
	}

	/**
	 * Invokes all layers to draw.
	 */
	public void draw(GL2 gl) {
		super.draw(gl);
		
		if (state != null && gl != null && drawableLayer != null) {

			GaiaCanvas canvas = GaiaCanvas.getInstance();

			// Clear the color-buffer to white:
			gl.glClearColor(0xFF, 0xFF, 0xFF, 0);
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

			// Set camera.
			float currentZoom = state.getZoom();

			setupCamera(gl, new GLU(), state.getPosition().getX(), state
					.getPosition().getY(), currentZoom, canvas.getWidth(),
					canvas.getHeight());

			// Draw all layers beginning with the lowest:
			drawableLayer.draw(gl, 0);
		}
	}

	@Override
	public void setupCamera(GL2 gl, GLU glu, float x, float y, float z,
			int width, int height) {

		if(state != null) {
			// Change to projection matrix.
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();

			gl.glViewport(0, 0, width, height);

			FloatBoundingBox bbox = state.getOrthogonalBBox();
			float glWidth = Math.abs(bbox.getUpperRight().getX()
					- bbox.getUpperLeft().getX());
			float glHeight = Math.abs(bbox.getUpperRight().getY()
					- bbox.getLowerRight().getY());

			FloatVector3D glCenter = state.getPosition();
			
			gl.glOrthof(glCenter.getX() - glWidth / 2.0f, glCenter.getX() + glWidth
					/ 2.0f, glCenter.getY() - glHeight / 2.0f, glCenter.getY()
					+ glHeight / 2.0f, -10000, 10000);
			
			gl.glTranslatef(glCenter.getX(), glCenter.getY(), 0);
			gl.glRotatef(state.getRotation().getX(), 1, 0, 0);
			gl.glRotatef(state.getRotation().getZ(), 0, 0, 1);
			gl.glTranslatef(-glCenter.getX(), -glCenter.getY(), 0);
			
			// Change back to model view matrix.
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
	}

	@Override
	public void reshape(GL2 gl, int x, int y, int w, int h) {
		if(state != null) {
			GaiaCanvas canvas = GaiaCanvas.getInstance();
			state.updateBoundingBox();
			setupCamera(gl, new GLU(), state.getPosition().getX(), state
					.getPosition().getY(), state.getZoom(), canvas.getWidth(),
					canvas.getHeight());
		}
	}

}
