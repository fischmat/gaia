package sep.gaia.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import sep.gaia.renderer.Mode2D;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.state.StateManager;
import sep.gaia.state.TileState;

/**
 * 
 * Utility class that provides converting methods between different States (such
 * as GLState, TileState, GeoState).
 * 
 * @author Johannes Bauer, Matthias Fisch
 * 
 */
public final class AlgoUtil {

	
	/**
	 * A half of the length of the whole map (i.e. the world) in GL coordinates.
	 * MAYBE TO REMOVE TODO
	 */
	private static final int HALF_MAP_LENGTH_GL = (int) Math.pow(2,
			TileState.MAX_ZOOM - 1);
	
	/**
	 * Amount of pixels a tile should have in window-coordinate-space.
	 */
	public static final float TILE_LENGTH_IN_PIXELS = 256;
	
	public static final float[][] IDENTITY = {{1, 0, 0},
											  {0, 1, 0},
											  {0, 0, 1}}; 

	private AlgoUtil() { } // utility class constructor

	/*
	 * CONVERTING VECTOR METHODS
	 */

	/**
	 * Converts a given vector of which the first two coordinates represent
	 * a position and the third one a zoom level in geographic coordinates into
	 * a vector of which the first two coordinates represent a matching 
	 * position and zoom in GL-coordinates.
	 * 
	 * @param geoVector the vector to be converted
	 * 
	 * @return the converted vector in GL-coordinates which is of the form
	 * (x-position, y-position, zoom)
	 */
	public static FloatVector3D geoToGL(FloatVector3D geoVector) {
		/*float lat = (geoVector.getX() * ((float) Math.pow(2,
				Mode2D.MAX_2D_LEVEL))) / 180.0f;
		float lon = (geoVector.getY() * ((float) Math.pow(2,
				Mode2D.MAX_2D_LEVEL))) / 180.0f;
		float z = geoVector.getZ();

		return new FloatVector3D(lat, lon, z);
		*/
		
		FloatVector3D floatTile = geoToFloatTile(geoVector);
		FloatVector3D glVector = floatTileToGL(floatTile);
		glVector.setY(-glVector.getY());
		return glVector;
	}

	/**
	 * Converts a <code>IntegerVector3D</code> object which represents a
	 * position
	 * on the map and a zoom level in tile coordinates into a
	 * <code>FloatVector3D</code> object that represents the same position and
	 * the same zoom level in GL-coordinates. The position is represented in
	 * the first two coordinates and the zoom level in the third one. Because a
	 * zoom level is always greater than or equal to 0 in tile coordinates,
	 * in the case of the given zoom level being smaller than 0
	 * <code>null</code> will be returned.
	 * 
	 * @param tileVector
	 *            the vector in tile coordinates that represents a
	 *            position on the map in the first two coordinates and a
	 *            zoom level in the third one
	 * 
	 * @return a vector in GL-coordinates that matches <code>tileVector</code>
	 *         and <code>null</code> if and only if <code>tileVector</code> is
	 *         <code>null</code> or the third coordinate of it is smaller
	 *         than 0
	 */
	public static FloatVector3D tileToGL(IntegerVector3D tileVector) {
		int tileZoom = tileVector.getZ();
		int tilesPerDirection = (int) Math.pow(2, tileZoom);
		float sideLength = (float) Math.pow(2, Mode2D.MAX_2D_LEVEL - tileZoom);

		float x = (tileVector.getX() - tilesPerDirection / 2.0f) * (sideLength);
		float y = (tileVector.getY() - tilesPerDirection / 2.0f) * sideLength;
		float z = tileToGLZoom(tileZoom);

		return new FloatVector3D(x, -y, z);
	}

