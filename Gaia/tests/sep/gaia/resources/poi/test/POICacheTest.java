package sep.gaia.resources.poi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.poi.POICache;
import sep.gaia.resources.poi.POIQuery;

public class POICacheTest {
	
	private static POICache pCache;
	
 	@BeforeClass
	public static void init() {
		
 		pCache = new POICache();
	}

	@Test
	public void testGet() {
		assertEquals(pCache.get(new POIQuery(null, null)), null);
	}

	@Test
	public void testAddResources() {
		fail("Not yet implemented");
	}

}