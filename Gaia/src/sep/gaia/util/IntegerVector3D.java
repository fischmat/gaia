package sep.gaia.util;

/**
 * Class to implement a 3-dimensional integer vector for a variety of purposes.
 * Instances of this class may also be used as 2-dimensional vectors by using
 * only the first two coordinates <code>x</code> and <code>y</code> and setting
 * the <code>z</code>-coordinate on <code>0</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class IntegerVector3D implements Vector3D {

	private int x;
	
	private int y;
	
	private int z;

	/**
	 * Generates a new <code>IntegerVector3D</code>.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public IntegerVector3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Gets the x-coordinate.
	 * 
	 * @return the x-coordinate
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x the value to be set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y-coordinate.
	 * 
	 * @return the y-coordinate
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y the value to be set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the z-coordinate.
	 * 
	 * @return the z-coordinate
	 */
	public int getZ() {
		return this.z;
	}

	/**
	 * Sets the z-coordinate.
	 * 
	 * @param z the value to be set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IntegerVector3D) {
			IntegerVector3D compare = (IntegerVector3D) obj;
			
			return x == compare.getX() && y == compare.getY() && z == compare.getZ();
			
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
