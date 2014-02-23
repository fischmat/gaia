package sep.gaia.resources.weather;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import sep.gaia.resources.DataResourceManager;
import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.resources.Query;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Manages the <code>WeatherResource</code> objects and is a
 * <code>StateObserver</code> by its inheritance of
 * <code>DataResourceManager</code>. WeatherManager is also a
 * <code>ResourceObservable</code> because of its inheritance of
 * <code>DataResourceManager</code>.
 * 
 * <code>WeatherManager</code> is responsible to load current weather data as
 * <code>WeatherResource</code> objects according to the current
 * <code>FloatBoundingBox</code>.
 * 
 * @author Johannes Bauer
 * 
 */
public class WeatherManager extends DataResourceManager<WeatherResource>
		implements LoaderEventListener<WeatherResource> {

	/**
	 * The minimal zoom level in tile value at which a weather
	 * manager starts to be active.
	 */
	public static final int ACTIVITY_LEVEL = 11;
	
	/**
	 * The label used to identify a WeatherManager.
	 */
	public static final String MANAGER_LABEL = "WeatherManager";

	/**
	 * Map that contains all weather status icons. The Integer number is a value
	 * like x00, where x is natural number. The enables the reference of a
	 * <code>Weather Status Code</code> to exactly one image.
	 * 
	 * @see <code>weatherConditionCode</code> in <code>WeatherResource</code>
	 */
	private Map<Integer, Image> iconMap;

	/**
	 * The current <code>WeatherResource</code> object, specified by the current
	 * position ( <code>FloatBoundingBox</code>).
	 */
	private WeatherResource currentRes;

	/**
	 * FloatBoundingBox. The coordinates for the <code>WeatherResource</code>
	 * object will be calculated according to the <code>FloatBoundingBox</code>.
	 */
	private FloatBoundingBox bbox;

	/**
	 * Reference to the loader object. The loader is responsible for delegating
	 * requested
	 */
	private Loader<Query, WeatherResource> loader;

	/**
	 * Reference to the cache.
	 */
	private WeatherCache cache;
	
	private GLCanvas canvas;

	/**
	 * The constructor creates a new weather loader and a new temporary cache.
	 * All loaded <code>WeatherResource</code> objects are hold in this cache
	 * and are deleted, if it's full.
	 */
	public WeatherManager() {
		super(MANAGER_LABEL, true, true);
		cache = new WeatherCache(this);
	}

	/**
	 * Loads weather data for the overgiven <code>WeatherResource</code> dummy.
	 * The loading procedure is done in a separate thread.
	 * 
	 * @param dummy
	 *            The <code>WeatherResource</code> dummy. This means the dummy
	 *            object has a specified geographical position, but contains not
	 *            any weather information yet.
	 */
	public void load(WeatherResource dummy) {

		// Look up in cache.
		cache.get(dummy);
		

		// Check for cache hit or miss and if manager is online.
		/*
		 * if (lookedUp == null && this.isOnline()) { // Cache miss: Generate
		 * query. Query query = new Query(new HashSet<DataResource>());
		 * query.addResource(dummy, 1);
		 * 
		 * // Create worker and "listen" to it. WeatherLoaderWorker worker = new
		 * WeatherLoaderWorker(query, this); } else { // Cache hit. currentRes =
		 * lookedUp;
		 * 
		 * // Wrap result in collection to fit interface.
		 * Collection<WeatherResource> results = new HashSet<>();
		 * results.add(currentRes); this.onResourcesAvailable(results); }
		 */
	}

	/**
	 * Returns the current used <code>WeatherResource</code> object. "Current"
	 * means, the observed state refers to a specific place (indicated by the
	 * <code>FloatBoundingBox</code> bbox member) in world and the referring
	 * <code>WeatherResource</code> object holds the weather status for this
	 * place.
	 * 
	 * @return <code>WeatherResource</code> object to the current location,
	 *         indicated by the <code>FloatBoundingBox bbox</code> member.
	 */
	public WeatherResource getCurrentWeatherResource() {
		return currentRes;
	}

	@Override
	public void onUpdate(State state) {
		// Get current position.
		GLState glState = (GLState) state;
		float glZoom = glState.getZoom();
		FloatVector3D glPosition = new FloatVector3D(glState.getPosition());
		glPosition.setZ(glZoom);
		FloatVector3D geoVec = AlgoUtil.glToGeo(glPosition);
		int tileZoom = AlgoUtil.glToTileZoom(glZoom);

		if (this.isEnabled() && tileZoom >= ACTIVITY_LEVEL) {
			WeatherResource dummy = new WeatherResource(geoVec.getX(),
					geoVec.getY(), tileZoom);
			if (currentRes != null
					&& currentRes.getKey().equals(dummy.getKey())) {
				// Nothing to do. Current position is in same area as the old
				// one.
				return;
			}

			load(dummy);
		} else {
			// If manager disabled it holds no current resources at all.
			currentRes = null;
		}

	}

	@Override
	public void requestLoaderStop() {
		notifyAll(null);
	}

	@Override
	public void onResourcesAvailable(Collection<WeatherResource> resources) {

		if (resources == null || resources.isEmpty() || !this.isEnabled()) {
			return;
		}

		// Update current used resource with the results of the worker.
		currentRes = resources.iterator().next();

		if (currentRes == null) {
			return;
		}

		GLCanvas canvas = GaiaCanvas.getInstance();
		int width = (int) (canvas.getWidth() / 4.0f);
		int height = (int) (canvas.getHeight() / 6.0f);
		BufferedImage image = WeatherImageFactory.createImage(currentRes, width, height);

		// Create texture data for weather resource.
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", imageOut);
			InputStream imageIn = new ByteArrayInputStream(
					imageOut.toByteArray());
			// TextureData texData =
			// TextureIO.newTextureData(GLProfile.getDefault(), new File(image),
			// false, "PNG");
			TextureData texData = TextureIO.newTextureData(
					GLProfile.get(GLProfile.GL2), imageIn, false, "PNG");
			currentRes.setTexture(texData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Notify observers.
		if (this.isEnabled()) {
			notifyAll(resources);
		}
	}
}