	/**
	 * Converts a <code>FloatVector3D</code> object which represents a position
	 * on the map and a zoom level in GL-coordinates into an
	 * <code>IntegerVector3D</code> object that represents the same position and
	 * the same zoom level in tile coordinates. The position is represented in
	 * the first two coordinates and the zoom level in the third one. Because a
	 * zoom level is always strictly greater than 0 in the GL-representation,
	 * in the case of the given zoom level being smaller or equal to 0
	 * <code>null</code> will be returned.
	 * 
	 * @param glVector
	 *            the vector in GL-coordinates that represents a position on the
	 *            map in the first two coordinates and a zoom level in the third
	 *            one
	 * 
	 * @return a vector in tile coordinates that matches <code>glVector</code>
	 *         and <code>null</code> if and only if <code>glVector</code> is
	 *         <code>null</code> or the third coordinate of it is smaller or
	 *         equal to 0
	 */   
	public static IntegerVector3D glToTile(FloatVector3D glVector) {
		// Fetch gl coordinates.
		float glX = glVector.getX();
		float glY = glVector.getY();
		float glZ = glVector.getZ();

		// Initialize tile coordinates.
		int tileX = 0;
		int tileY = 0;
		int tileZ = glToTileZoom(glZ);

		// The gl zoom is equal to the side length of a tile in gl coords.
		float tileLengthGL = glZ;
		// The side length of the "whole earth map" in gl coords.
		float glSideLength = (int) Math.pow(2, Mode2D.MAX_2D_LEVEL);

		// Translate gl coords: eliminate negative values.
		glX += glSideLength / 2.0f;
		glY -= glSideLength / 2.0f;
		glY *= -1;

		// Prepare deltas for rounding. dX/dY is the difference to the next
		// higher tileLengthGL multiple.
		float dX = (glX % tileLengthGL) % tileLengthGL;
		float dY = (glY % tileLengthGL) % tileLengthGL;

		// Floor to next multiple if positive, else ceiling.
		glX -= dX;
		glY -= dY;

		tileX = (int) (glX / tileLengthGL);
		tileY = (int) (glY / tileLengthGL);

		// return new created tileVector
		return new IntegerVector3D(tileX, tileY, tileZ);
	}

	/**
	 * Converts a <code>FloatVector3D</code> object which represents a position
	 * on the map and a zoom level in geographic coordinates into an
	 * <code>IntegerVector3D</code> object that represents the same position and
	 * the same zoom level in tile coordinates. The position is represented in
	 * the first two coordinates and the zoom level in the third one. Because a
	 * zoom level is always strictly greater than 0 in geographic coordinates,
	 * in the case of the given zoom level being smaller or equal to 0
	 * <code>null</code> will be returned.
	 * 
	 * @param geoVector
	 *            the vector in geographic coordinates that represents a
	 *            position on the map in the first two coordinates and a
	 *            zoom level in the third one
	 *
	 * @return a vector in tile coordinates that matches <code>geoVector</code>
	 *         and <code>null</code> if and only if <code>geoVector</code> is
	 *         <code>null</code> or the third coordinate of it is smaller or
	 *         equal to 0
	 */  
	public static IntegerVector3D geoToTile(FloatVector3D geoVector) {
		float lat = geoVector.getX();
		float lon = geoVector.getY();

		// Lat in radians.
		float latRAD = (float) Math.toRadians(lat);

		// Tile zoom.
		int tileZ = glToTileZoom(geoVector.getZ());

		int tileX = (int) Math.floor((lon + 180.0f) / 360 * (1 << tileZ));
		int tileY = (int) Math.floor((1 - Math.log(Math.tan(latRAD) + 1
				/ Math.cos(latRAD))
				/ Math.PI)
				/ 2.0f * (1 << tileZ));

		return new IntegerVector3D(tileX, tileY, tileZ);
	}

	/**
	 * Converts a given vector of which the first two coordinates represent
	 * a position and the third one a zoom level in GL-coordinates into a
	 * vector of which the first two coordinates represent a matching position
	 * and zoom in geographic coordinates.
	 * 
	 * @param glVector the vector in GL-coordinates to be converted
	 * 
	 * @return a converted vector in geographic coordinates, which has the form
	 * (longitude, latitude, zoom)
	 */
	public static FloatVector3D glToGeo(FloatVector3D glVector) {
		FloatVector3D floatTileVec = glToFloatTile(glVector);
		float x = floatTileVec.getX();
		float y = floatTileVec.getY();
		float z = floatTileVec.getZ();

		float lat = (float) Math.toDegrees(Math.atan(Math.sinh(Math.PI
				- (2.0 * Math.PI * y) / Math.pow(2.0, z))));
		float lon = (float) ((x / Math.pow(2, z)) * 360.0 - 180.0);
		float zoom = glVector.getZ();

		return (new FloatVector3D(lat, lon, zoom));
	}

	/**
	 * Converts a <code>IntegerVector3D</code> object which represents a position
	 * on the map and a zoom level in tile coordinates into a
	 * <code>FloatVector3D</code> object that represents the same position and
	 * the same zoom level in geographic coordinates. The position is represented in
	 * the first two coordinates and the zoom level in the third one. Because a
	 * zoom level is always greater than or equal to 0 in tile coordinates,
	 * in the case of the given zoom level being smaller than 0
	 * <code>null</code> will be returned.
	 * 
	 * @param tileVector
	 *            the vector in tile coordinates that represents a
	 *            position on the map in the first two coordinates and a
	 *            zoom level in the third one
	 * 
	 * @return a vector in geographic coordinates that matches <code>tileVector</code>
	 *         and <code>null</code> if and only if <code>tileVector</code> is
	 *         <code>null</code> or the third coordinate of it is smaller
	 *         than 0
	 */    
	public static FloatVector3D tileToGeo(IntegerVector3D tileVector) {
		int x = tileVector.getX();
		int y = tileVector.getY();
		int zoom = tileVector.getZ();
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, zoom);

