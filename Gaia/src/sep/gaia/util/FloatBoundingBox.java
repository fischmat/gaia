package sep.gaia.util;



/**
 * Class to represent the size of a rectangle by giving the coordinates
 * of the upper left corner and the lower right corner. All used 3d
 * vectors are used for 2-dimensional coordinates and have 0 as their
 * z-coordinate.
 * The coordinates are represented as float values.
 * 
 * @author Max Witzelsperger, Johannes Bauer, Matthias Fisch
 * 
 */
public class FloatBoundingBox implements BoundingBox {

	private FloatVector3D upperLeft = new FloatVector3D(0, 0, 0);
		
	private FloatVector3D lowerRight = new FloatVector3D(0, 0, 0);
	
	private FloatVector3D upperRight = null;
	
	private FloatVector3D lowerLeft = null;
	
	/**
	 * Generates a new bounding box with <code>float</code> coordinates. The
	 * z-coordinates of all used vectors will be ignored and should be 0.
	 * The given coordinates must form a valid rectangle, which is the case
	 * if and only if <code>upLeft.getX() < lowRight.getX()</code> and
	 * <code>upLeft.getY() > lowRight.getY()</code> are both <code>true</code>.
	 * Otherwise those two values will initially be <code>null</code>.
	 * 
	 * @param upLeft the coordinates of the upper left corner in form of a
	 * 3-dimensional vector of which the z-coordinate should be 0 and will
	 * otherwise be ignored.
	 * @param lowRight the coordinates of the lower right corner in form of a
	 * 3-dimensional vector of which the z-coordinate should be 0 and will
	 * otherwise be ignored.
	 */
	public FloatBoundingBox(FloatVector3D upLeft, FloatVector3D lowRight) {
		upperLeft = new FloatVector3D(upLeft);
		lowerRight = new FloatVector3D(lowRight);
		
		upperRight = new FloatVector3D(lowerRight.getX(), upperLeft.getY(), upperLeft.getZ());
		lowerLeft = new FloatVector3D(upperLeft.getX(), lowerRight.getY(), upperLeft.getZ());
	}
	
	/**
	 * Initializes the bounding-box with its corners.
	 * @param upLeft The upper-left corner.
	 * @param upRight The upper-right corner.
	 * @param lowLeft The lower-left corner.
	 * @param lowRight The lower-right corner.
	 */
	public FloatBoundingBox(FloatVector3D upLeft, FloatVector3D upRight, FloatVector3D lowLeft, FloatVector3D lowRight) {
		upperLeft = new FloatVector3D(upLeft);
		upperRight = new FloatVector3D(upRight);
		lowerLeft = new FloatVector3D(lowLeft);
		lowerRight = new FloatVector3D(lowRight);
	}

	/**
	 * Initializes the box identical to <code>box</code>.
	 * @param box The box to copy.
	 */
	public FloatBoundingBox(FloatBoundingBox box) {
		upperLeft = box.getUpperLeft();
		upperRight = box.getUpperRight();
		lowerLeft = box.getLowerLeft();
		lowerRight = box.getLowerRight();
	}
	
	@Override
	public FloatVector3D getUpperLeft() {
		return this.upperLeft;
	}

	/**
	 * {@inheritDoc}
	 * Additionally the parameter <code>upperLeft</code> must be an instance of
	 * <code>FloatVector3D</code>, otherwise the new value will not be set.
	 * 
	 * @param upperLeft the new coordinates for the upper left corner of
	 * <code>this</code>, which must be an instance of
	 * <code>FloatVector3D</code> and of which the z-coordinate is assumed
	 * to be 0 and will otherwise be ignored
	 */
	public void setUpperLeft(Vector3D upperLeft) {
		this.upperLeft = (FloatVector3D) upperLeft;
	}

	@Override
	public FloatVector3D getLowerRight() {
		return this.lowerRight;
	}
	
	/**
	 * {@inheritDoc}
	 * Additionally the parameter <code>lowerRight</code> must be an instance of
	 * <code>FloatVector3D</code>, otherwise the new value will not be set.
	 * 
	 * @param lowerRight the new coordinates for the lower right corner of
	 * <code>this</code>, which must be an instance of
	 * <code>FloatVector3D</code> and of which the z-coordinate is assumed
	 * to be 0 and will otherwise be ignored
	 */
	public void setLowerRight(Vector3D lowerRight) {
		this.lowerRight = (FloatVector3D) lowerRight;
	}

