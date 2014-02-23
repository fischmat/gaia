package sep.gaia.renderer.layer;

import java.util.Collection;

import javax.media.opengl.GL2;

import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.weather.WeatherManager;
import sep.gaia.resources.weather.WeatherResource;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateObserver;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * 
 * @author Johannes Bauer
 *
 */
public class WeatherAdapter extends TextureAdapter<WeatherResource> implements StateObserver {

	// Reference to the GL state.
	private GLState glState;
	// Flag that indicates whether a new texture has to be bound.
	private boolean toBeBound = true;
	// The current bounded texture.
	private Texture currentTex;
	
	private FloatBoundingBox drawBox;
	
	// The current weather resource.
	private WeatherResource currentRes;

	@Override
	public void onUpdate(Collection<WeatherResource> resources) {
		if (resources != null && !resources.isEmpty()) {
			WeatherResource updated = resources.iterator().next();

			// Check if incoming resource is not trash.
			if (!updated.isDummy()) {
				// Check if current resource is maybe equal to old one.
				if (currentRes == null) {
					currentRes = updated;
				} else if (!currentRes.getKey().equals(updated.getKey())) {
					// The incoming resource is "new" so it has to be bound.
					toBeBound = true;
					currentRes = updated;
				}
			}
		}
	}
	
	@Override
	public void onUpdate(State state) {
		GLState glState = (GLState) state;
		this.glState = glState;
		int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
		
		if (glState.is2DMode() && tileZoom >= WeatherManager.ACTIVITY_LEVEL) {
			
			drawBox = new FloatBoundingBox(new FloatVector3D(-1, 1, 0), 
										   new FloatVector3D(-0.7f, 0.8f, 0));
		}		
	}

	/**
	 * Binds a texture if it isn't already bound and destroys the old, exisiting
	 * one.
	 * 
	 * @param gl
	 *            The OpenGL context.
	 */
	public void bindTexture(GL2 gl) {
		// Only called if a new texture has to be bound and the weather resource
		// exists.
		if (toBeBound && currentRes != null) {
			// First destroy old existing texture.
			if (currentTex != null) {
				currentTex.destroy(gl);
			}

			// Now create new texture.
			TextureData texData = currentRes.getTexture();
			if (texData != null) {
				if(currentTex != null) {
					currentTex.destroy(gl);
				}
				
				currentTex = TextureIO.newTexture(currentRes.getTexture());				
				texData.destroy();
			}
		}
	}
	
	/**
	 * 
	 * @return The current bound texture.
	 */
	public Texture getTexture() {
		return currentTex;
	}

	public FloatBoundingBox getDrawBox() {
		return drawBox;
	}

	public boolean toDraw() {
		WeatherManager weatherManager = (WeatherManager) ResourceMaster.getInstance().getResourceManager("WeatherManager");
		return AlgoUtil.glToTileZoom(glState.getZoom()) >= WeatherManager.ACTIVITY_LEVEL && weatherManager.isEnabled();
	}

	@Override
	public void onClear() { }
}
