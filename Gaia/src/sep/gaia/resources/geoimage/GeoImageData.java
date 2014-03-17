package sep.gaia.resources.geoimage;

import java.io.File;

import sep.gaia.resources.DataResource;
import sep.gaia.util.FloatVector3D;

/**
 * Associates a image-file with the geographical position it was taken at.
 * @author Matthias Fisch
 *
 */
public class GeoImageData extends DataResource {

	private static final long serialVersionUID = 8226186496612806599L;

	/**
	 * The image-file which geo-position is represented by this object.
	 */
	private File imageFile;
	
	/**
	 * The location (in geo-coordinates) where the picture (<code>imageFile</code>) was taken.
	 */
	private FloatVector3D geoPosition;
	
	/**
	 * @param imageFile The image-file which geo-position is represented by this object.
	 * @param geoPosition The location (in geo-coordinates) where the picture (<code>getImageFile()</code>) was taken.
	 */
	public GeoImageData(File imageFile, FloatVector3D geoPosition) {
		super();
		this.imageFile = imageFile;
		this.geoPosition = geoPosition;
	}

	@Override
	protected long incrementTimestamp() {
		// Caching is not required for this type of data, so timestamp is also not required.
		return 0;
	}

	@Override
	public String getKey() {
		// The files absolute path, where the location is from, is unique:
		return imageFile.getAbsolutePath();
	}

	/**
	 * @return The image-file which geo-position is represented by this object.
	 */
	protected File getImageFile() {
		return imageFile;
	}

	/**
	 * @param imageFile The image-file which geo-position is represented by this object.
	 */
	protected void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * @return The location (in geo-coordinates) where the picture (<code>getImageFile()</code>) was taken.
	 */
	protected FloatVector3D getGeoPosition() {
		return geoPosition;
	}

	/**
	 * @param geoPosition The location (in geo-coordinates) where the picture (<code>getImageFile()</code>) was taken.
	 */
	protected void setGeoPosition(FloatVector3D geoPosition) {
		this.geoPosition = geoPosition;
	}
}
