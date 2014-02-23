package sep.gaia.renderer.layer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * The <code>TileLayer</code> is the first layer that is drawn in GAIA. It draws
 * the 2d tiles. All other <code>DrawableLayer</code> implementations are
 * rendered after this first initial layer.
 * 
 * @author Johannes Bauer, Matthias Fisch
 */
public class TileLayer extends AbstractLayer {

	/**
	 * The adapter used by this layer for converting coordinates of tiles and
	 * binding textures.
	 */
	private final TileAdapter adapter;

	/**
	 * 
	 * @param resourceAdapter
	 *            Bind the appropiate <code>ResourceAdapter</code> to this
	 *            class.
	 */
	public TileLayer(TileAdapter adapter) {
		super();
		this.adapter = adapter;
	}

	/**
	 * Draws all tiles available for the current view. Also all OpenGL-specific
	 * work in the adapter must be performed here. To accomplish this
	 * <code>performGLCalls()</code> is called.
	 */
	@Override
	public void draw(GL2 gl, float height) {
		if (adapter != null) {

			// Create outstanding textures.
			adapter.performGLCalls(gl);

			Collection<GLResource> toDraw = adapter.getGLResources();
			for (GLResource current : toDraw) {
				// Get texture.
				Texture tex = adapter.getTexture(current.getKey());

				// Bind texture.
				if (tex != null) {
					tex.bind(gl);
					
					// Vertices to draw.
					FloatBoundingBox box = current.getBox();
					FloatVector3D[] vertices = box.getCornersCounterClockwise();
					// Tex coords.
					TextureCoords texCoords = tex.getImageTexCoords();
					
					// Backup the current Model-View-Matrix:
					gl.glPushMatrix();
					// Lift the x-y-pane up:
					gl.glTranslatef(0, 0, height);
					
					// Draw vertices and pass tex coords.
					gl.glBegin(GL2.GL_QUADS);
					gl.glTexCoord2f(texCoords.right(), texCoords.top());
					gl.glVertex3f(vertices[0].getX(), vertices[0].getY(), 0);
					gl.glTexCoord2f(texCoords.left(), texCoords.top());
					gl.glVertex3f(vertices[1].getX(), vertices[1].getY(), 0);
					gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
					gl.glVertex3f(vertices[2].getX(), vertices[2].getY(), 0);
					gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
					gl.glVertex3f(vertices[3].getX(), vertices[3].getY(), 0);
					gl.glEnd();
					
					// Restore the old matrix:
					gl.glPopMatrix();
				}
			}
		}
		
		// Draw next layer.
		if (this.hasNextLayer()) {
			this.getNextLayer().draw(gl, height + HEIGHT_GAP);
		}

	}
}