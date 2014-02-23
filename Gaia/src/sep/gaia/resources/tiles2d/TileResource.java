/**
 * 
 */
package sep.gaia.resources.tiles2d;

import java.util.Date;

import sep.gaia.resources.DataResource;
import sep.gaia.util.IntegerVector3D;

import com.jogamp.opengl.util.texture.TextureData;

/**
 * Concrete class <code>TileResource</code>. <code>TileResource</code>
 * represents a image tile, overgiven of the <code>openstreetmap.org</code> api.
 * It is used later on for drawing these tile images on the screen in the 2D
 * Mode of GAIA.
 * 
 * Furthermore, <code>TileResource</code> contains a exact position, indicated
 * by the "tile coordinates". The tile coordinates are based upon the
 * "Slippy-Map convention".
 * 
 * @see <a
 *      href="http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames">Slippy-Map
 *      convention</a>
 * 
 *      Each tile image comes has it's specific "style" which depends on the
 *      data source, where it comes from. But the <code>TileResource</code>
 *      object don't contain this information - only the
 *      <code>TileResourceManager</code> is responsible to differ between the
 *      different styles of tiles.
 * @see <code>TileResourceManager</code>
 * @author Johannes Bauer, Matthias Fisch
 */
public class TileResource extends DataResource {
	private static final long serialVersionUID = 1833476829524121758L;

	/**
	 * Contains the image file which will be bound and drawn later on.
	 */
	private TextureData image;
	
	/**
	 * Refers to the position in "tile coordinates". Tile coordinates are
	 * described in the slippy map convention. Let (x,y,z) be the components of
	 * the <code>Vector3D</code> class, then they're semantics are as follows: x
	 * stands for the x-coordinate of the tile resource. y stands for the
	 * y-coordinate of the tile resource. z stands for the zoom coordinate,
	 * referring from the <code>TileState</code> class.
	 */
	private final IntegerVector3D tileCoord;
	
	/**
	 * The style of this tile.
	 */
	private Style style;

	/**
	 * Initializes the tile-resource with its coordinates (see SlippyMap-convention).
	 * @param tileCoord The coordinates of the tile. The z-component of the vector 
	 * defines the zoom-level.
	 * @throws IllegalArgumentException Thrown if the x- or y-coordinate is not in
	 * range [1, 2^z], whereas z is the z-component of the vector. Also the latter
	 * must be in range [1, 18].
	 */
	public TileResource(IntegerVector3D tileCoord) throws IllegalArgumentException {
		super(); // Initialize the base class (e.g. timestamp)
		
		// The maximum value the x- and y-coordinate could have at current zoom-level:
		int maxCoordValue = (int)Math.pow(2, tileCoord.getZ());
		
		// Check the validity of each parameter:
		boolean xCoordValid = tileCoord.getX() >= 0 && tileCoord.getX() <= maxCoordValue;
		boolean yCoordValid = tileCoord.getY() >= 0 && tileCoord.getY() <= maxCoordValue;
		boolean zCoordValid = tileCoord.getZ() >= 0 && tileCoord.getZ() <= 18;
		
		if(xCoordValid && yCoordValid && zCoordValid) {
			this.tileCoord = tileCoord;
		} else {
			// If any parameter was invalid, throw an exception:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Initializes the tile-resource with its coordinates (see SlippyMap-convention).
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @param zoom The zoom-level of the tile.
	 * @throws IllegalArgumentException Thrown if the x- or y-coordinate is not in
	 * range [1, 2^<code>zoom</code>]. Also <code>zoom</code> must be in range [0, 18].
	 */
	public TileResource(int x, int y, int zoom) throws IllegalArgumentException {
		super(); // Initialize the base class (e.g. timestamp)
		
		// The maximum value the x- and y-coordinate could have at current zoom-level:
		int maxCoordValue = (int)Math.pow(2, zoom);
		
		// Check the validity of each parameter:
		boolean xCoordValid = x >= 0 && x <= maxCoordValue;
		boolean yCoordValid = y >= 0 && y <= maxCoordValue;
		boolean zCoordValid = zoom >= 0	 && zoom <= 18;
		
		if(xCoordValid && yCoordValid && zCoordValid) {
			this.tileCoord = new IntegerVector3D(x, y, zoom);
		} else {
			// If any parameter was invalid, throw an exception:
			throw new IllegalArgumentException("x: " + x + "y: " + y + "zoom: " + zoom + " are invalid tile coords.");
		}
	}
	
	/**
	 * Returns the tile coordinates of this <code>TileResource</code>.
	 * @return
	 */
	public IntegerVector3D getCoord() {
		return tileCoord;
	}
	
	/**
	 * Returns the image file of this <code>TileResource</code>.
	 * @return The image file, if this <code>TileResource</code> is loaded completly.
	 * Otherwise it is null.
	 */
	public TextureData getTextureData() {
		return image;
	}
	
	/**
	 * Sets the image file of this <code>TileResource</code>.
	 * @param image The image file for this <code>TileResource</code>.
	 */
	public void setTextureData(TextureData image) {
		this.image = image;
	}
	
	
	/**
	 * Returns the style the tile is in.
	 * @return The style the tile is in.
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * Sets the style the tile is in.
	 * @param style The style the tile is in.
	 */
	public void setStyle(Style style) {
		this.style = style;
	}

	/**
	 * Returns the current UNIX-timestamp.
	 * @return The current UNIX-timestamp.
	 */
	@Override
	public long incrementTimestamp() {
		// In the case of tiles a UNIX-timestamp is used, so the next timestamp is
		// the current time:
		Date currentTime = new Date();
		return currentTime.getTime();
	}

	@Override
	public void setTimestamp(long timeStamp) {
		super.setTimestamp(timeStamp);
	}

	/**
	 * Returns the key that can be used to uniquely identify a tile.
	 * For the format of the key see <code>generateKey()</code>.
	 * @return A key to uniquely identify the tile.
	 */
	@Override
	public String getKey() {
		return generateKey(getCoord().getX(), getCoord().getY(), getCoord().getZ(), style.getLabel());
	}
	
	/**
	 * Generates a key as it would be returned by <code>getKey()</code> of a <code>TileResource</code>
	 * with the given characteristics. The key will have the following format: <i>x-y-zoom-label</i>.
	 * This method does not check the validity of the parameters.
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @param zoom The zoom-level of the tile.
	 * @param label The label of the tiles style.
	 * @return The generated key.
	 */
	public static String generateKey(int x, int y, int zoom, String label) {
		return x + "-" + y + "-" + zoom + "-" + label;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TileResource) {
			TileResource other = (TileResource) obj;
			return this.getKey().equals(other.getKey());
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		builder.append(getCoord());
		builder.append(" - ");
		builder.append(style);
		builder.append(", texture-data: ");
		builder.append(image != null);
		builder.append(", dummy: ");
		builder.append(isDummy());
		builder.append("]");
		return builder.toString();
	}
}