package sep.gaia.renderer.layer;

import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;

import javax.media.opengl.GL2;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.DataResource;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateObserver;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

public class CopyrightAdapter extends TextureAdapter<DataResource> implements StateObserver {

	/**
	 * The key associated with the copyright-notes texture.
	 */
	protected static final String COPYRIGHT_TEXTURE_KEY = "osmcopy";
	

	/**
	 * The box in which the note should be drawn.
	 */
	private FloatBoundingBox drawBox;
	
	@Override
	protected void performGLInit(GL2 gl) {
		// Get the texture-files path from the environment:
		Environment environment = Environment.getInstance();
		String copyrightTexturePath = environment.getString(EnvVariable.OSM_COPYRIGHT_TEXTURE_FILE);
		
		// Schedule the textures creation and insertion into primary cache:
		try {
			scheduleTextureCreation(gl.getGLProfile(), COPYRIGHT_TEXTURE_KEY, copyrightTexturePath);
		
		} catch (IOException e) {
			Logger.getInstance().error("Cannot create texture for copyright-note. "
										+ "Detailed message: " + e.getMessage());
		}
	}



	@Override
	public void onUpdate(State state) {
		// Create the non-rotated bounding-box:
		drawBox = new FloatBoundingBox(new FloatVector3D(0.6f, 0.95f, 0), new FloatVector3D(1, 1, 0));
	}
	
	/**
	 * Returns the box in which the copyright should be drawn.
	 * @return The box in which the copyright should be drawn.
	 */
	public FloatBoundingBox getDrawBox() {
		return drawBox;
	}
}
