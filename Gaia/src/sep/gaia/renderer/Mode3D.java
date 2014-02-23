package sep.gaia.renderer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import sep.gaia.resources.mode3d.Credits;
import sep.gaia.resources.mode3d.Earth;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * The 3D <code>RenderMode</code>. The method <code>setUpCamera</code> must be
 * invoked initially.
 * 
 * @author Michael Mitterer (specification), Max Witzelsperger (implementation)
 */
public class Mode3D extends RenderMode {

	/**
	 * The minimal (most far away) allowed zoom level in GL-coordinates while
	 * the 3d-mode is active.
	 */
	public static final float MIN_3D_LEVEL = 2304;

	/**
	 * The closest allowed zoom level in GL-coordinates while 3d-mode is active.
	 */
	public static final float MAX_3D_LEVEL = 1024;

	/**
	 * The constant distance of the camera from the globe in the 3d-mode
	 */
	private static final float CAMERA_DISTANCE = 20;
	
	private static final int EARTH_RESOLUTION = 32;
	
	private static final int SKYDOME_RESOLUTION = 32;
	
	/**
	 * The credits if active or <code>null</code> if not.
	 */
	private Credits credits;
	
	private static final float[] IDENTITY = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
		0, 0, 1 };

	/**
	 * Holds the currently used texture for the 3d earth and its radius
	 */
	private Earth earth;

	/**
	 * Provides geometry functions
	 */
	private GLU glu;

	/**
	 * The width of the window in pixels
	 */
	private float width;

	/**
	 * The height of the window in pixels
	 */
	private float height;
	
	private FloatVector3D oldRotation = new FloatVector3D(-90, 0, 0);

	private Texture skydomeTexture;
	
	private boolean texturesCreated;

	private float[] modelViewMatrix;

	public Mode3D() {
		this.earth = new Earth(6.378f); // initial radius
		this.state = (GLState) StateManager.getInstance().getState(
				StateType.GLState);
		this.glu = new GLU();
		
		modelViewMatrix = new float[IDENTITY.length];
		System.arraycopy(IDENTITY, 0, modelViewMatrix, 0, IDENTITY.length);
	}

	private void setLights(GL2 gl) {

		// prepare light parameters
		float SHINE_ALL_DIRECTIONS = 1;
		float[] lightPos = { -40, 0, 20, SHINE_ALL_DIRECTIONS };

		// 'weak' light which is everywhere
		float[] lightColorAmbient = { 0.2f, 0.2f, 0.2f, 1f };

		// 'strong' light from a particular spot which creates a 3d-effect
		float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

		// set light parameters
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

		// enable lights
		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHTING);
	}

	private void setCamera(GL2 gl, GLU glu, float width, float height) {

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// float dist = state.getZoom() / 100f;

		// perspective
		float widthHeightRatio = width / height;
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
		glu.gluLookAt(0, 0, CAMERA_DISTANCE, 0, 0, 0, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	private void loadSkydomeTexture() {
		try {
			skydomeTexture = TextureIO.newTexture(new File("res/skydome.png"), false);
		} catch (GLException | IOException e) {
			Logger.getInstance().error("Error loading skydome-texture: " + e.getMessage());
		}
	}

	private void drawEarth(GL2 gl) {
		// set material properties
		float[] rgba = { 1f, 1f, 1f }; // neutral white surface
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

		if (!texturesCreated) {
			earth.scheduleAvailableTextures(gl.getGLProfile());
			earth.createScheduledTextures(gl);
			List<String> textureNames = earth.getAvailableTextureNames();
			if (!textureNames.isEmpty()) {
				earth.setCurrentTexture(earth.getDefaultTexture());
			}

			texturesCreated = true;
		}
		
		if(skydomeTexture == null) {
			loadSkydomeTexture();
			
		} else {
			skydomeTexture.enable(gl);
			skydomeTexture.bind(gl);
			
			// create the earth ball
			GLUquadric skydome = glu.gluNewQuadric();
			glu.gluQuadricTexture(skydome, true); // apply texture to the sphere
			glu.gluQuadricDrawStyle(skydome, GLU.GLU_FILL);
			glu.gluQuadricNormals(skydome, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(skydome, GLU.GLU_INSIDE);
			
			glu.gluSphere(skydome, this.currentRadius()*5, SKYDOME_RESOLUTION, SKYDOME_RESOLUTION);
			glu.gluDeleteQuadric(skydome);
		}
		
		if(credits != null && !wasLastFrame2D()) {
			credits.render(gl);
		} else {

			// apply the texture
			Texture texture = earth.getCurrentTexture();
			if (texture != null) {
				texture.enable(gl);
				texture.bind(gl);
			}
	
			
			
			// create the earth ball
			GLUquadric earthQuad = glu.gluNewQuadric();
			glu.gluQuadricTexture(earthQuad, true); // apply texture to the sphere
			glu.gluQuadricDrawStyle(earthQuad, GLU.GLU_FILL);
			glu.gluQuadricNormals(earthQuad, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(earthQuad, GLU.GLU_OUTSIDE);
	
			// size of sphere depends on zoom
			glu.gluSphere(earthQuad, this.currentRadius(), EARTH_RESOLUTION, EARTH_RESOLUTION);
			glu.gluDeleteQuadric(earthQuad);
		}
	}

	/*
	 * Compute the radius matching the current zoom level
	 */
	private float currentRadius() {
		float rad = this.earth.getRadius();
		float zoom = this.state.getZoom() / 100; // TODO faktor anpassen

		rad = CAMERA_DISTANCE * rad / zoom;

		return rad;
	}

	@Override
	public void draw(GL2 gl) {
		
		boolean lastFrame2D = wasLastFrame2D();
		super.draw(gl);
		
		// Clear the color-buffer to white:
		gl.glClearColor(0x0, 0x0, 0x0, 0);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		this.setCamera(gl, this.glu, this.width, this.height);
		// this.setLights(gl);

		gl.glLoadIdentity();

		if(state.is2DMode() != lastFrame2D) {
			oldRotation = new FloatVector3D(0, 0, 0);
			System.arraycopy(IDENTITY, 0, modelViewMatrix, 0, IDENTITY.length);
			gl.glRotatef(-90, 1, 0, 0);
			gl.glMultMatrixf(modelViewMatrix, 0);
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
			gl.glLoadIdentity();
		}

		
		FloatVector3D relativeRotation = new FloatVector3D(oldRotation);
		relativeRotation.sub(state.getRotation());
		
		
		// rotate the globe
		gl.glRotatef(relativeRotation.getX(), 1, 0, 0);
		gl.glRotatef(relativeRotation.getY(), 0, 1, 0);
		// compute 'old' rotation
		gl.glMultMatrixf(modelViewMatrix, 0);

		// reset the rotations
		oldRotation = new FloatVector3D(state.getRotation());

		// store the current rotation in the GL-state
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);

		this.drawEarth(gl);
	}

	@Override
	public void setupCamera(GL2 gl, GLU glu, float x, float y, float z,
			int width, int height) {

		this.width = width;
		this.height = height;
	}

	public Earth getModel() {
		return earth;
	}

	@Override
	public void reshape(GL2 gl, int x, int y, int w, int h) {
		GLState glState = (GLState) StateManager.getInstance()
				.getState(StateType.GLState);
		FloatVector3D glCenter = glState.getPosition();
		float glZoom = glState.getZoom();
		setupCamera(gl, glu, glCenter.getX(), glCenter.getY(), glZoom, w, h);

	}

	/**
	 * Sets the credits to display.
	 * @param credits The credits or <code>null</code> to deactivate them.
	 */
	public void setCredits(Credits credits) {
		this.credits = credits;
	}
}