	@Override
	public FloatVector3D getUpperRight() {
		return upperRight;
	}

	@Override
	public FloatVector3D getLowerLeft() {
		return lowerLeft;
	}
	
	public FloatVector3D[] getCornersCounterClockwise() {
		FloatVector3D[] ccOut = new FloatVector3D[4];
		ccOut[0] = getUpperRight();
		ccOut[1] = getUpperLeft();
		ccOut[2] = getLowerLeft();
		ccOut[3] = getLowerRight();
		return ccOut;
	}
	
	public FloatVector3D[] getCornersClockwise() {
		FloatVector3D[] ccOut = new FloatVector3D[4];
		ccOut[0] = getUpperRight();
		ccOut[1] = getLowerRight();
		ccOut[2] = getLowerLeft();
		ccOut[3] = getUpperLeft();
		return ccOut;
	}

	public float getWidth() {
		FloatVector3D dist = new FloatVector3D(getUpperRight());
		dist.sub(upperLeft);
		return (float) Math.abs(dist.length());
	}
	
	public float getHeight() {
		FloatVector3D dist = new FloatVector3D(getLowerLeft());
		dist.sub(getUpperLeft());
		return (float) Math.abs(dist.length());
	}
	
	/**
	 * Rotates the box clockwise around <code>center</code>.
	 * The z-coordinates of the corners is ignored and will stay the same.
	 * @param center The point to rotate around.
	 * @param rotation The angle of the rotation in degrees.
	 */
	public void rotate(FloatVector3D center, float rotation) {
		
		// Make the angle positive:
		rotation = (360f - rotation)%360f;
		
		// Make copies of all corners:
		FloatVector3D upperLeft = new FloatVector3D(getUpperLeft());
		FloatVector3D upperRight = new FloatVector3D(getUpperRight());
		FloatVector3D lowerLeft = new FloatVector3D(getLowerLeft());
		FloatVector3D lowerRight = new FloatVector3D(getLowerRight());
		
		// Make the vectors relative to the origin:
		upperLeft.sub(center);
		upperRight.sub(center);
		lowerLeft.sub(center);
		lowerRight.sub(center);
		
		// Rotate vectors:
		upperLeft.rotateAroundZ(rotation);
		upperRight.rotateAroundZ(rotation);
		lowerLeft.rotateAroundZ(rotation);
		lowerRight.rotateAroundZ(rotation);
		
		// Make them relative to the center again:
		upperLeft.add(center);
		upperRight.add(center);
		lowerLeft.add(center);
		lowerRight.add(center);
		
		this.upperLeft = upperLeft;
		this.upperRight = upperRight;
		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
	}
	
	/**
	 * Checks if <code>box</code> is contained in this bounding-box.
	 * @param box The box to be checked.
	 * @return <code>true</code> if <code>box</code> is contained. Otherwise
	 * <code>false</code>.
	 */
	public boolean contains(FloatBoundingBox box) {
		
		// If the boxes are equal return immediately:
		if(equals(box)) {
			return true;
		}
		
		// Make copies of both boxes:
		FloatBoundingBox rotatedOuter = new FloatBoundingBox(this);
		FloatBoundingBox rotatedInner = new FloatBoundingBox(box);
		
		// Get the rotation between the vector from upperRight to upperLeft:
		FloatVector3D right = new FloatVector3D(1, 0, 0);
		FloatVector3D dir = new FloatVector3D(rotatedOuter.getUpperRight());
		dir.sub(rotatedOuter.getUpperLeft());
		
		float rotation = (float)(Math.acos(right.scalarMul(dir)/(right.length() * dir.length()))*180f/Math.PI);
		
		// Rotate both boxes back:
		FloatVector3D rotationCenter = rotatedOuter.getUpperLeft();
		rotatedOuter.rotate(rotationCenter, rotation);
		rotatedInner.rotate(rotationCenter, rotation);
		
		// Check coordinates by ranges:
		boolean inXRange = true, inYRange = true;
		for(FloatVector3D corner : rotatedInner.getCornersClockwise()) {
			inXRange &= corner.getX() >= rotatedOuter.getUpperLeft().getX();
			inXRange &= corner.getX() <= rotatedOuter.getUpperRight().getX();
			inYRange &= corner.getY() >= rotatedOuter.getLowerLeft().getY();
			inYRange &= corner.getY() <= rotatedOuter.getUpperLeft().getY();
		}
		
		return inXRange && inYRange;
	}
	
