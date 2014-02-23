/**
 * 
 */
package sep.gaia.renderer.layer;

import java.util.Collection;

import javax.media.opengl.GL2;

import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * For all POIs appearing on the current map section, a appropriate icon will be
 * drawn.
 * 
 * @author Johannes Bauer, Matthias Fisch
 */
public class POILayer extends AbstractLayer {

	/**
	 * The adapter used by this layer for converting coordinates of POIs and
	 * binding textures.
	 */
	private POIAdapter adapter;

	/**
	 * Flag whether the <code>adapter</code> was already initialized. This must
	 * be done once in the OpenGL-thread.
	 */
	private boolean adapterInitialized;

	/**
	 * Initializes the layer with its adapter, which used for retrieving
	 * textures and GL-coordinates of the entities to draw.
	 * 
	 * @param adapter
	 *            The adapter that this layer should use.
	 */
	public POILayer(POIAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Draws all POIs available for the current view. Also all OpenGL-specific
	 * work in the adapter must be performed here.
	 */
	@Override
	public void draw(GL2 gl, float height) {
		if (adapter != null && gl != null) {
			// Check if the adapter was already initialized.
			// This must be done from the OpenGl-thread:
			if (!adapterInitialized) {
				adapter.performGLInit(gl);
			}

			// Do further processing in the OpenGL-context:
			adapter.performGLCalls(gl);

			// We want to use textures alpha-channel:
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			

			// Iterate all markers already converted into GL-coordinates:
			Collection<GLResource> glResources = adapter.getGLResources();
			for (GLResource current : glResources) {

				Texture texture = adapter
						.getTexture(current.getKey());

				if(texture != null) {

					// The textures 2D-coordinates:
					TextureCoords texCoords = texture.getImageTexCoords();
					
					// Bind the texture for use with every marker to draw:
					texture.bind(gl);
					
					// Vertices to draw:
					FloatBoundingBox box = current.getBox();
					FloatVector3D[] vertices = box.getCornersCounterClockwise();

					// Backup the current Model-View-Matrix:
					gl.glPushMatrix();
					// Lift the x-y-pane up:
					gl.glTranslatef(0, 0, height);
					
					// Draw vertices and pass uv-coords.
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