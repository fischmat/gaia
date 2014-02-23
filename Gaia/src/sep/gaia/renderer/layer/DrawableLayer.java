/**
 * 
 */
package sep.gaia.renderer.layer;

import javax.media.opengl.GL2;

/**
 * This interface is implemented by all layer class in this application. It is
 * used for the decorator pattern to work.
 * 
 * @author Johannes Bauer
 */
public interface DrawableLayer {

	/**
	 * The render process of this layer takes place in this method. After this
	 * layer has finished, it's calling it's calling another
	 * <code>DrawableLayer</code> to render its content (if another
	 * <code>DrawableLayer</code> is specified).
	 * 
	 * @param gl
	 *            The GL context in which the layers renders its content.
	 * @param height The logical height of this layer. If a layer <code>l2</code>
	 * should be drawn over <code>l1</code>, that are drawn via <code>l1.draw(gl, h1)</code>
	 * and <code>l2.draw(gl, h2)</code> respectively, then <code>h2</code> must be greater than 
	 * <code>h1</code>.
	 */
	public void draw(GL2 gl, float height);

	/**
	 * Let this <code>DrawableLayer</code> implementation add another
	 * <code>DrawableLayer</code> whose <code>draw</code> is called, after this
	 * <code>DrawableLayer</code> has finished its render process.
	 * 
	 * @param overlay
	 */
	public void add(DrawableLayer layer);
}