	/**
	 * Checks if the point <code>p</code> is contained in the box.
	 * @param p The point to be checked.
	 * @return <code>true</code> if <code>p</code> is contained. Otherwise
	 * <code>false</code>.
	 */
	public boolean contains(FloatVector3D p) {
		// Make copies of both boxes:
		FloatBoundingBox rotatedOuter = new FloatBoundingBox(this);
		
		// Get the rotation between the vector from upperRight to upperLeft:
		FloatVector3D right = new FloatVector3D(1, 0, 0);
		FloatVector3D dir = new FloatVector3D(rotatedOuter.getUpperRight());
		dir.sub(rotatedOuter.getUpperLeft());
		
		
		float rotation = (float)(Math.acos(right.scalarMul(dir)/(right.length() * dir.length()))*180f/Math.PI);
		
		FloatVector3D rotated = new FloatVector3D(p);
		rotated.sub(rotatedOuter.getUpperLeft());
		rotated.rotateAroundZ(-rotation);
		rotated.add(rotatedOuter.getUpperLeft());
		
		// Rotate both boxes back:
		FloatVector3D rotationCenter = rotatedOuter.getUpperLeft();
		rotatedOuter.rotate(rotationCenter, rotation);
		
		// Check coordinates by ranges:
		boolean inXRange = true, inYRange = true;
		inXRange &= rotated.getX() >= rotatedOuter.getUpperLeft().getX();
		inXRange &= rotated.getX() <= rotatedOuter.getUpperRight().getX();
		inYRange &= rotated.getY() >= rotatedOuter.getLowerLeft().getY();
		inYRange &= rotated.getY() <= rotatedOuter.getUpperLeft().getY();
		
		return inXRange && inYRange;
	}
	
	/**
	 * Scales the bounding-box by factor <code>c</code> assuming that
	 * <code>center</code> is its center.
	 * @param center The assumed center of the Bounding-box.
	 * @param c The factor to scale.
	 */
	public void scale(float c) {
		
		// The center of the box is at the half of its diagonal:
		FloatVector3D center = new FloatVector3D(lowerRight);
		center.sub(upperLeft);
		center.mul(0.5f);
		center.add(upperLeft);
		
		// Get the vector from center to the upper-left corner and scale it.
		// Then make it relative to origin again:
		upperLeft = new FloatVector3D(upperLeft);
		upperLeft.sub(center);
		upperLeft.mul(c);
		upperLeft.add(center);
		
		// Do the same for the other corners:
		upperRight = new FloatVector3D(upperRight);
		upperRight.sub(center);
		upperRight.mul(c);
		upperRight.add(center);
		
		lowerLeft = new FloatVector3D(lowerLeft);
		lowerLeft.sub(center);
		lowerLeft.mul(c);
		lowerLeft.add(center);
		
		lowerRight = new FloatVector3D(lowerRight);
		lowerRight.sub(center);
		lowerRight.mul(c);
		lowerRight.add(center);
	}
	
	/**
	 * Returns the non-rotated bounding-box containing this bounding-box and having
	 * minimal area.
	 * @return The minimal non-rotated bounding-box.
	 */
	public FloatBoundingBox getMinimalNonRotated() {
		float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
		
		FloatVector3D[] corners = getCornersClockwise();
		for(FloatVector3D corner : corners) {
			minX = Math.min(minX, corner.getX());
			maxX = Math.max(maxX, corner.getX());
			minY = Math.min(minY, corner.getY());
			maxY = Math.max(maxY, corner.getY());
		}
		
		float z = corners[0].getZ();
		
		return new FloatBoundingBox(new FloatVector3D(minX, maxY, z), 
									new FloatVector3D(maxX, minY, z));
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FloatBoundingBox) {
			
			boolean equal = ((FloatBoundingBox)obj).getUpperLeft().equals(getUpperLeft());
			equal &= ((FloatBoundingBox)obj).getUpperRight().equals(getUpperRight());
			equal &= ((FloatBoundingBox)obj).getLowerLeft().equals(getLowerLeft());
			equal &= ((FloatBoundingBox)obj).getLowerRight().equals(getLowerRight());
			
			return equal;
			
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + upperLeft + ", " + lowerRight + ")";
	}
}
