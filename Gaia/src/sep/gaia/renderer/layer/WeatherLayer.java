
/**
 * 
 */
package sep.gaia.renderer.layer;

import javax.media.opengl.GL2;

import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * This layer draws the current weather information in a box in one corner of
 * the <code>GLCanvas</code>. The weather information is represented with some
 * "data" and a graphical icon.
 * 
 * @author Johannes Bauer
 */
public class WeatherLayer extends AbstractLayer {

	private WeatherAdapter adapter;

	public WeatherLayer(WeatherAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void draw(GL2 gl, float height) {
		// Update and fetch texture.
		if (adapter.toDraw()) {
			adapter.bindTexture(gl);
			Texture tex = adapter.getTexture();
			
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
		}
		
		// Draw next layer if available.
		if (this.hasNextLayer()) {
			this.getNextLayer().draw(gl, height + HEIGHT_GAP);			
		}
	}

}