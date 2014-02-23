package sep.gaia.resources.poi.test;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.poi.POICache;
import sep.gaia.resources.poi.POIFilter;
import sep.gaia.resources.poi.POILoaderWorker;
import sep.gaia.resources.poi.POIQuery;
import sep.gaia.resources.poi.PointOfInterest;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

public class POILoaderWorkerTest {

	private static POILoaderWorker worker;
	
	@Test
	public void testRun() {
		FloatBoundingBox bbox = new FloatBoundingBox(new FloatVector3D(12.95886754989624f, 48.83704211147274f, 0), 
													 new FloatVector3D(12.961697280406952f, 48.83549909515772f, 0));
		
		POIFilter filter = new POIFilter();
		filter.addLimitation("name", "Post");
		filter.addLimitation("amenity", "post_office|parking");
		
		POIQuery query = new POIQuery(bbox, filter);
		
		worker = new POILoaderWorker(query, new POICache());
		
		worker.run();
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			fail("Request not finished within given period of time!");
		}
		
		Collection<PointOfInterest> pois = worker.getResults();
		
		if(pois.size() < 2) {
			fail("Not all POIs retruned!");
		}
		
		boolean officeFound = false;
		boolean parkingFound = false;
		
		for(PointOfInterest poi : pois) {
			if(poi.getName().equals("vor der Post")) {
				assertEquals("surface", poi.getValue("parking"));
				assertEquals("limited", poi.getValue("wheelchair"));
				officeFound = true;
			}
			if(poi.getName().equals("Deutsche Post")) {
				assertEquals(48.8362334f, poi.getLatitude(), 0.01f);
				assertEquals(12.9601691f, poi.getLongitude(), 0.01f);
				assertEquals("Deutsche Post AG", poi.getValue("operator"));
				parkingFound = true;
			}
		}
		
		assertEquals(true, officeFound && parkingFound);
	}

}
