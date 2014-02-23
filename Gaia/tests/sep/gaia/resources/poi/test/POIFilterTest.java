package sep.gaia.resources.poi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.poi.POIFilter;
import sep.gaia.resources.poi.PointOfInterest;

/**
 * Class to test <code>POIFilter</code>
 * 
 * @author Max Witzelsperger
 *
 */
public class POIFilterTest {
	
	private static POIFilter filter;
	
	@BeforeClass
	public static void init() {
		
		filter = new POIFilter();
	}
	
	@Test
	public void testAddLimitation() {
		assertFalse(filter.getLimitations().containsKey("myKey"));
		filter.addLimitation("myKey", "myValue");
		assertTrue(filter.getLimitations().containsKey("myKey"));
	}

	@Test
	public void testIsMatchingPointOfInterest() {
		PointOfInterest poi = new PointOfInterest("POI", 0, 0);
		assertTrue(filter.isMatching(poi));
		HashMap<String, String> map = new HashMap<>();
		map.put("key", "val");
		poi.setAttributes(map);
		
		filter.addLimitation("key", "val");	
		assertTrue(filter.isMatching(poi));
		
		filter.addLimitation("anotherKey", "anotherValue");
		assertFalse(filter.isMatching(poi));
		
		assertFalse(poi.hasAttrbute("anotherKey"));
	}

	@Test
	public void testIsMatchingCollectionOfPointOfInterest() {
		Collection<PointOfInterest> pois = new LinkedList<>();
		PointOfInterest poi1 = new PointOfInterest("POI1", 0, 0);
		PointOfInterest poi2 = new PointOfInterest("POI2", 1, 1);
		pois.add(poi1);
		pois.add(poi2);
		
		filter.addLimitation("key", "value");
		
		HashMap<String, String> map = new HashMap<>();
		map.put("key", "value");
		poi1.setAttributes(map);
		
		assertFalse(filter.isMatching(pois));
		
		poi2.setAttributes(map);
		assertTrue(filter.isMatching(pois));
		
	}

}
