package sep.gaia.util;

/**
 * Utility-class to calculate intersections with the x-y-pane.
 * Uses "imaginary" rays starting from a specific position in a
 * certain angle to get the rays intersection with the pane.
 * 
 * @author Matthias Fisch
 */
public final class RayCast {
	
	private RayCast() { } // utility class constructor


	/**
	 * Calculates the intersection of a linear ray with emission point <code>from</code> and rotated
	 * in a certain way, with the x-y-pane.
	 * @param from The emission point of the ray.
	 * @param angles The rotation around the axis in degrees. E.g. the x-value describes the rotation of 
	 * the ray around the x-axis.
	 * @return The intersection-point of the ray with the x-y-pane.
	 */
	public static FloatVector3D linearCast(FloatVector3D from, FloatVector3D angles) {

		
		// Calculate the direction of the ray:
		FloatVector3D dir = new FloatVector3D(0, 0, -1);
		float[][] matrix = AlgoUtil.getRotationMatrix(angles.getX(), new FloatVector3D(1, 0, 0), AlgoUtil.IDENTITY);
		matrix = AlgoUtil.getRotationMatrix(angles.getZ(), new FloatVector3D(0, 0, 1), matrix);
		dir.applyRotation(matrix);
		
		
		// Calculate the lambda for the rays intersection with the x-y-plane:
		float lambda = -(from.getZ() / dir.getZ());
		
		// Scale the direction-vector:
		dir.mul(lambda);
		
		// The intersection is at from + dir:
		FloatVector3D intersection = new FloatVector3D(from);
		intersection.add(dir);
		
		return intersection;
	}
	
	
	
	/**
	 * Casts a ray from the position in the window plane and returns the
	 * intersection with the x-y-plane.
	 * @param windowX The x-coordinate of the window from where to cast in pixels.
	 * @param windowY The y-coordinate of the window from where to cast in pixels.
	 * @param windowWidth The width of the window in pixels.
	 * @param windowHeight The height of the window in pixels.
	 * @param viewWidth The width of the section of the projection-cube.
	 * @param viewHeight The height of the section of the projection-cube.
	 * @param position The current position on the x-y-plane. The z-coordinate is ignored.
	 * @param angle The current rotation around the x-axis in degrees.
	 * @return The intersection of the ray with the x-y-plane.
	 */
	public static FloatVector3D linearCast(int windowX, int windowY,
										   int windowWidth, int windowHeight,
										   float viewWidth, float viewHeight, 
										   FloatVector3D position, FloatVector3D angles) {
		
		// Calculate relative positions of the window-coordinates:
		float relPosX = (float)windowX / (float)windowWidth;
		float relPosY = (float)windowY / (float)windowHeight;
		
		float[][] matrix = AlgoUtil.getRotationMatrix(angles.getX(), new FloatVector3D(1, 0, 0), AlgoUtil.IDENTITY);
		matrix = AlgoUtil.getRotationMatrix(angles.getZ(), new FloatVector3D(0, 0, 1), matrix);
		
		// Calculate the upper-left corner:
		FloatVector3D from = new FloatVector3D(-viewWidth/2f + (viewWidth * relPosX), viewHeight/2f - (viewHeight * relPosY), 0);
		from.applyRotation(matrix);
		
		// Now perform a simple linear-cast from this position:
		FloatVector3D intersection = linearCast(from, angles);
		intersection.add(position.getX(), position.getY(), 0);
		
		return intersection;
	}
}
