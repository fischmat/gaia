package sep.gaia.resources.locationsearch.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.locationsearch.Location;
import sep.gaia.resources.locationsearch.LocationSearch;

/**
 * Class to test <code>sep.gaia.resources.locationsearch.LocationSearch</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class LocationSearchTest {
	
	private static LocationSearch lSearch;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lSearch = new LocationSearch();
	}

	@Test
	public void testGetLocationResults() {
		assertTrue(lSearch.getLocationResults().isEmpty());
		
		lSearch.queryforLocations("Frankfurt");
		
		// wait for loader
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertFalse(lSearch.getLocationResults().isEmpty());
	}
	
	@Test
	public void testOnResourcesAvailable() {
		Collection<Location> resources = new LinkedList<>();
		resources.add(new Location(null, null, null));
		lSearch.onResourcesAvailable(resources);
		
		assertTrue(lSearch.getLocationResults().containsAll(resources));
	}

}
