package sep.gaia.state;

import sep.gaia.util.BoundingBox;
import sep.gaia.util.IntegerBoundingBox;
import sep.gaia.util.IntegerVector3D;
import sep.gaia.util.Vector3D;

/**
 * 
 * Class to represent the current state of the earth in tile coordinates.
 * 
 * @author Max Witzelsperger (Implementation: Fabian Buske)
 * 
 */
public class TileState extends State {

	/**
	 * The maximal zoom level the application allows.
	 */
	public static final int MAX_ZOOM = 15;

	/**
	 * The general size of a tile in pixels.
	 */
	public static final int TILE_SIZE = 256;

	/**
	 * The current zoom level which is in a range of 0-15.
	 */
	private int zoom;

	/**
	 * The current bounding box.
	 */
	private IntegerBoundingBox boundingBox;

	/**
	 * The current center, represented as a 3-dimensional vector of which which
	 * only the first 2 coordinates are used and third coordinate (the
	 * z-coordinate) is 0.
	 */
	private IntegerVector3D center;

	/**
	 * Generates a new <code>TileState</code>.
	 * 
	 * @param zoom
	 *            the initial zoom level
	 * @param bB
	 *            the initial bounding box which represents the perimeter of the
	 *            currently visible area
	 * @param cenX
	 *            the x-coordinate of the initial center of the depiction
	 * @param cenY
	 *            the y-coordinate of the initial center of the depiction
	 */
	public TileState(int zoom, IntegerBoundingBox bB, int cenX, int cenY) {

		this.boundingBox = bB;
		this.zoom = zoom;
		this.center = new IntegerVector3D(cenX, cenY, 0);
	}

	/**
	 * Gets the current zoom level.
	 * 
	 * @return the zoom level
	 */
	public int getZoom() {
		return this.zoom;
	}

	/**
	 * Sets the zoom level.
	 * 
	 * @param zoom
	 *            the new value for the zoom level
	 */
	public synchronized void setZoom(int zoom) {
		setZoom(zoom, true);
	}

	public synchronized void setZoom(int zoom, boolean update) {
		this.zoom = zoom;
		if (update) {
			this.notifyManager();
		}
	}

	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public IntegerVector3D getPosition() {
		return this.center;
	}

	@Override
	public synchronized void setPosition(Vector3D cen) {

		setCenter(cen, true);
	}

	public synchronized void setCenter(Vector3D cen, boolean update) {

		if (cen instanceof IntegerVector3D) {
			this.center = (IntegerVector3D) cen;
		}
		
		if (update) {
			this.notifyManager();
		}
		
		
	}

	@Override
	public IntegerBoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public synchronized void setBoundingBox(BoundingBox box) {

		setBoundingBox(box, true);
	}
	
	public synchronized void setBoundingBox(BoundingBox box, boolean update) {

		if (box instanceof IntegerBoundingBox) {
			this.boundingBox = (IntegerBoundingBox) box;
		}
		
		if (update) {
			this.notifyManager();
		}
	}

}
