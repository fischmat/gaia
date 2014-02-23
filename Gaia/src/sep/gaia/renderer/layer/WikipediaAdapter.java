package sep.gaia.renderer.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.resources.wikipedia.WikipediaData;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Logger;

/**
 * This class receives updates about new Wikipedia-POIs available and converts their
 * coordinates into GL-Coordinates. The converted resources can be used for drawing 
 * by the <code>WikipediaLayer</code> afterwards.
 * 
 * @author Matthias Fisch, Michael Mitterer
 */
public class WikipediaAdapter extends TextureAdapter<WikipediaData> {
	
	/**
	 * The key the texture used for wikipedias is identified with.
	 */
	public static final String WIKIPEDIA_TEXTURE_KEY = "wikipedia";
	
	/**
	 * The side-length of a wikipedia-icon in pixels.
	 */
	private static final int WIKIPEDIA_SIDE_LEN = 25;
	
	/**
	 * Creates the symbol-texture used for wikipedia articles.
	 * The texture is mapped to the key <i>wikipedia</i> and can be retrieved 
	 * via <code>getTexture()</code> after it was created.
	 */
	@Override
	protected void performGLInit(GL2 gl) {
		// Get the texture-files path from the environment:
		Environment environment = Environment.getInstance();
		String wikipediaTexturePath = environment.getString(EnvVariable.WIKIPEDIA_TEXTURE_FILE);
		
		// Schedule the textures creation and insertion into primary cache:
		try {
			scheduleTextureCreation(gl.getGLProfile(), WIKIPEDIA_TEXTURE_KEY, wikipediaTexturePath);
		
		} catch (IOException e) {
			Logger.getInstance().error("Cannot create texture for markers. "
										+ "Detailed message: " + e.getMessage());
		}
	}
	
	/**
	 * Converts the coordinates of all Wikipedia-POIs to GL-coordinates by converting them
	 * and storing the result in a copies position.
	 * @param resources The Wikipedia-POIs to be processed.
	 */
	@Override
	public void onUpdate(Collection<WikipediaData> resources) {
		// Call the supertypes implementation as required:
		super.onUpdate(resources);
		
		// The current state is required for zoom-dependent scaling of the marker:
		GLState glState = null;
		State state = StateManager.getInstance().getState(StateType.GLState);
		
		if(state instanceof GLState) {
			glState = (GLState)state;
		}
		
		
		// If the current state could be retrieved:
		if(glState != null && resources != null) {
			
			int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
			
			Collection<GLResource> glResources = new ArrayList<>(resources.size());
			// Iterate all poi-resources and generate GL-resources from them:
			for(WikipediaData wiki : resources) {
				
				FloatBoundingBox iconBBox = calculateSymbolBox(wiki, tileZoom);
				
				// Add the bounding box with the default texture-key to the resources to be drawn:
				GLResource glResource = new GLResource(WIKIPEDIA_TEXTURE_KEY, iconBBox);
				glResource.setName(wiki.getName());
				glResources.add(glResource);
			}
			
			// Set the generated resources as the adapters current result:
			setGLResources(glResources);
		}
	}
	
	public static FloatBoundingBox calculateSymbolBox(WikipediaData resource, int tileZoom) {
		
		// The displayed size of a icon-side on the zoom-level:
		float symbolSideHalf = AlgoUtil.glCoordsPerPixelRange(WIKIPEDIA_SIDE_LEN, tileZoom)/2;
		
		// Generate the bounding-box of the icon to be drawn:
		FloatVector3D upperLeft = new FloatVector3D(resource.getLongitude() - symbolSideHalf,
				 									resource.getLatitude() + symbolSideHalf,
													0);
		FloatVector3D lowerRight = new FloatVector3D(resource.getLongitude() + symbolSideHalf,
													 resource.getLatitude() - symbolSideHalf, 
													 0);
		return new FloatBoundingBox(upperLeft, lowerRight);
	}
}
