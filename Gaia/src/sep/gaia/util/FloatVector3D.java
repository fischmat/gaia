package sep.gaia.util;

/**
 * Class to represent a 3-dimensional vector with <code>float</code>
 * coordinates. Instances of this class may also be used as 2-dimensional
 * vectors by following the convention of setting the <code>z</code>-coordinate
 * <code>0</code>.
 * 
 * @author Max Witzelsperger
 * 
 */
public class FloatVector3D implements Vector3D {

	private float x;

	private float y;

	private float z;

	/**
	 * Generates a new <code>FloatVector3D</code>.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @param z
	 *            the z-coordinate
	 */
	public FloatVector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Initializes the vector with the same component-values as
	 * <code>vector</code>.
	 * 
	 * @param vector
	 *            The vector which values to copy.
	 */
	public FloatVector3D(FloatVector3D vector) {
		this.x = vector.getX();
		this.y = vector.getY();
		this.z = vector.getZ();
	}

	/**
	 * Gets the x-coordinate.
	 * 
	 * @return the x-ccordinate
	 */
	public float getX() {
		return this.x;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x
	 *            the value to be set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Gets the y-coordinate.
	 * 
	 * @return the y-coordinate
	 */
	public float getY() {
		return this.y;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y
	 *            the value to be set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Gets the z-coordinate.
	 * 
	 * @return the z-coordinate
	 */
	public float getZ() {
		return this.z;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y
	 *            the value to be set
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Adds the values of the components of <code>vector</code> to the
	 * respective components.
	 * 
	 * @param vector
	 *            The vector to add.
	 */
	public void add(FloatVector3D vector) {
		this.x += vector.getX();
		this.y += vector.getY();
		this.z += vector.getZ();
	}

	/**
	 * Adds the vector <code>(x, y, z)</code> to this vector.
	 * @param x The x-value. 
	 * @param y The y-value.
	 * @param z The z-value.
	 */
	public void add(float x, float y, float z) {
		add(new FloatVector3D(x, y, z));
	}
	
	/**
	 * Subtracts the values of the components of <code>vector</code> from the
	 * respective components.
	 * 
	 * @param vector
	 *            The vector to subtract.
	 */
	public void sub(FloatVector3D vector) {
		this.x -= vector.getX();
		this.y -= vector.getY();
		this.z -= vector.getZ();
	}

	/**
	 * Subtracts the vector <code>(x, y, z)</code> from this vector.
	 * @param x The x-value. 
	 * @param y The y-value.
	 * @param z The z-value.
	 */
	public void sub(float x, float y, float z) {
		sub(new FloatVector3D(x, y, z));
	}
	

	/**
	 * Multiplies each component with <code>c</code> and stores the result.
	 * 
	 * @param c
	 *            The factor to multiply with.
	 */
	public void mul(float c) {
		x *= c;
		y *= c;
		z *= c;
	}
	
	/**
	 * Returns the dot product of this vector and <code>v</code>.
	 * @param v The vector to form the product with.
	 * @return The dot product.
	 */
	public float scalarMul(FloatVector3D v) {
		return x*v.getX() + y*v.getY() + z*v.getZ();
	}

	/**
	 * Stores the result of the modulo-division of each component by
	 * <code>c</code> in the respective component.
	 * 
	 * @param c
	 *            The number to divide each component by.
	 */
	public void modulo(float c) {
		this.x %= c;
		this.y %= c;
		this.z %= c;
	}
	
	/**
	 * Rotates <code>this</code> around the x-axis by the angle <code>deg</code>
	 * .
	 * 
	 * @param deg
	 *            the rotation angle in degree
	 */
	public void rotateAroundX(float deg) {
		double a = Math.toRadians(deg);
		float oldY = y;
		y = y * ((float) Math.cos(a)) - z * ((float) Math.sin(a));
		z = oldY * ((float) Math.sin(a)) + z * ((float) Math.cos(a));
	}
	
	/**
	 * Rotates <code>this</code> around the y-axis by the angle <code>deg</code>
	 * .
	 * 
	 * @param deg
	 *            the rotation angle in degree
	 */
	public void rotateAroundY(float deg) {
		double a = Math.toRadians(deg);
		float oldX = x;
		x = x * ((float) Math.cos(a)) + z * ((float) Math.sin(a));
		z = -oldX * ((float) Math.sin(a)) + z * ((float) Math.cos(a));
	}
	
	/**
	 * Rotates <code>this</code> around the z-axis by the angle <code>deg</code>
	 * .
	 * 
	 * @param deg
	 *            the rotation angle in degree
	 */
	public void rotateAroundZ(float deg) {
		double a = Math.toRadians(deg);
		float oldX = x;
		x = x * ((float) Math.cos(a)) - y * ((float) Math.sin(a));
		y = oldX * ((float) Math.sin(a)) + y * ((float) Math.cos(a));
	}
	
	/**
	 * Returns the length of the vector.
	 * @return The length of the vector.
	 */
	public float length() {
		float sum = (float) Math.pow(x, 2);
		sum += (float) Math.pow(y, 2);
		sum += (float) Math.pow(z, 2);
		
		return (float) Math.sqrt(sum);
	}
	
	/**
	 * Returns the vector pointing in the same direction, but with a length
	 * of one.
	 * @return The unit vector derived from this vector.
	 */
	public FloatVector3D toUnitVector() {
		float length = length();
		
		return new FloatVector3D(x/length, y/length, z/length);
	}
	
	public void applyRotation(float[][] matrix) {
		/*float newX = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z;
		float newY = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z;
		float newZ = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z;
		
		x = newX;
		y = newY;
		z = newZ;*/
		
		float[][] vector = new float[1][3];
		vector[0][0] = x;
		vector[0][1] = y;
		vector[0][2] = z;
		
		float[][] result = AlgoUtil.multMatrix(vector, matrix);
		
		x = result[0][0];
		y = result[0][1];
		z = result[0][2];
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