		float lon = (float) (x / Math.pow(2.0, zoom) * 360.0 - 180.0);
		float lat = (float) (Math.toDegrees(Math.atan(Math.sinh(n))));

		return new FloatVector3D(lat, lon, zoom);
	}

	// HELPER METHODS FOR CONVERTING:
	// A "float tile" is a tile coordinate with floating point amount.
	// This is needed for converting between tile and gl/geo easier.
	// The zoom of a "float tile" is equal to the "normal" tile zoom.

	public static FloatVector3D glToFloatTile(FloatVector3D glVector) {
		float glX = glVector.getX();
		float glY = glVector.getY();
		float tileLengthGL = glVector.getZ();

		// Convert the glVector in a "regular", integer tileVector.
		IntegerVector3D tileVector = glToTile(glVector);
		float x = tileVector.getX();
		float y = tileVector.getY();
		float z = tileVector.getZ();

		// Now make the tileVector "floating": means, add fraction amount to it.
		float dX = (Math.abs(glX) % tileLengthGL) / tileLengthGL;
		float dY = (Math.abs(glY) % tileLengthGL) / tileLengthGL;

		// Add the deltas.
		x += dX;
		y += dY;

		return (new FloatVector3D(x, y, z));
	}

	public static FloatVector3D glToFloatTile(FloatBoundingBox glBox) {
		FloatVector3D upperLeftGL = glBox.getUpperLeft();
		FloatVector3D lowerRightGL = glBox.getLowerRight();

		IntegerVector3D upperLeftTile = glToTile(upperLeftGL);
		IntegerVector3D lowerRightTile = glToTile(lowerRightGL);

		float centerX = upperLeftTile.getX()
				+ Math.abs(upperLeftTile.getX() - lowerRightTile.getX()) / 2.0f;
		float centerY = upperLeftTile.getY()
				+ Math.abs(upperLeftTile.getY() - lowerRightTile.getY()) / 2.0f;
		float centerZ = glToTileZoom(upperLeftGL.getZ());

		return new FloatVector3D(centerX, centerY, centerZ);
	}

	public static FloatVector3D floatTileToGL(FloatVector3D floatTile) {
		float x = floatTile.getX();
		float y = floatTile.getY();
		float z = floatTile.getZ();
		
		float glTileLength = tileToGLZoom(z);
		
		float glX = x * glTileLength - HALF_MAP_LENGTH_GL;
		float glY = y * glTileLength - HALF_MAP_LENGTH_GL;
		float glZ = tileToGLZoom(z);
		
		return (new FloatVector3D(glX, glY, glZ));
	}
	
	public static FloatVector3D geoToFloatTile(FloatVector3D geoVector) {
		float lat = geoVector.getX();
		float lon = geoVector.getY();
		float glZoom = geoVector.getZ();
		int zoom = glToTileZoom(geoVector.getZ());

		float xTile = (float) ((lon + 180.0f) / 360.0f * (1 << zoom));
		float yTile = (float) ((1 - Math.log(Math.tan(Math.toRadians(lat))
				+ 1.0f / Math.cos(Math.toRadians(lat)))
				/ Math.PI) / 2.0f * (1 << zoom));
		
		if (xTile < 0)
			xTile = 0;
		if (xTile >= (1 << zoom))
			xTile = (float) ((1 << zoom) - 1);
		if (yTile < 0)
			yTile = 0;
		if (yTile >= (1 << zoom))
			yTile = (float) ((1 << zoom) - 1);

		return new FloatVector3D(xTile, yTile, zoom);
	}

	/*
	 * CONVERTING BOUNDINGBOX METHODS
	 */

	/**
	 * Converts a bounding box in geographic coordinates where the first two
	 * coordinates of each corner vectors represent a position and the third
	 * one a zoom level into a matching bounding box in GL-coordinates.
	 * 
	 * @param geoBox the geographic bounding box to be converted, it is
	 * assumed to form a valid rectangle
	 * 
	 * @return a bounding box in GL-coordinates that matches <code>geoBox</code>
	 * or <code>null</code> if <code>geoBox</code> is <code>null</code>
	 */
	public static FloatBoundingBox geoToGL(FloatBoundingBox geoBox) {
		FloatVector3D upperLeft = geoToGL(geoBox.getUpperLeft());
		FloatVector3D upperRight = geoToGL(geoBox.getUpperRight());
		FloatVector3D lowerLeft = geoToGL(geoBox.getLowerLeft());
		FloatVector3D lowerRight = geoToGL(geoBox.getLowerRight());
		return (new FloatBoundingBox(upperLeft, upperRight, lowerLeft,
				lowerRight));
	}

	/**
	 * Converts a given bounding box in geographic coordinates (degree) into an
	 * equivalent bounding box in tile coordinates.
	 * 
	 * @param geoBox
	 *            the bounding box in geographic coordinates, which is expected
	 *            to form a valid rectangle
	 *            
	 * @return a bounding box in tile coordinates that matches 
	 *         <code>geoBox</code>, or <code>null</code> if and only if
	 *         <code>geoBox</code> is  <code>null</code>
	 */
	public static IntegerBoundingBox geoToTile(FloatBoundingBox geoBox) {
		IntegerVector3D upperLeft = geoToTile(geoBox.getUpperLeft());
		IntegerVector3D lowerRight = geoToTile(geoBox.getLowerRight());
		return (new IntegerBoundingBox(upperLeft, lowerRight));
	}

	/**
	 * Converts a bounding box in GL-coordinates where the first two
	 * coordinates of each corner vectors represent a position and the third
	 * one a zoom level into a matching bounding box in geographic coordinates.
	 * 
	 * @param glBox the GL-bounding box to be converted, it is
	 * assumed to form a valid rectangle
	 * 
	 * @return a bounding box in geographic coordinates that matches
	 * <code>geoBox</code> of <code>null</code> if <code>glBox</code> is
	 * <code>null</code>
	 */
	public static FloatBoundingBox glToGeo(FloatBoundingBox glBox) {
		FloatVector3D upperLeft = glToGeo(glBox.getLowerRight());
		FloatVector3D lowerRight = glToGeo(glBox.getUpperLeft());
		return new FloatBoundingBox(upperLeft, lowerRight);
	}

	/**
	 * Converts a given bounding box in GL-coordinates into an equivalent
	 * bounding box in tile coordinates.
	 * 
	 * @param glBox
	 *            the bounding box in GL-coordinates, which is expected to be a
	 *            valid rectangle
	 * 
	 * @param glZoom
	 *            the current zoom level which is needed for the conversion
	 *            
	 * @return a bounding box in tile-coordinates that matches <code>glBox</code>,
	 *         or <code>null</code> if and only if the given bounding box is
	 *         <code>null</code>
	 */
	public static IntegerBoundingBox glToTile(FloatBoundingBox glBox, float glZoom) {
		float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
		
		FloatVector3D[] corners = glBox.getCornersClockwise();
		
		// Calculate new tile bounding box.
		for(FloatVector3D coords : corners) {
			minX = (float) Math.min(minX, coords.getX());
			minY = (float) Math.min(minY, coords.getY());
			maxX = (float) Math.max(maxX, coords.getX());
			maxY = (float) Math.max(maxY, coords.getY());
		}
		
		//float z = corners[0].getZ();
		
		// HACK!!! Zoom level must be fetched by the boundingbox! TODO
		GLState glState = (GLState) StateManager.getInstance().getState(StateType.GLState);
		float z = glState.getZoom();
		int tileZoom = glToTileZoom(z);
		
		FloatVector3D upperLeft = new FloatVector3D(minX, maxY, z);
		FloatVector3D lowerRight = new FloatVector3D(maxX, minY, z);
		
		// Now set corners of new tile bounding box and dont't let it overflow.
		int maxXTile = (int) Math.pow(2, tileZoom) - 1;
		int maxYTile = (int) Math.pow(2, tileZoom) - 1;
		
		IntegerVector3D upperLeftTile = glToTile(upperLeft);
		IntegerVector3D lowerRightTile = glToTile(lowerRight);
		
		// Watch for overflow.
		for (IntegerVector3D current : Arrays.asList(upperLeftTile, lowerRightTile)) {
			if (current.getX() > maxXTile) {
				current.setX(maxXTile);
			} else if (current.getX() < 0) {
				current.setX(0);
			}
			
			if (current.getX() > maxXTile) {
				current.setY(maxYTile);
			} else if (current.getY() < 0) {
				current.setY(0);
			}
		}
		
		return new IntegerBoundingBox(upperLeftTile, lowerRightTile);
	}

	/**
	 * Converts a given bounding box in tile coordinates into an equivalent
	 * bounding box in geographic coordinates (degree).
	 * 
	 * @param tileBox
	 *            a bounding box in tile coordinates, which is expected to be a
	 *            valid rectangle
	 * 
	 * @return a matching bounding box in geographic coordinates, or
	 *         <code>null</code> if and only if the passed bounding box is
	 *         <code>null</code>
	 */
	public static FloatBoundingBox tileToGeo(IntegerBoundingBox tileBox) {
		FloatVector3D upperLeft = tileToGeo(tileBox.getUpperLeft());
		FloatVector3D lowerRight = tileToGeo(tileBox.getLowerRight());
		return (new FloatBoundingBox(upperLeft, lowerRight));
	}

	/**
	 * Converts a bounding box given in tile coordinates into an equivalent
	 * bounding box in GL-coordinates.
	 * 
	 * @param tileBox
	 *            the bounding box in tile coordinates which is expected to be a
	 *            valid rectangle
	 * 
	 * @return the converted bounding box in GL-coordinates, or
	 *         <code>null</code> if and only if <code>null</code> is given as
	 *         bounding box
     */
	public static FloatBoundingBox tileToGL(IntegerBoundingBox tileBox) {
		FloatVector3D upperLeft = tileToGL(tileBox.getUpperLeft());
		FloatVector3D lowerRight = tileToGL(tileBox.getLowerRight());
		return (new FloatBoundingBox(upperLeft, lowerRight));
	}

	/**
	 * Converts a given 3d-integer-vector of which the first two coordinates
	 * represent a position in tile coordinates and the third one a zoom level
	 * in tile coordinates, into a bounding box in GL-coordinates of which the
	 * upper left corner matches the given vector.
	 * 
	 * @param tileCoords
	 *            a vector in tile coordinates, representing position and a zoom
	 *            level
	 * 
	 * @return a rectangle in GL-coordinates of which <code>tileCoords</code>
	 *         matches the upper left corner
	 */
	public static FloatBoundingBox tileToGLBox(IntegerVector3D tileVector) {
		float zoomGL = AlgoUtil.tileToGLZoom(tileVector.getZ());
		float zoom = tileVector.getZ();

		FloatVector3D upperLeft = tileToGL(tileVector);

		float sideLength = (float) Math.pow(2, TileState.MAX_ZOOM - zoom);

		FloatVector3D lowerRight = new FloatVector3D(upperLeft.getX()
				+ sideLength, upperLeft.getY() - sideLength, zoomGL);

		return (new FloatBoundingBox(upperLeft, lowerRight));
	}

	/*
	 * CONVERTING STATES
	 */

	/**
	 * Converts the coordinates from <code>geoState</code> into GL-coordinates
	 * and saves them in <code>glState</code>
	 * 
	 * @param geoState the state from which the data is taken
	 * @param glState the state to be updated
	 */
	public static void geoToGL(GeoState geoState, GLState glState) {
		if (geoState != null && glState != null) {
			// Convert bounding boxes:
			FloatBoundingBox bBoxGeo = geoState.getBoundingBox();

			FloatVector3D upperLeftGeo = bBoxGeo.getUpperLeft();
			FloatVector3D lowerRightGeo = bBoxGeo.getLowerRight();

			FloatVector3D upperLeftGL = geoToGL(upperLeftGeo);
			FloatVector3D lowerRightGL = geoToGL(lowerRightGeo);

			FloatBoundingBox bBoxGl = new FloatBoundingBox(upperLeftGL,
					lowerRightGL);

			glState.setBoundingBox(bBoxGl, false);

			// Calculate center:
			FloatVector3D centerGeo = geoState.getPosition();

			FloatVector3D centerGL = geoToGL(centerGeo);

			glState.setPosition(centerGL, false);

			// Convert zoom:
			float zoomGeo = geoState.getZoom();

			glState.setZoom(zoomGeo, false);// Geo-zoom identical to
			// GL-zoom
		}
	}

	/**
	 * Converts the available coordinates from <code>glState</code> into
	 * tile coordinates and updates <code>tileState</code> with those new
	 * values.
	 * 
	 * @param glState the state from which the information is taken
	 * @param tileState the state to be updated
	 */
	public static void glToTile(GLState glState, TileState tileState) {

		if (glState != null && tileState != null) {
			FloatBoundingBox bBoxGl = glState.getBoundingBox();

			// Convert bounding box flipping corners.
			FloatVector3D lowerRightGl = bBoxGl.getUpperLeft();
			FloatVector3D upperLeftGl = bBoxGl.getLowerRight();

			upperLeftGl.setZ(glState.getZoom());
			lowerRightGl.setZ(glState.getZoom());

			// Flip coordinates to fit slippy-map convention:
			IntegerVector3D upperLeftTile = glToTile(lowerRightGl);
			IntegerVector3D lowerRightTile = glToTile(upperLeftGl);

			upperLeftTile.setZ(glToTileZoom(upperLeftGl.getZ()));
			lowerRightTile.setZ(glToTileZoom(lowerRightGl.getZ()));

			IntegerBoundingBox bBoxTile = new IntegerBoundingBox(upperLeftTile,
					lowerRightTile);

			tileState.setBoundingBox(bBoxTile, false);

			// Calculate center:
			FloatVector3D centerGl = glState.getPosition();

			IntegerVector3D centerTile = glToTile(centerGl);

			tileState.setCenter(centerTile, false);

			// Convert zoom:
			float zoomGl = glState.getZoom();
			int zoomTile = glToTileZoom(zoomGl);

			tileState.setZoom(zoomTile, false);
		}
	}

	/**
	 * Converts the coordinates from <code>geoState</code> into tile coordinates
	 * and updates <code>tileState</code> with them.
	 * 
	 * @param geoState the state from which the data is taken
	 * @param tileState the state to be updated
	 */
	public static void geoToTile(GeoState geoState, TileState tileState) {
		if (geoState != null && tileState != null) {
			// Convert bounding boxes:
			FloatBoundingBox bBoxGeo = geoState.getBoundingBox();

			FloatVector3D upperLeftGeo = bBoxGeo.getUpperLeft();
			FloatVector3D lowerRightGeo = bBoxGeo.getLowerRight();

			IntegerVector3D upperLeftTile = geoToTile(upperLeftGeo);
			IntegerVector3D lowerRightTile = geoToTile(lowerRightGeo);

			IntegerBoundingBox bBoxTile = new IntegerBoundingBox(upperLeftTile,
					lowerRightTile);

			tileState.setBoundingBox(bBoxTile);

			// Calculate center:
			FloatVector3D centerGeo = geoState.getPosition();

			IntegerVector3D centerTile = geoToTile(centerGeo);

			tileState.setCenter(centerTile, false);

			// Convert zoom:
			float zoomGeo = geoState.getZoom();
			int zoomTile = glToTileZoom(zoomGeo); // Geo-zoom identical to
			// GL-zoom

			tileState.setZoom(zoomTile, false);
		}
	}

	/**
	 * Updates the given <code>GeoState</code> object by converting all the
	 * available information from <code>GLState</code> into geographic
	 * coordinates.
	 * 
	 * @param glState the state from which the data is used
	 * @param geoState the state to be updated
	 */
	public static void glToGeo(GLState glState, GeoState geoState) {
		if (glState != null && geoState != null) {
			// Convert bounding boxes:
			FloatBoundingBox bBoxGL = glState.getBoundingBox();

			FloatVector3D upperLeftGL = bBoxGL.getUpperLeft();
			FloatVector3D upperRightGL = bBoxGL.getUpperRight();
			FloatVector3D lowerLeftGL = bBoxGL.getLowerLeft();
			FloatVector3D lowerRightGL = bBoxGL.getLowerRight();

			FloatVector3D upperLeftGeo = glToGeo(upperLeftGL);
			FloatVector3D upperRightGeo = glToGeo(upperRightGL);
			FloatVector3D lowerLeftGeo = glToGeo(lowerLeftGL);
			FloatVector3D lowerRightGeo = glToGeo(lowerRightGL);

			FloatBoundingBox bBoxGeo = new FloatBoundingBox(upperLeftGeo,
					upperRightGeo, lowerLeftGeo, lowerRightGeo);

			geoState.setBoundingBox(bBoxGeo, false);

			// Calculate center:
			FloatVector3D centerGL = glState.getPosition();
			FloatVector3D centerGeo = glToGeo(centerGL);
			geoState.setCenter(centerGeo, false);

			// Adopt zoom.
			geoState.setZoom(glState.getZoom(), false);
		}
	}

	/**
	 * Converts a given positive number representing a zoom level in
	 * GL-coordinates into a zoom level for tile coordinates.
	 * 
	 * @param glZoom
	 *            the zoom level to be converted
	 * 
	 * @return a value in the range of 1-<code>TileState.MAX_ZOOM</code>
	 *         representing a zoom level in tile coordinates
	 */
	public static int glToTileZoom(float glZoom) {
		return (int) (Mode2D.MAX_2D_LEVEL - (Math.log(glZoom) / Math.log(2)));
	}

	/**
	 * Converts a given non-negative number representing a zoom level in
	 * tile coordinates into a zoom level in GL-coordinates.
	 * 
	 * @param tileZoom
	 *            the zoom level to be converted
	 * 
	 * @return a zoom level in GL-coordinates
	 */
	public static float tileToGLZoom(float tileZoom) {
		return (float) (Math.pow(2, Mode2D.MAX_2D_LEVEL - tileZoom));
	}

	public static boolean isTileBBoxSuperposed(IntegerBoundingBox a,
			IntegerBoundingBox b) {
		boolean inXRange = a.getUpperLeft().getX() >= b.getUpperLeft().getX()
				&& a.getUpperRight().getX() <= b.getUpperRight().getX();

		boolean inYRange = a.getUpperLeft().getY() >= b.getUpperLeft().getY()
				&& a.getLowerLeft().getY() <= b.getLowerLeft().getY();

		return inXRange && inYRange;
	}

	/**
	 * Returns the length of a vertical or horizontal range in
	 * window-coordinate-space in GL-coordinates.
	 * 
	 * @param pixels
	 *            The length of the range in pixels (window-space).
	 * @param tileZoom
	 *            The zoom-level to calculate the length for.
	 * @return The length of the range in GL-coordinates.
	 */
	public static float glCoordsPerPixelRange(int pixels, int tileZoom) {
		return (((float)pixels) / TILE_LENGTH_IN_PIXELS)
				* (float) Math.pow(2, Mode2D.MAX_2D_LEVEL - tileZoom);
	}

	public static float glCoordsPerPixelRange(int pixels,
			float canvasPixelRange, float canvasRange) {
		return ((float) pixels * (float) canvasRange / (float) canvasPixelRange);
	}

	/*
	 * ROTATING METHODS
	 */

	/*
	 * public static FloatVector3D rotateAroundX(FloatVector3D vec, float angle)
	 * { // MAYBE TODO return null; }
	 * 
	 * public static FloatVector3D rotateAroundY(FloatVector3D vec, float angle)
	 * { // MAYBE TODO return null; }
	 */

	/**
	 * Rotates the passed Vector around the Z-Axis.
	 * 
	 * @param vec
	 *            The Vector to be rotated. It won't be changed.
	 * @param angle
	 *            The angle around which the FloatVector3D <code>vec</code>will
	 *            be rotated.
	 * @return A vector rotated around <code>angle</code> which origin
	 *         coordinates where specified by <code>vec</code>.
	 */
	public static FloatVector3D rotateAroundZ(FloatVector3D vec, float angle) {
		float x = vec.getX();
		float y = vec.getY();
		float z = vec.getZ();

		float angleRAD = (float) Math.toRadians(angle);

		float newX = (float) (x * Math.cos(angleRAD) - y * Math.sin(angleRAD));
		float newY = (float) (x * Math.sin(angleRAD) + y * Math.cos(angleRAD));

		return (new FloatVector3D(newX, newY, z));
	}

	/**
	 * Takes a GL-bounding-box and returns the tile-coordinates of all those
	 * tiles that are contained fully or partially in the bounding-box.
	 * 
	 * @param bbox
	 *            The bounding-box to get tile-coordinates for.
	 * @param step
	 *            The distance checked for tiles in. This should be less or
	 *            equal to the side-length of a tile to get all tiles contained
	 *            in the box.
	 * @param tileZoom
	 *            The tile-zoom of the tiles to check for.
	 * @return The tile-coordinates of all those tiles that are contained fully
	 *         or partially in the bounding-box.
	 */
	public static Collection<IntegerVector3D> getTileCoordsInGLBBox(
			FloatBoundingBox bbox, float step, int tileZoom) {

		// Get the corners of the Bounding-box:
		FloatVector3D[] corners = bbox.getCornersClockwise();

		// Get the maximum and minimum tile-coordinates of the corners:
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (FloatVector3D corner : corners) {
			IntegerVector3D tileCoords = AlgoUtil.glToTile(corner);

			maxX = Math.max(tileCoords.getX(), maxX);
			minX = Math.min(tileCoords.getX(), minX);

			maxY = Math.max(tileCoords.getY(), maxY);
			minY = Math.min(tileCoords.getY(), minY);
		}

		/*
		 * Now a matrix is used where the entry at [x][y] is true if and only if
		 * a border of the bbox is on the tile with coordinates (minX + x, minY
		 * + y).
		 */
		boolean[][] borders = new boolean[maxX - minX][maxY - minY];

		for (int i = 0; i < corners.length; i++) {
			// Select the next corner clockwise:
			FloatVector3D from = new FloatVector3D(corners[i]);
			FloatVector3D to = new FloatVector3D(corners[(i + 1)
					% corners.length]);

			// Ignore z-coordinate:
			from.setZ(0);
			to.setZ(0);

			// Calculate the vector between the corners:
			FloatVector3D totalDiff = new FloatVector3D(to);
			totalDiff.sub(from);

			// For scaling a vector of length one is required:
			FloatVector3D unit = totalDiff.toUnitVector();

			// Now view all linear-combinations shorter than the vector between
			// corners:
			for (int j = 0; j < totalDiff.length(); j += step) {

				// Get the linear-combination with current length:
				FloatVector3D currentDiff = new FloatVector3D(unit);
				currentDiff.mul(j);

				// The stretched vector is relative to from. Undo that:
				FloatVector3D pos = new FloatVector3D(from);
				pos.add(currentDiff);

				// Conversion-algorithm requires z-coordinate to be Gl-Zoom:
				pos.setZ(tileToGLZoom(tileZoom));

				// Get coordinates of the tile where the current position is on:
				IntegerVector3D tilePos = glToTile(pos);

				// Mark the current position as border coordinates:
				borders[tilePos.getX() - minX][tilePos.getY() - minY] = true;
			}
		}

		Collection<IntegerVector3D> tileCoords = new LinkedList<>();

		boolean rotated = !borders[0][0] || !borders[maxX - minX][maxY - minY];

		// If the box is not rotated, all coordinates are contained in the box:
		for (int x = 0; x < borders.length && !rotated; x++) {
			for (int y = 0; y < borders[x].length; y++) {
				// Add the coordinate to those in the bbox:
				IntegerVector3D tilePos = new IntegerVector3D(minX + x, minY
						+ y, tileZoom);
				tileCoords.add(tilePos);
			}
		}

		/*
		 * Now we have the all tile-coordinates where the borders are. Iterate
		 * the matrix and add those border-tiles and all tile-coordinates
		 * between them:
		 */
		for (int x = 0; x < borders.length && rotated; x++) {

			Collection<IntegerVector3D> columnTileCoords = new LinkedList<>();

			// Flag whether we are between two borders in this column:
			boolean inRange = false;

			for (int y = 0; y < borders[x].length; y++) {

				// If we are not in range yet, but just hit a border:
				if (!inRange && borders[x][y]) {
					inRange = true; // Remember that
				}

				// If we are on a border or between them:
				if (inRange) {
					// Did we hit another border?
					inRange = !borders[x][y];

					// Add the coordinate to those in the bbox:
					IntegerVector3D tilePos = new IntegerVector3D(minX + x,
							minY + y, tileZoom);
					columnTileCoords.add(tilePos);
				}
			}

			// Add all found coordinates to total collection:
			tileCoords.addAll(columnTileCoords);
		}

		return tileCoords;
	}
	
	/**
	 * Calculates the distance between two geo-positions.
	 * @param from The position to start from.
	 * @param to The destination.
	 * @return The distance between <code>from</code> and <code>to</code> in kilometers.
	 */
	public static float calculateGeoDistance(FloatVector3D from, FloatVector3D to) {
	 	
		float radius = 6371f; // The earth radius in kilometers
		
		// Get differences in radians
		float dLat = (float) Math.toRadians(to.getX() - from.getX());
		float dLon = (float) Math.toRadians(to.getY() - from.getY());
		// Get latitudes in radians:
		float latFrom = (float) Math.toRadians(from.getX());
		float latTo = (float) Math.toRadians(to.getX());

		float a = (float) (Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(latFrom) * Math.cos(latTo)); 
		float c = 2 * (float)Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		float distance = radius * c;
		
		return distance;
	}
	
	public static float[][] multMatrix(float[][] m, float[][] n) {
		float[][] result = null;

		if (m[0].length == n.length) {
			int rowsInM = m.length;
			int colsInM = m[0].length;
			int colsInN = n[0].length;

			result = new float[rowsInM][colsInN];

			for (int i = 0; i < rowsInM; i++) {
				for (int j = 0; j < colsInN; j++) {
					result[i][j] = 0;
					for (int k = 0; k < colsInM; k++) {
					  result[i][j] += m[i][k] * n[k][j];
					}
				}
			}
		} else {
			int rows = m.length;
			int cols = m[0].length;

			result = new float[rows][cols];
			for (int i = 0; i < m.length; i++) {
				for (int j = 0; j < m[0].length; j++) {
					result[i][j] = 0;
				}
			}
		}
		return result;
	}
	
	public static float[][] getRotationMatrix(float deg, FloatVector3D axis, float[][] oldMatrix) {
		FloatVector3D n = axis.toUnitVector();
		
		float[][] matrix = new float[3][3];
		
		float cos = (float) Math.cos(Math.toRadians(deg));
		float sin = (float) Math.sin(Math.toRadians(deg));
		
		matrix[0][0] = (n.getX()*n.getX()) * (1 - cos) + cos;
		matrix[0][1] = (n.getX()*n.getY()) * (1 - cos) - (n.getZ() * sin);
		matrix[0][2] = (n.getX()*n.getZ()) * (1 - cos) + (n.getY() * sin);
		matrix[1][0] = (n.getY()*n.getX()) * (1 - cos) + (n.getZ() * sin);
		matrix[1][1] = (n.getY()*n.getY()) * (1 - cos) + cos;
		matrix[1][2] = (n.getY()*n.getZ()) * (1 - cos) - (n.getX() * sin);
		matrix[2][0] = (n.getX()*n.getZ()) * (1 - cos) - (n.getY() * sin);
		matrix[2][1] = (n.getY()*n.getZ()) * (1 - cos) + (n.getX() * sin);
		matrix[2][2] = (n.getZ()*n.getZ()) * (1 - cos) + cos;
		
		matrix = AlgoUtil.multMatrix(oldMatrix, matrix);
		
		return matrix;
	}
}
