package sep.gaia.resources.weather.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.NotADummyException;
import sep.gaia.resources.weather.WeatherCache;
import sep.gaia.resources.weather.WeatherManager;
import sep.gaia.resources.weather.WeatherResource;

import com.jogamp.opengl.util.texture.TextureData;

/**
 * Class to test the methods of
 * <code>sep.gaia.resources.weather.WeatherCache</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class WeatherCacheTest {

	private static WeatherCache wCache;
	
	private static DummyWeatherManager wManager;
	
	private static WeatherResource wResource;
	
	@BeforeClass
	public static void init() {
		wManager = new DummyWeatherManager();
		wCache = new WeatherCache(wManager);
		
		// Create a WeatherResource for (lon, lat) = (0, 0) and zoom level 12
		wResource = new WeatherResource(0, 0, 12);
		wResource.setTexture(new TextureData(null, 0, 0, 0, 0, null, false, false, false, null, null));

	}
	
	@Before
	public void reset() {
		wResource.setDummy(true); // a weather resource is initially a dummy
		wCache.clear(); // remove all elements from the cache
		wManager.res = null;
		wCache.setMaxEntries(Integer.MAX_VALUE);
	}
	
	@Test (expected = NotADummyException.class)
	public void testGetWithNoDummy() {
		wResource.setDummy(false);
		wCache.get(wResource); // cache starts a worker
		
		// weather resource is not a dummy, so the worker throws an exception
	}
	
	@Test
	public void testGetAsDummyWithFilledCache() {
		wCache.add(wResource);
		wCache.get(wResource);
		
		Collection<WeatherResource> results = new HashSet<>();
		results.add(wCache.get(wResource.getKey()));
		assertEquals(results, wManager.res);
	}
	
	@Test
	public void testGetAsDummyWithEmptyCache() {
		wCache.get(wResource);
		
		// a new loader starts to get data from the Internet.
		// Wait for the loader
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(wManager.res != null);
	}

	@Test
	public void testOnResourcesAvailable() {
		Collection<WeatherResource> res = new HashSet<>();
		res.add(wResource);
		wCache.onResourcesAvailable(res);
		
		assertEquals(res, wManager.res);
	}
	
	@Test
	public void testOnResourcesAvailableWithLimitedCacheSize() {
		wCache.add(wResource);
		wCache.setMaxEntries(0);
		
		assertTrue(wCache.size() == 1);
		
		Collection<WeatherResource> res = new HashSet<>();
		res.add(wResource);
		
		wCache.onResourcesAvailable(res);
		
		assertTrue(wCache.size() == 0);
		assertEquals(wManager.res, res);
	}
	
	private static class DummyWeatherManager extends WeatherManager {
		
		public Collection<WeatherResource> res;
		
		public DummyWeatherManager() {
			super();
			this.res = null;
		}
		
		@Override
		public void onResourcesAvailable(Collection<WeatherResource> resources) {
			
			this.res = resources;
		}
	}

}
