package sep.gaia.util;

import sep.gaia.util.exception.NotABoxException;

/**
 * Class to represent the size of a rectangle by giving the coordinates
 * of the upper left corner and the lower right corner. All used 3d
 * vectors are used for 2-dimensional coordinates and have 0 as their
 * z-coordinate.
 * The coordinates are represented as integer values.
 * 
 * @author Max Witzelsperger
 *
 */
public class IntegerBoundingBox implements BoundingBox {
	
	private IntegerVector3D upperLeft = new IntegerVector3D(0, 0, 0);
			
	private IntegerVector3D lowerRight = new IntegerVector3D(0, 0, 0);
	
	/**
	 * Generates a new bounding box with <code>int</code> coordinates. The
	 * z-coordinates of all used vectors will be ignored and should be 0.
	 * The given coordinates must form a valid rectangle, which is the case
	 * if and only if <code>upLeft.getX() < lowRight.getX()</code> and
	 * <code>upLeft.getY() > lowRight.getY()</code> are both <code>true</code>.
	 * Otherwise those two values will initially be <code>null</code>.
	 * 
	 * @param upLeft the coordinates of the upper left corner in form of a
	 * 3-dimensional vector of which the z-coordinate may be set as required.
	 * @param lowRight the coordinates of the lower right corner in form of a
	 * 3-dimensional vector of which the z-coordinate may be set as required.
	 * @throws NotABoxException Thrown if the passed corners do not form a rectangle.
	 */
	public IntegerBoundingBox(IntegerVector3D upLeft, IntegerVector3D lowRight) throws NotABoxException {
		this.upperLeft.setX(upLeft.getX());
		this.upperLeft.setY(upLeft.getY());
		this.upperLeft.setZ(upLeft.getZ());
		
		this.lowerRight.setX(lowRight.getX());
		this.lowerRight.setY(lowRight.getY());
		this.lowerRight.setZ(lowRight.getZ());
	}
	
	/**
	 * Checks if the coordinates set form a valid rectangle.
	 * 
	 * @return Returns <code>true</code> is the corners specified form 
	 * a valid rectangle.
	 */
	private boolean validateCoordinates() {
		if ((this.upperLeft.getX() < this.lowerRight.getX()) && (this.upperLeft.getY() < this.lowerRight.getY())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public IntegerVector3D getUpperLeft() {
		return this.upperLeft;
	}

	/**
	 * {@inheritDoc}
	 * Additionally the parameter <code>upperLeft</code> must be an instance of
	 * <code>IntegerVector3D</code>, otherwise the new value will not be set.
	 * 
	 * @param upperLeft the new coordinates for the upper left corner of
	 * <code>this</code>, which must be an instance of
	 * <code>IntegerVector3D</code> and of which the z-coordinate is assumed
	 * to be 0 and will otherwise be ignored

	 */
	public void setUpperLeft(Vector3D upperLeft) {
		this.upperLeft = (IntegerVector3D) upperLeft;		
	}

	@Override
	public IntegerVector3D getLowerRight() {
		return this.lowerRight;
	}

	/**
	 * {@inheritDoc}
	 * Additionally the parameter <code>lowerRight</code> must be an instance of
	 * <code>IntegerVector3D</code>, otherwise the new value will not be set.
	 * 
	 * @param lowerRight the new coordinates for the lower right corner of
	 * <code>this</code>, which must be an instance of
	 * <code>IntegerVector3D</code> and of which the z-coordinate is assumed
	 * to be 0 and will otherwise be ignored
	 */
	public void setLowerRight(Vector3D lowerRight) {
		this.lowerRight = (IntegerVector3D) lowerRight;
	}

	@Override
	public IntegerVector3D getUpperRight() {
		IntegerVector3D upperRight = new IntegerVector3D(this.lowerRight.getX(), this.upperLeft.getY(), this.upperLeft.getZ());
		return upperRight;
	}

	@Override
	public IntegerVector3D getLowerLeft() {
		IntegerVector3D lowerLeft = new IntegerVector3D(this.upperLeft.getX(), this.lowerRight.getY(), this.lowerRight.getZ());
		return lowerLeft;
	}	
	
	public FloatBoundingBox toFloatBox() {
		FloatVector3D upperLeftFloat = new FloatVector3D(upperLeft.getX(), upperLeft.getY(), upperLeft.getZ());
		FloatVector3D lowerRightFloat = new FloatVector3D(lowerRight.getX(), lowerRight.getY(), lowerRight.getZ());
		
		return new FloatBoundingBox(upperLeftFloat, lowerRightFloat);
	}
}