package sep.gaia.renderer.layer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.state.GeoState;
import sep.gaia.state.State;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * An adapter for creating the texture used to display markers as well
 * as to calculate the GL-coordinates of the markers to be drawn.
 * Instances of this class are observing the <code>MarkerResourceManager</code>
 * and are queried by the <code>MarkerLayer</code> at draw-time.
 * @author Michael Mitterer, Matthias Fisch
 *
 */
public class MarkerAdapter extends TextureAdapter<MarkerResource> {

	/**
	 * The key the texture used for markers is identified with.
	 */
	public static final String MARKER_TEXTURE_KEY = "marker";
	
	/**
	 * The side-length of a marker-icon assuming one tile per GL-unit.
	 */
	private static final float MARKER_SIDE_LEN = 0.1f;
	
	/**
	 * Creates the symbol-texture used for markers.
	 * The texture is mapped to the key <i>marker</i> and can be retrieved 
	 * via <code>getTexture()</code> after it was created.
	 */
	@Override
	protected void performGLInit(GL2 gl) {
		// Get the texture-files path from the environment:
		Environment environment = Environment.getInstance();
		String markerTexturePath = environment.getString(EnvVariable.MARKER_TEXTURE_FILE);
		
		// Schedule the textures creation and insertion into primary cache:
		try {
			scheduleTextureCreation(gl.getGLProfile(), MARKER_TEXTURE_KEY, markerTexturePath);
		
		} catch (IOException e) {
			Logger.getInstance().error("Cannot create texture for markers. "
										+ "Detailed message: " + e.getMessage());
		}
	}

	/**
	 * Converts the coordinates of all markers to GL-coordiantes by converting them
	 * and storing the result in a copies position.
	 * @param resources The markers to be processed.
	 */
	@Override
	public void onUpdate(Collection<MarkerResource> resources) {
		// Call the supertypes implementation as required:
		super.onUpdate(resources);
		
		
		// The current state is required for zoom-dependent scaling of the marker:
		GLState glState = null;
		State state = StateManager.getInstance().getState(StateType.GLState);
		
		if(state instanceof GLState) {
			glState = (GLState)state;
		}
		
		// If the current state could be retrieved:
		if(glState != null) {
			Collection<GLResource> glResources = new ArrayList<>(resources.size());
			
			// Iterate all marker-resources and generate GL-resources from them:
			for(MarkerResource marker : resources) {
				// The displayed size of a icon-side on the zoom-level:
				float symbolSideHalf = MARKER_SIDE_LEN * glState.getZoom();
				
				// Generate the bounding-box of the icon to be drawn:
				FloatVector3D upperLeft = new FloatVector3D(marker.getLon() - symbolSideHalf,
															marker.getLat() + 2 * symbolSideHalf,
															0);
				FloatVector3D lowerRight = new FloatVector3D(marker.getLon() + symbolSideHalf,
															 marker.getLat(), 
															 0);
				
				FloatBoundingBox iconBBox = new FloatBoundingBox(upperLeft, lowerRight);
				
				// Add the bounding box with the default texture-key to the resources to be drawn:
				GLResource glResource = new GLResource(MARKER_TEXTURE_KEY, iconBBox);
				glResource.setName(marker.getName());
				glResources.add(glResource);
			}
			
			// Set the generated resources as the adapters current result:
			setGLResources(glResources);
		}
	}	
	
	
}