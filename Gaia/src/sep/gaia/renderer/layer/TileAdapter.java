package sep.gaia.renderer.layer;

import java.util.Collection;
import java.util.LinkedList;

import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.IntegerVector3D;

import com.jogamp.opengl.util.texture.TextureData;

/**
 * This class receives information about all currently available tiles and
 * processes to fit the requirements of the <code>TileLayer</code>.
 * Apart from converting coordinates the creation of textures from the tile-images
 * is done here.
 * 
 * @author Johannes Bauer, Matthias Fisch
 */
public class TileAdapter extends TextureAdapter<TileResource> {

	private FloatBoundingBox getTileBoundingBox(TileResource tile) {
		IntegerVector3D upperLeft = tile.getCoord();
		return AlgoUtil.tileToGLBox(upperLeft);
	}
	
	@Override
	public void onUpdate(Collection<TileResource> resources) {
		if(resources != null) {
			// Call the method of TextureAdapter as required:
			super.onUpdate(resources);
			
			Collection<GLResource> drawableResources = new LinkedList<>();
			
			GLState glState = (GLState) StateManager.getInstance().getState(StateType.GLState);
			FloatBoundingBox glBBox = glState.getBoundingBox();
			glBBox.getLowerLeft();
			
			for(TileResource tile : resources) {
				if(!tile.isDummy()) {
					String key = tile.getKey();
					FloatBoundingBox bbox = getTileBoundingBox(tile);
					TextureData texData = tile.getTextureData();
					
					scheduleTextureCreation(key, texData);
					
					GLResource glResource = new GLResource(key, bbox);
					
					drawableResources.add(glResource);
				}
			}
			
			setGLResources(drawableResources);
		}
	}

	
	
}