package sep.gaia.state;

import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.util.BoundingBox;
import sep.gaia.util.Vector3D;

/**
 * 
 * Abstract class to represent and manage the current state of the depiction of
 * the earth. The state generally consists of the coordinates of the map center
 * and of the four corners of the window. It should also save the current zoom
 * level as a numerical value. Every method of this class and inheriting classes
 * that change their internal data must invoke the <code>notifyManager</code>
 * method in order to tell their manager to update the other state
 * representations.
 * 
 * @author Max Witzelsperger
 * 
 */
public abstract class State extends StateObservable {

	/**
	 * Method to decide whether the coordinates used to manage the data of
	 * <code>this</code> are of the type <code>float</code>. If
	 * <code>false</code> is returned, the representation of the internal
	 * coordinates of <code>this</code> may be assumed as <code>int</code>.
	 * 
	 * @return <code>true</code> if and only if the coordinates saved in this
	 *         are of the type <code>float</code>
	 */
	public abstract boolean isFloat();

	/**
	 * Gets the current center in form of a 3-dimensional numerical vector of
	 * which the first two coordinates represent the center of the current view
	 * and the last one is 0.
	 * 
	 * @return the center as a vector of which the coordinate type is
	 *         <code>float</code> if and only if <code>this.isFloat()</code> is
	 *         <code>true</code> and can otherwise assumed to be
	 *         <code>int</code>
	 */
	public abstract Vector3D getPosition();

	/**
	 * Sets the center of the map in form of a 3-dimensional numerical vector,
	 * which must be an instance of <code>FloatVector3D</code> if and only if
	 * <code>this.isFloat()</code> is <code>true</code>, otherwise the
	 * invocation of this method will be without effect.
	 * 
	 * @param cen
	 *            the new value for the center as 3-dimensional vector which
	 *            must be an instance of <code>FloatVector3D</code> if
	 *            <code>this.isFloat()</code> is <code>true</code> and otherwise
	 *            must be an instance of <code>IntegerVector3D</code>, and of
	 *            which the z-coordinate should be 0 and will be ignored
	 *            otherwise
	 */
	public abstract void setPosition(Vector3D cen);

	/**
	 * Gets the current bounding box which represents the circumference of the
	 * currently depicted map.
	 * 
	 * @return the bounding box where the coordinates of the corners are of the
	 *         type <code>float</code> if and only if
	 *         <code>this.isFloat()</code> is <code>true</code>, otherwise the
	 *         coordinate type can be assumed to be <code>int</code>
	 */
	public abstract BoundingBox getBoundingBox();

	/**
	 * Sets the bounding box which represents the circumference of the currently
	 * depicted map. The given bounding box must be an instance of
	 * <code>FloatBoundingBox</code> if and only if <code>this.isFloat()</code>
	 * is <code>true</code>, otherwise the invocation of this method will have
	 * no effect.
	 * 
	 * @param box
	 *            the new bounding box for <code>this</code>, which must be an
	 *            instance of <code>FloatBoundingBox</code> if
	 *            <code>this.isFloat()</code> is <code>true</code>, and
	 *            otherwise must be an instance of
	 *            <code>IntegerBoundingBox</code>
	 */
	public abstract void setBoundingBox(BoundingBox box);

	/**
	 * Notifies the manager of <code>this</code> that <code>this</code> has
	 * changed in order to update all the other state representations.
	 */
	public void notifyManager() {
		
		if (StateManager.isInitialized()) {
			((StateManager) StateManager.getInstance()).stateChanged(this
					.type());
		}
	}

	/**
	 * Gets the enum type of <code>this</code>.
	 * 
	 * @return the type
	 */
	public StateType type() {

		if (this instanceof GeoState) {

			return StateType.GeoState;
		} else if (this instanceof GLState) {

			return StateType.GLState;
		} else {

			return StateType.TileState;
		}
	}
}