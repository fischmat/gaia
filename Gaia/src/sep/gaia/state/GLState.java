package sep.gaia.state;

import sep.gaia.controller.KeyboardZoomAdapter;
import sep.gaia.renderer.Mode2D;
import sep.gaia.renderer.Mode3D;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.BoundingBox;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.RayCast;
import sep.gaia.util.Vector3D;

/**
 * 
 * Class to represent and manage all aspects of the state that are essential for
 * the rendering process, which includes the GL-matrices.
 * 
 * @author Matthias Fisch, Johannes Bauer, Fabian Buske
 * 
 */
public class GLState extends State {

	private boolean is2DMode;

	/**
	 * The current zoom level.
	 */
	private float glZoom;

	/**
	 * The current position and thus the center of <code>boundingBox</code>. The
	 * z-component should be 0.
	 */
	private FloatVector3D position;

	/**
	 * The current rotation around the axis. E.g. the x-component of this vector
	 * describes the rotation around the X-axis in degrees.
	 */
	private FloatVector3D rotation;

	/**
	 * The bounding box.
	 */
	private FloatBoundingBox boundingBox;

	/**
	 * Constructs a <code>GLState</code> object where the initial values for the
	 * rotations and translations are 0, and the matrices are the unite matrices
	 * and a default bounding box.
	 * 
	 * @param zoom
	 *            the initial zoom level
	 */
	public GLState(float zoom) {
		this.glZoom = zoom;

		// Initialize with non-sense values ;)
		position = new FloatVector3D(0, 0, 0);
		rotation = new FloatVector3D(0, 0, 0);

		FloatVector3D upLeft = new FloatVector3D(0, 0, 0);
		FloatVector3D lowRight = new FloatVector3D(1, 1, 0);
		boundingBox = new FloatBoundingBox(upLeft, lowRight);
	}

	/**
	 * Gets the current zoom level.
	 * 
	 * @return the zoom level
	 */
	public float getZoom() {

		return this.glZoom;
	}

	/**
	 * Sets the current zoom level.
	 * 
	 * @param zoom
	 *            The new value for the zoom level in GL-coordinates.
	 */
	public synchronized void setZoom(float zoom) {

		setZoom(zoom, true);
	}

	/**
	 * Sets the current zoom level.
	 * 
	 * @param zoom
	 *            The new value for the zoom level in GL-coordinates.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void setZoom(float zoom, boolean update) {

		this.glZoom = zoom;

		// Recalculate bounding-box on new zoom-level:
		boundingBox = calculateBoundingBox(position);
		
		if (update) {
			this.notifyManager();
		}
	}

	/**
	 * Zooms in or out by a constant amount, which is specified by the constants
	 * <code>ZOOM_IN</code> and <code>ZOOM_OUT</code>. If the argument is not
	 * one of those values, the invocation of this method will be of no effect.
	 * 
	 * @param factor
	 *            Constants <code>ZOOM_IN</code> or <code>ZOOM_OUT</code>.
	 */
	public synchronized void zoom(int factor) {
		zoom(factor, true);
	}

