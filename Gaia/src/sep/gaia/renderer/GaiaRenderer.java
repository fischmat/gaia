package sep.gaia.renderer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import sep.gaia.renderer.layer.DrawableLayer;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.state.AbstractStateManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateManager;
import sep.gaia.util.FloatVector3D;

/**
 * Being an implementation of <code>GLEventListener</code>, this object draws,
 * updates and destroys all graphical components.
 * 
 * @author Michael Mitterer (specification), Matthias Fisch (implementation)
 */
public class GaiaRenderer implements GLEventListener {

	private static final float SWITCH_MODE_THRESHOLD = 200;

	private GL2 gl;
	private GLU glu;

	/**
	 * The mode that is currently needed to be rendered in the view
	 */
	private RenderMode currentMode;

	/**
	 * The GlState
	 */
	private GLState gLState;

	/**
	 * The 2D mode reference
	 */
	private Mode2D mode2D;

	/**
	 * The 3D mode reference
	 */
	private Mode3D mode3D;

	/**
	 * The zoom-value of gLState in the last frame. Remembering this is required
	 * for determining if the mode must be switched.
	 */
	private float lastZoom;

	/**
	 * Flag if a screenshot was requested.
	 */
	private boolean requestedScreenshot;

	private static GaiaRenderer instance;

	public static GaiaRenderer getInstance(DrawableLayer ground2D) {
		if (instance == null) {
			instance = new GaiaRenderer(ground2D);
		}

		return instance;
	}

	public static GaiaRenderer getInstance() {
		if (instance == null) {
			throw new NumberFormatException(
					"GaiaRenderer wasn't initalized yet.");
		} else {
			return instance;
		}
	}

	/**
	 * Initializes the renderer. The mode the renderer is initially in depends
	 * on the zoom-level of the states which must be set when creating the
	 * renderer.
	 * 
	 * @param ground2D
	 *            The lowest layer to draw in 2D-mode. If <code>null</code> is
	 *            passed, nothing will be drawn in 2D-mode.
	 */
	private GaiaRenderer(DrawableLayer ground2D) {
		super();

		// Retrieve the state-manager and get the GL-State:
		AbstractStateManager manager = StateManager.getInstance();
		State state = manager.getState(StateType.GLState);
		// If returned state is valid:
		if (state != null && state instanceof GLState) {
			gLState = (GLState) state; // Remember the state
			lastZoom = gLState.getZoom(); // Remember the current zoom-level

			// Create the render-modes:
			mode2D = new Mode2D(ground2D);
			mode3D = new Mode3D();

			// Set mode to start in depending on zoom level:
			if (lastZoom >= SWITCH_MODE_THRESHOLD) {
				currentMode = mode3D;
			} else {
				currentMode = mode2D;
			}
		}
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		// Get the set containing all OpenGL-calls:
		GL2 gl;
		if (drawable != null) {
			gl = drawable.getGL().getGL2();
		} else {
			// If the GL-calls can not be retrieved an execution of the method
			// is futile.
			return;
		}

		/*
		 * Check if the mode used in the last frame is still valid. If it is not
		 * currentMode must be replaced by its respective counterpart:
		 */
		if (gLState != null) {
			float currentZoom = gLState.getZoom(); // Get the zoom level of the
													// state.

			// Check if 3D-mode is current and if the zoom from geo-zoom
			// (kilometers)
			// to tile-zoom and thus the mode must change:
			if (gLState.is2DMode() && currentMode != mode2D) {
				currentMode = mode2D;
				ResourceMaster.getInstance().enableAll();
			}
			if (!gLState.is2DMode() && currentMode != mode3D) {
				currentMode = mode3D;
				ResourceMaster.getInstance().disableAll();
			}

			// Remember the current zoom to determine the direction zoomed in
			// the next frame:
			lastZoom = currentZoom;
		}

		// Now draw the selected mode:
		if (currentMode != null) {
			currentMode.draw(gl);

			// Take a screenshot if requested (and save it directly to the
			// default location).
			if (requestedScreenshot) {
				Screenshot.screenshotAndSave(gl);
				requestedScreenshot = false;
			}
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Get the set containing all OpenGL-calls:
		if (drawable != null) {
			gl = drawable.getGL().getGL2();
		} else {
			// If the GL-calls can not be retrieved an execution of the method
			// is futile.
			return;
		}
		glu = new GLU();

		// Enable the depth-test and overlap objects with higher distance:
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		// Color must not be interpolated, so take the linear interpolation:
		gl.glShadeModel(GL2.GL_FLAT);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);

		// Set the color of empty area to white:
		gl.glClearColor(0xFF, 0xFF, 0xFF, 0);

		// Initially set the model-view matrix:
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		if (currentMode != null && drawable != null) {
			GL2 gl = drawable.getGL().getGL2();

			State state = StateManager.getInstance()
					.getState(StateType.GLState);

			if (state != null && state instanceof GLState) {
				GLState glState = (GLState) state;

				float glZoom = glState.getZoom();
				FloatVector3D glCenter = glState.getPosition();

				//currentMode.setupCamera(gl, glu, glCenter.getX(),
				//		glCenter.getY(), glZoom, w, h);
				
				currentMode.reshape(gl, x, y, w, h);
			}
		}
	}

	/* FOR TESTING */
	public RenderMode getMode2D() {
		return mode2D;
	}

	public Mode3D getMode3D() {
		return mode3D;
	}

	/* FOR TESTING */
	public void setMode(RenderMode mode) {
		this.currentMode = mode;
	}

	public void requestScreenshot() {
		requestedScreenshot = true;
	}
}
