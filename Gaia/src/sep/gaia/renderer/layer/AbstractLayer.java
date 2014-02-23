/**
 * 
 */
package sep.gaia.renderer.layer;

import javax.media.opengl.GL2;

/**
 * Implements the <code>DrawableLayer</code> interface and implements the
 * <code>add(DrawableLayer layer)</code> method. The <code>draw(GL2 gl)</code>
 * method is abstract, because it's the task of every concrete layer to
 * implement this method (the render process).
 * 
 * @author Johannes Bauer
 */
public abstract class AbstractLayer implements DrawableLayer {

	/**
	 * The space between two layers in GL-coordinates.
	 */
	protected static final float HEIGHT_GAP = 0.01f;
	
	/**
	 * This layer must be called in the <code>draw</code> method, after this
	 * <code>DrawableLayer</code> has finished its own render process.
	 */
	private DrawableLayer layer;

	/**
	 * This constructor is for all layers that don't need any <code>ResourceAdapter</code>.
	 */
	public AbstractLayer() {
	}

	@Override
	public abstract void draw(GL2 gl, float height);

	@Override
	public void add(DrawableLayer layer) {
		this.layer = layer;
	}

	/**
	 * Getter for the "child" layer.
	 * 
	 * @return The "child" layer if it exists, else null.
	 */
	protected DrawableLayer getNextLayer() {
		return layer;
	}

	/**
	 * 
	 * @return True, if there is another layer, else false.
	 */
	protected boolean hasNextLayer() {
		return (layer == null) ? false : true;
	}
}