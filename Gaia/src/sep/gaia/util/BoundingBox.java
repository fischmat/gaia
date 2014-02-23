package sep.gaia.util;

/**
 * Interface for a general 2-dimensional rectangle, of which the
 * corners are represented by instances of classes that implement
 * <code>Vector3D</code>. Because the modeled rectangle is
 * 2-dimensional, the z-coordinate of each vector representing a
 * corner of this bounding box is 0.
 * 
 * @author Max Witzelsperger
 *
 */
public interface BoundingBox {

	/**
	 * Gets the upper left coordinate of <code>this</code>.
	 * 
	 * @return the upper left coordinate in form of a 3-dimensional vector
	 * of which the z-coordinate is 0
	 */
	public Vector3D getUpperLeft();

	/**
	 * Sets the upper left coordinate if the argument <code>upperLeft</code>
	 * together with the current lower right coordinate <code>lowerRight</code>
	 * of <code>this</code> forms a valid rectangle, which it does if and only
	 * if the x-coordinate of <code>upperLeft</code> is strictly smaller than
	 * the x-coordinate of <code>lowerRight</code> and the y-coordinate of
	 * <code>upperLeft</code> is strictly greater than the y-coordinate of
	 * <code>lowerRight</code>.
	 * Otherwise the invocation of this method is without effect.
	 * 
	 * @param upperLeft the new value for the upper left coordinate in form
	 * of a 3-dimensional vector of which the z-coordinate is assumed to
	 * be 0 and is otherwise ignored
	 */
	public void setUpperLeft(Vector3D upperLeft);

	/**
	 * Gets the lower right coordinate.
	 * 
	 * @return the lower right coordinate in form of a 3-dimensional vector
	 * of which the z-coordinate is 0
	 */
	public Vector3D getLowerRight();

	/**
	 * Sets the lower right coordinate if the argument <code>lowerRight</code>
	 * together with the current upper left coordinate <code>upperLeft</code>
	 * of <code>this</code> forms a valid rectangle, which it does if and only
	 * if the x-coordinate of <code>upperLeft</code> is strictly smaller than
	 * the x-coordinate of <code>lowerRight</code> and the y-coordinate of
	 * <code>upperLeft</code> is strictly greater than the y-coordinate of
	 * <code>lowerRight</code>.
	 * Otherwise the invocation of this method is without effect.
	 * 
	 * @param lowerRight the new value for the lower right coordinate in form
	 * of a 3-dimensional vector of which the z-coordinate is assumed to
	 * be 0 and is otherwise ignored
	 */
	public void setLowerRight(Vector3D lowerRight);
	
	/**
	 * Gets the upper right coordinate.
	 * 
	 * @return the upper right coordinate in form of a 3-dimensional vector
	 * of which the z-coordinate is 0
	 */
	public Vector3D getUpperRight();

	/**
	 * Gets the lower left coordinate.
	 * 
	 * @return the lower left coordinate in form of a 3-dimensional vector
	 * of which the z-coordinate is 0
	 */
	public Vector3D getLowerLeft();
	
}
