package sep.gaia.renderer.layer;

import javax.media.opengl.GL2;

import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class CopyrightLayer extends AbstractLayer {


	private boolean adapterInitialized;
	
	private CopyrightAdapter adapter;

	/**
	 * Constructor.
	 */
	public CopyrightLayer(CopyrightAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void draw(GL2 gl, float height) {
		
		// If not done yet, invoke the adapters initialization:
		if(!adapterInitialized) {
			adapter.performGLInit(gl);
			adapterInitialized = true;
		}
		
		// Perform all the necessary OpenGL-stuff:
		adapter.performGLCalls(gl);
		
		// Update and fetch texture.
		Texture tex = adapter.getTexture(CopyrightAdapter.COPYRIGHT_TEXTURE_KEY);
		if (tex != null) {
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			tex.bind(gl);
			
			TextureCoords texCoords = tex.getImageTexCoords();
			
			FloatBoundingBox box = adapter.getDrawBox();
			FloatVector3D upperLeft = box.getUpperLeft();
			FloatVector3D upperRight = box.getUpperRight();
			FloatVector3D lowerLeft = box.getLowerLeft();
			FloatVector3D lowerRight = box.getLowerRight();
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-1, 1, 
						-1, 1, 
						-10000, 10000);
			
			gl.glDisable(GL2.GL_DEPTH_TEST);
			
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			
			// Lift the x-y-pane up:
			gl.glTranslatef(0, 0, height);
			
			// Draw texture.
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(upperLeft.getX(), upperLeft.getY(), 0.0f);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(lowerLeft.getX(), lowerLeft.getY(), 0.0f);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(lowerRight.getX(), lowerRight.getY(), 0.0f);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(upperRight.getX(), upperRight.getY(), 0.0f);
			gl.glEnd();
		}
		
		// Draw next layer.
		if (this.hasNextLayer()) {
			this.getNextLayer().draw(gl, height + HEIGHT_GAP);
		}
	}
}
