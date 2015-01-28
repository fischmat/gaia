package sep.gaia.state;

import sep.gaia.util.BoundingBox;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.Vector3D;

/**
 * 
 * Class to represent the state of the earth in geographic coordinates.
 * 
 * @author Max Witzelsperger (Implementation: Fabian Buske)
 *
 */
public class GeoState extends State {
	
	/**
	 * The current zoom level in kilometers.
	 */
	private float zoom;
	
	/**
	 * The bounding box.
	 */
	private FloatBoundingBox boundingBox;
	
	/**
	 * The center of the current depiction, represented as 3-dimensional vector of which
	 * the x-and y-coordinate stand for the longitude and latitude of the center and
	 * the z-coordinate is 0.
	 */
	private FloatVector3D center;

	/**
	 * Generates a new <code>GeoState</code>.
	 * 
	 * @param zoom the initial zoom level in kilometers
	 * @param cenLon the initial longitude of the center
	 * @param cenLat the initial latitude of the center
	 * @param bB the initial bounding box
	 */
	public GeoState(float zoom, float cenLon, float cenLat, 
			FloatBoundingBox bB) {
		
		this.boundingBox = new FloatBoundingBox(new FloatVector3D(0f, 0f, zoom), new FloatVector3D(0f, 0f, zoom));//bB;
		this.zoom = zoom;
		this.center = new FloatVector3D(cenLon, cenLat, 0);
	}

	/**
	 * Gets the current zoom level.
	 * 
	 * @return the zoom level
	 */
	public float getZoom() {
		return this.zoom;
	}

	/**
	 * Sets the value of the current zoom level.
	 * 
	 * @param zoom the new value for the zoom level
	 */
	public synchronized void setZoom(float zoom) {
		setZoom(zoom, true);
	}
	
	/**
	 * Sets the value of the current zoom level.
	 * 
	 * @param zoom the new value for the zoom level
	 */
	public synchronized void setZoom(float zoom, boolean update) {
		this.zoom = zoom;
		if(update) {
			this.notifyManager();
		}
	}

	@Override
	public boolean isFloat() {		
		return true;
	}

	@Override
	public FloatVector3D getPosition() {
		return this.center;
	}

	@Override
	public synchronized void setPosition(Vector3D cen) {
		setCenter(cen, true);
	}
	
	public synchronized void setCenter(Vector3D cen, boolean update) {
		if (cen instanceof FloatVector3D) {
			float distance_x = this.center.getX() - ((FloatVector3D) cen).getX();
			float distance_y = this.center.getY() - ((FloatVector3D) cen).getY();
			this.center = (FloatVector3D) cen;
			float uL_x = boundingBox.getUpperLeft().getX()+distance_x;
			float uL_y = boundingBox.getUpperLeft().getY()-distance_y;
			float lR_x = boundingBox.getUpperLeft().getX()-distance_x;
			float lR_y = boundingBox.getUpperLeft().getY()+distance_y;
			FloatVector3D uL = new FloatVector3D(uL_x, uL_y, ((FloatVector3D) cen).getZ());
			FloatVector3D lR = new FloatVector3D(lR_x, lR_y, ((FloatVector3D) cen).getZ());
			boundingBox.setUpperLeft(uL);
			boundingBox.setLowerRight(lR);
		} 
		if(update) {
			this.notifyManager();
		}
	}

	@Override
	public FloatBoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public synchronized void setBoundingBox(BoundingBox box) {
		setBoundingBox(box, true);
	}
	
	public synchronized void setBoundingBox(BoundingBox box, boolean update) {
		if (box instanceof FloatBoundingBox) {
			this.boundingBox = (FloatBoundingBox) box;
		}
		if(update) {
			this.notifyManager();
		}
	}
	
}