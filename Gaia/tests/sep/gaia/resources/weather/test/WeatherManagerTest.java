package sep.gaia.resources.weather.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.Collection;
import java.util.HashSet;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.weather.WeatherManager;
import sep.gaia.resources.weather.WeatherResource;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

/**
 * Class to test <code>sep.gaia.resources.weather.WeatherManager</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class WeatherManagerTest {

	private static WeatherManager manager;
	private static WeatherResource wallersdorf;

	@BeforeClass
	public static void init() {
		manager = new WeatherManager();
		
		// Standard profile. Needed for some of the methods of the weather
		// manager to work
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities glcap = new GLCapabilities(profile);
		GaiaCanvas.getInstance(glcap).setSize(new Dimension(512, 512));
		
		// WeatherResource für Wallersdorf
		wallersdorf = new WeatherResource(48.75123f, 12.75123f, 12);
	}
	
	@Before
	public void reset() {
		wallersdorf.setDummy(true);
		manager = new WeatherManager();
	}

	@AfterClass
	public static void destroy() {
		manager = null;
	}

	@Test
	public void testRequestLoaderStop() {
		manager.requestLoaderStop();
		
		manager.load(wallersdorf);
		
	    try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	    // loader should not have loaded data
		assertTrue(null == manager.getCurrentWeatherResource());
	}
	
	@Test
	public void testGetCurrentWeatherResource() {
		assertTrue(null == manager.getCurrentWeatherResource());
		
		manager.load(wallersdorf);

	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    assertEquals(wallersdorf, manager.getCurrentWeatherResource());
	}

	@Test
	public void testLoad() {
		manager.load(wallersdorf);

	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(wallersdorf, manager.getCurrentWeatherResource());
	}

	@Test
	public void testOnUpdate() { 
		
		// create a GL state with wallersdorf as center
		FloatVector3D geoVec = new FloatVector3D(48.75123f, 12.75123f, 12);
		FloatVector3D glVec = AlgoUtil.geoToGL(geoVec);
		glVec.setZ(AlgoUtil.tileToGLZoom(12));
		GLState state = new GLState(AlgoUtil.tileToGLZoom(12));
		state.setPosition(glVec);
		manager.enable();
		// load data for wallersdorf
		manager.onUpdate(state);
		
		assertEquals(manager.getCurrentWeatherResource(), wallersdorf);
	}

	@Test
	public void testOnResourcesAvailable() {
		Collection<WeatherResource> res = new HashSet<>();
		res.add(wallersdorf);
		
		manager.onResourcesAvailable(res);
		
		assertEquals(manager.getCurrentWeatherResource(), wallersdorf);
	}

	@Test
	public void testDisableAndEnable() {

		manager.disable();
		manager.load(wallersdorf);
		
		// Now, the WeatherManager should be disabled and therefore, it holds
		// no WeatherResource objects.
		assertEquals(manager.getCurrentWeatherResource(), null);

		manager.enable();
		manager.load(wallersdorf);

		// manager should have loaded now
		assertEquals(manager.getCurrentWeatherResource(), wallersdorf);
		
	}

	// TODO no longer needed?
	//@Test
	/**
	 * Update GeoState -> Notify WeatherManager 
	 * -> WeatherManager loads current WeatherResource
	 */
	public void testGetUsedResources() {
		// Ort: Poxau
		GeoState geoState = new GeoState(15, 12.56123f, 48.56123f, null);
		manager.onUpdate(geoState);
		WeatherResource poxau = new WeatherResource(48.56f, 12.56f, 12);
		
		// Latitude equal?
		assertEquals(manager.getCurrentWeatherResource().getLat(),
				poxau.getLat(), 0.1f);
		// Longitude equal?
		assertEquals(manager.getCurrentWeatherResource().getLon(),
				poxau.getLon(), 0.1f);
	}

}