	/**
	 * Zooms in or out by a constant amount, which is specified by the constants
	 * <code>ZOOM_IN</code> and <code>ZOOM_OUT</code>. If the argument is not
	 * one of those values, the invocation of this method will be of no effect.
	 * 
	 * @param factor
	 *            Constants <code>ZOOM_IN</code> or <code>ZOOM_OUT</code>.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void zoom(int factor, boolean update) {

		// If in 3D-mode:
		if (is2DMode) {
			State state = StateManager.getInstance().getState(
					StateType.TileState);
			
			if (state instanceof TileState) {
				TileState tileState = (TileState) state;

				int tileZoom = tileState.getZoom();

				float newGlZoom = AlgoUtil.tileToGLZoom(tileZoom - factor);

				if (newGlZoom > AlgoUtil.tileToGLZoom(Mode2D.MIN_2D_LEVEL)) {
					// Switch to 3d mode:
					is2DMode = false;
					
					// Position in 2D- conforms to rotation in 3D-mode.
					// Convert current position in geo-coordinates:
					FloatVector3D geoPosition = AlgoUtil.glToGeo(new FloatVector3D(position.getX(), position.getY(), glZoom));

					// Set the rotation. Mind counterclockwise rotation in GL:
					rotation = new FloatVector3D(0, 0, 0);
					rotation.setX(-geoPosition.getX());
					rotation.setY(geoPosition.getY());
					
					
					setRotation(rotation, false);
					
					// Set the zoom to the nearest 3D-zoom:
					glZoom = Mode3D.MAX_3D_LEVEL;
					setPosition(new FloatVector3D(0, 0, glZoom));

				} else if (newGlZoom >= AlgoUtil
						.tileToGLZoom(Mode2D.MAX_2D_LEVEL)) {
					// Stay in 2D-mode:
					glZoom = newGlZoom;
					
					// Calculate the new bounding-box and set it:
					boundingBox  = calculateBoundingBox(new FloatVector3D(
							position.getX(), position.getY(), glZoom));
				}
			}

		} else { // If currently in 3D-mode:

			float newGlZoom = glZoom + factor
					* KeyboardZoomAdapter.ZOOM_STEP_3D;

			if (newGlZoom < Mode3D.MAX_3D_LEVEL) {
				// Switch to 2D-mode:
				is2DMode = true;

				
				// Geo-coordinates are negative in the south and west:
				float northSouthFactor = Math.signum(rotation.getX()%90);
				float eastWestFactor = -Math.signum(rotation.getY()%180);
				
				if(eastWestFactor < 0) {
					eastWestFactor *= -1;
				}
				if(northSouthFactor > 0) {
					northSouthFactor *= -1;
				}
				
				float latitude = (rotation.getX()%90) * northSouthFactor;
				float longitude = (rotation.getY()%180) * eastWestFactor;
				
				float height = AlgoUtil.tileToGLZoom(Mode2D.MIN_2D_LEVEL);
				
				FloatVector3D geoPosition = new FloatVector3D(latitude, longitude, height);
				
				position = AlgoUtil.geoToGL(geoPosition);

				// View initially orthogonal in 2D-mode:
				rotation = new FloatVector3D(0, 0, 0);

				FloatVector3D glCenter = new FloatVector3D(position.getX(),
						position.getY(), glZoom);

				boundingBox = calculateBoundingBox(glCenter);

				setPosition(glCenter, false);

				ResourceMaster.getInstance().enableAll();

			} else if (newGlZoom < Mode3D.MIN_3D_LEVEL) {
				glZoom = newGlZoom;
			}
		}

		if (update) {
			this.notifyManager();
		}
	}

	/**
	 * Calculates the GL-bounding box for centered at the current position using
	 * the current canvas-metrics and the states zoom.
	 * 
	 * @return The bounding box centered at current position (see
	 *         <code>getPosition()</code>) and fitting the current
	 *         canvas-metrics.
	 */
	public FloatBoundingBox getOrthogonalBBox() {
		return calculateOrthogonalBBox(position);
	}

	/**
	 * Calculates the GL-bounding box for centered at <code>glCenter</code>
	 * using the current canvas-metrics and the states zoom.
	 * 
	 * @param glCenter
	 *            The center of the bounding-box to calculate.
	 * @return The bounding box centered at <code>glCenter</code> and fitting
	 *         the current canvas-metrics.
	 */
	public FloatBoundingBox calculateOrthogonalBBox(FloatVector3D glCenter) {

		// Retrieve the drawing-areas metrics:
		int canvasWidth = GaiaCanvas.getInstance().getWidth();
		int canvasHeight = GaiaCanvas.getInstance().getHeight();

		// Calculate the metrics of the box:
		int tileZoom = AlgoUtil.glToTileZoom(glZoom);
		float bboxWidth = Math.abs(AlgoUtil.glCoordsPerPixelRange(canvasWidth,
				tileZoom));
		float bboxHeight = Math.abs(AlgoUtil.glCoordsPerPixelRange(
				canvasHeight, tileZoom));

		// Create the bounding-box with the center in its middle:
		float x = glCenter.getX() - bboxWidth / 2.0f;
		float y = glCenter.getY() + bboxHeight / 2.0f;
		float z = glCenter.getZ();
		FloatVector3D upperLeft = new FloatVector3D(x, y, z);
		FloatVector3D lowerRight = new FloatVector3D(x + bboxWidth, y
				- bboxHeight, z);

		return new FloatBoundingBox(upperLeft, lowerRight);
	}

