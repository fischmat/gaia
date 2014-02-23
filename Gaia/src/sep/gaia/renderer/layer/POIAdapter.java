package sep.gaia.renderer.layer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.poi.PointOfInterest;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

/**
 * This class receives updates about new POIs available and converts their
 * coordinates into GL-Coordinates. Also the respective symbol for each POI
 * is loaded and bound as a texture. The converted resources can be used for drawing 
 * by the <code>POILayer</code> afterwards.
 * 
 * @author Matthias Fisch (specification/implementation)
 */
public class POIAdapter extends TextureAdapter<PointOfInterest> {
	
	/**
	 * The OpenGL-profile to use when creating textures.
	 */
	private GLProfile profile;

	
	@Override
	protected void performGLInit(GL2 gl) {
		super.performGLInit(gl);
		profile = gl.getGLProfile();
	}

	/**
	 * Converts the coordinates of all pois to GL-coordiantes by converting them
	 * and storing the result in a copies position.
	 * @param resources The pois to be processed.
	 */
	@Override
	public void onUpdate(Collection<PointOfInterest> resources) {
		// Call the supertypes implementation as required:
		super.onUpdate(resources);
		
		// Get the resources to draw:
		Collection<GLResource> glResources = getGLResources();
		
		// The current state is required for zoom-dependent scaling of the poi:
		GLState glState = null;
		State state = StateManager.getInstance().getState(StateType.GLState);
		
		if(state instanceof GLState) {
			glState = (GLState)state;
		}
		
		// If the current state could be retrieved:
		if(glState != null) {
			
			Environment environment = Environment.getInstance();
			String poiTextureDir = environment.getString(EnvVariable.POI_TEXTURE_DIR);
			
			// Iterate all poi-resources and generate GL-resources from them:
			for(PointOfInterest poi : resources) {
				
				// For drawing, the POI must be valid and there must be a GL-profile available:
				if(!poi.isDummy() && profile != null) {
					String textureKey = "generic";
					// Schedule the texture for the POI:
					try {
						// The key is its name, URL-encoded:
						textureKey = URLEncoder.encode(poi.getCategoryKey(), "UTF-8");
						String texturePath = poiTextureDir + textureKey + ".png";
						
						// Schedule the creation in the OpenGL-thread:
						scheduleTextureCreation(profile, textureKey, texturePath);
						
					// On error, write a message to the logger:
					} catch (UnsupportedEncodingException e) {
						Logger.getInstance().error("UTF-8 not supported on this system!");
					} catch (IOException e) {
						Logger.getInstance().error("Error reading POI-texture: " + e.getMessage());
					}
					
					// The displayed size of a icon-side depending on the zoom-level:
					int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
					float symbolSideHalf = AlgoUtil.glCoordsPerPixelRange(8, tileZoom);
					
					// Convert the POIs position to GL-coordinates (The center of the drawn rectangle):
					FloatVector3D posGL = AlgoUtil.geoToGL(new FloatVector3D(poi.getLongitude(), poi.getLatitude(), glState.getZoom()));
					
					// Create the boxes corners relative to the center:
					FloatVector3D upperLeftDiff = new FloatVector3D(-symbolSideHalf, symbolSideHalf, 0);
					FloatVector3D lowerRightDiff = new FloatVector3D(symbolSideHalf, -symbolSideHalf, 0);
					
					// Generate the bounding-box of the icon to be drawn:
					FloatVector3D upperLeft = new FloatVector3D(posGL);
					upperLeft.add(upperLeftDiff);
					FloatVector3D lowerRight = new FloatVector3D(posGL);
					lowerRight.add(lowerRightDiff);
					FloatBoundingBox iconBBox = new FloatBoundingBox(upperLeft, lowerRight);
					
					// We want the drawn box to be always horizontal to the user, so rotate it back:
					iconBBox.rotate(posGL, glState.getRotation().getZ());
					
					// Add the bounding box with its texture-key to the resources to be drawn:
					GLResource glResource = new GLResource(textureKey, iconBBox);
					glResource.setName(poi.getName());
					glResources.add(glResource);
				}
			}
			
			// Set the generated resources as the adapters current result:
			setGLResources(glResources);
		}
	}	
}
