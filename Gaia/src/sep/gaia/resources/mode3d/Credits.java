package sep.gaia.resources.mode3d;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Represents the state of the credits-flow and provides functionality to render them.
 * @author Matthias Fisch
 *
 */
public class Credits {

	/**
	 * The rotation of the credits-pane around the x-axis.
	 */
	private static final float rotation = 20;
	
	/**
	 * The credit-panes width.
	 */
	public static float CREDITS_PANE_WIDTH = 10.24f;
	
	/**
	 * The credit-panes height.
	 */
	public static float CREDITS_PANE_HEIGHT = 27.65f;
	
	/**
	 * The position of the top-edge of the credits pane in z-direction.
	 */
	private float position = CREDITS_PANE_HEIGHT*0.8f;
	
	/**
	 * The texture to map on the credits-pane.
	 */
	private Texture creditsTexture;
	
	/**
	 * Creates the texture to map on the credits-pane.
	 * @return <code>true</code> if creation was successful.
	 */
	private boolean createTexture() {
		if(creditsTexture == null) {
			// Get the location of the texture-file:
			Environment environment = Environment.getInstance();
			String texFilePath = environment.getString(EnvVariable.CREDITS_TEXTURE_FILE);
			
			// Create the texture:
			try {
				creditsTexture = TextureIO.newTexture(new File(texFilePath), false);
				
			} catch (GLException | IOException e) {
				Logger.getInstance().error("The force is not with " + texFilePath);
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	public void render(GL2 gl) {
		
		// Create the credits texture if not done yet:
		if(creditsTexture == null) {
			createTexture();
		}
		
		if(creditsTexture != null) {
			// Activate alpha:
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
			// Bind the texture for use:
			creditsTexture.bind(gl);
			
			// The textures 2D-coordinates:
			TextureCoords texCoords = creditsTexture.getImageTexCoords();
			
			// Backup current matrix:
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			// Apply transformation:
			gl.glRotatef(rotation, 1, 0, 0);
			gl.glTranslatef(0, -2, position);
			gl.glRotatef(-90, 1, 0, 0);
			
			// Draw vertices and pass uv-coords.
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(CREDITS_PANE_WIDTH/2, CREDITS_PANE_HEIGHT/2, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-CREDITS_PANE_WIDTH/2, CREDITS_PANE_HEIGHT/2, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(-CREDITS_PANE_WIDTH/2, -CREDITS_PANE_HEIGHT/2, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(CREDITS_PANE_WIDTH/2, -CREDITS_PANE_HEIGHT/2, 0);
			gl.glEnd();
			
			// Restore matrix:
			gl.glPopMatrix();
		}
	}
	
	/**
	 * Translates the top-edge of the credits-pane along the z-axis.
	 * @param dir How much to translate along the z-axis.
	 */
	public void translate(float dir) {
		position += dir;
	}

	/**
	 * @return How much to translate along the z-axis.
	 */
	public float getPosition() {
		return position;
	}
}