	/**
	 * Calculates the bounding box on the x-y-pane taking the current rotation
	 * around the x-axis in consideration.
	 * 
	 * @param glCenter
	 *            The center of the bounding-box.
	 * @return The bounding-box scaled according to current rotation around
	 *         x-axis.
	 */
	public FloatBoundingBox calculateBoundingBox(FloatVector3D glCenter) {

		float rotAroundX = getRotation().getX();
		float rotAroundZ = getRotation().getZ();

		FloatBoundingBox orthoBBox = calculateOrthogonalBBox(glCenter);

		FloatVector3D lifting = new FloatVector3D(0, 0, 100);

		// Get the corners of the orthogonal box:
		FloatVector3D upperLeft = new FloatVector3D(orthoBBox.getUpperLeft());
		FloatVector3D upperRight = new FloatVector3D(orthoBBox.getUpperRight());
		FloatVector3D lowerLeft = new FloatVector3D(orthoBBox.getLowerLeft());
		FloatVector3D lowerRight = new FloatVector3D(orthoBBox.getLowerRight());
		
		// Make coordinates relatvie to center:
		upperLeft.sub(glCenter);
		upperRight.sub(glCenter);
		lowerLeft.sub(glCenter);
		lowerRight.sub(glCenter);
		
		// Now lift the vectors out of x-y-pane:
		upperLeft.add(lifting);
		upperRight.add(lifting);
		lowerLeft.add(lifting);
		lowerRight.add(lifting);

		// Calculate the rotation-matrix for the current-rotations:
		float[][] matrix = AlgoUtil.getRotationMatrix(rotAroundX, new FloatVector3D(1, 0, 0), AlgoUtil.IDENTITY);
		matrix = AlgoUtil.getRotationMatrix(rotAroundZ, new FloatVector3D(0, 0, 1), matrix);
		
		// Rotate the vectors around x- and z-axis:
		upperLeft.applyRotation(matrix);
		upperRight.applyRotation(matrix);
		lowerLeft.applyRotation(matrix);
		lowerRight.applyRotation(matrix);
		
		// Get the intersection of the rays starting at the moved corners:
		upperLeft = RayCast.linearCast(upperLeft, getRotation());
		upperRight = RayCast.linearCast(upperRight, getRotation());
		lowerLeft = RayCast.linearCast(lowerLeft, getRotation());
		lowerRight = RayCast.linearCast(lowerRight, getRotation());

		// Move the vectors back:
		upperLeft.add(glCenter);
		upperRight.add(glCenter);
		lowerLeft.add(glCenter);
		lowerRight.add(glCenter);
		

		// Reset the zoom stored in the z-coordinate:
		upperLeft.setZ(glZoom);
		upperRight.setZ(glZoom);
		lowerLeft.setZ(glZoom);
		lowerRight.setZ(glZoom);

		return new FloatBoundingBox(upperLeft, upperRight, lowerLeft, lowerRight);
	}

	/**
	 * Updates the bounding-box held in this state to fit the position as its
	 * center, as well as the current canvas-metrics and the current rotation.
	 */
	public synchronized void updateBoundingBox() {
		// calculate the bounding-box from current position:
		FloatBoundingBox newBBox = calculateBoundingBox(position);
		setBoundingBox(newBBox);
	}

	/**
	 * Returns the rotation-vector. The value of each component describes the
	 * rotation around the respective axis in degrees.
	 * 
	 * @return The rotation-vector.
	 */
	public FloatVector3D getRotation() {
		return new FloatVector3D(rotation);
	}

	/**
	 * Rotates the current rotation. This is done by adding rotations. To set
	 * the current rotation to a specific value use <code>setRotation()</code>.
	 * 
	 * @param rotation
	 *            The rotation to perform. Each component describes the rotation
	 *            around the respective axis in degrees.
	 */
	public synchronized void rotate(FloatVector3D rotation) {
		rotate(rotation, true);
	}

	/**
	 * Rotates the current rotation. This is done by adding rotations. To set
	 * the current rotation to a specific value use <code>setRotation()</code>.
	 * 
	 * @param rotation
	 *            The rotation to perform. Each component describes the rotation
	 *            around the respective axis in degrees.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void rotate(FloatVector3D rotation, boolean update) {
		// Add the angles:
		this.rotation.add(rotation);
		
		if(!is2DMode && rotation.getX()/90f > 1) {
			this.rotation.setX(rotation.getX()*(-1));
		}
		if(!is2DMode && rotation.getY()/180f > 1) {
			this.rotation.setY(rotation.getY()*(-1));
		}
		
		// We want to stay in range ]-360;360[
		this.rotation.modulo(360.0f);

		// The bounding-box may have changed. Update it:
		boundingBox = calculateBoundingBox(position);

		// Invoke update of other states if wished:
		if (update) {
			notifyManager();
		}
	}

	/**
	 * Sets the current rotation.
	 * 
	 * @param rotation
	 *            The rotation in degrees around the respective axis.
	 */
	public synchronized void setRotation(FloatVector3D rotation) {
		setRotation(rotation, true);
	}

	/**
	 * Sets the current rotation.
	 * 
	 * @param rotation
	 *            The rotation in degrees around the respective axis.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void setRotation(FloatVector3D rotation, boolean update) {
		// Adopt the angles:
		this.rotation.setX(rotation.getX());
		this.rotation.setY(rotation.getY());
		this.rotation.setZ(rotation.getZ());

		// The bounding-box may have changed. Update it:
		boundingBox = calculateBoundingBox(position);

		// Invoke update of other states if wished:
		if (update) {
			notifyManager();
		}
	}

	/**
	 * Sets the current position and adjust the bounding-box.
	 * 
	 * @param newPosition
	 *            The position to set.
	 */
	@Override
	public synchronized void setPosition(Vector3D newPosition) {
		if (newPosition instanceof FloatVector3D) {
			setPosition((FloatVector3D) newPosition, true);
		}
	}

	/**
	 * Sets the current position and adjust the bounding-box.
	 * 
	 * @param newPosition
	 *            The position to set.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void setPosition(FloatVector3D newPosition, boolean update) {
		// Adopt coordiantes:
		position.setX(newPosition.getX());
		position.setY(newPosition.getY());
		position.setZ(newPosition.getZ());
		
		// Recenter the current bounding-box:
		boundingBox = calculateBoundingBox(position);
		
		// Invoke update of other states if wished:
		if (update) {
			notifyManager();
		}
	}

	/**
	 * Moves the current position by <code>dist</code>. The bounding box will be
	 * adjusted to fit the new center.
	 * 
	 * @param dist
	 *            How much to move in each direction.
	 */
	public synchronized void translate(FloatVector3D dist) {
		translate(dist, true);
	}

	/**
	 * Moves the current position by <code>dist</code>. The bounding box will be
	 * adjusted to fit the new center.
	 * 
	 * @param dist
	 *            How much to move in each direction.
	 * @param update
	 *            <code>true</code> to invoke manager-update. <code>false</code>
	 *            otherwise.
	 */
	public synchronized void translate(FloatVector3D dist, boolean update) {
		// Add the raltive vector to the current position:
		position.add(dist);
		// Recenter the current bounding-box:
		boundingBox = calculateBoundingBox(position);
		
		// Invoke update of other states if wished:
		if (update) {
			notifyManager();
		}
	}

	@Override
	public boolean isFloat() {
		return true;
	}

	/**
	 * Returns the current position in GL-coordinates and thus the center of the
	 * bounding-box.
	 * 
	 * @return The current position.
	 */
	@Override
	public FloatVector3D getPosition() {
		return position;
	}

	@Override
	public FloatBoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public synchronized void setBoundingBox(BoundingBox box) {
		setBoundingBox(box, true);
	}

	/**
	 * Sets the current bounding-box and updates the current position, so that
	 * it is in the middle of the box again.
	 * @param box The box to set.
	 * @param update Indicates whether an update of all other states should be invoked.
	 */
	public synchronized void setBoundingBox(BoundingBox box, boolean update) {
		this.boundingBox = (FloatBoundingBox) box;
		
		// Update center position.
		FloatVector3D upperLeft = this.boundingBox.getUpperLeft();
		FloatVector3D lowerRight = this.boundingBox.getLowerRight();
		
		// Get the diagonal-vector:
		FloatVector3D diagonal = new FloatVector3D(lowerRight);
		diagonal.sub(upperLeft);
		// We want the center to be at the half of the diagonal:
		diagonal.mul(0.5f);

		position = new FloatVector3D(diagonal);
		// Make it relative to origin:
		position.add(upperLeft);

		if (update) {
			this.notifyManager();
		}
	}

	/**
	 * @return <code>true</code> if and only if the state is in 2D-mode.
	 */
	public boolean is2DMode() {
		return is2DMode;
	}

}
