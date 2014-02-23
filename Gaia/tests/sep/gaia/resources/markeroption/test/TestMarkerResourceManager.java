package sep.gaia.resources.markeroption.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.resources.markeroption.MarkerResourceManager;
import sep.gaia.util.FloatVector3D;

/**
 * Class to test
 * <code>sep.gaia.resources.markeroption.MarkerResourceManager</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class TestMarkerResourceManager {

	private static MarkerResourceManager manager;
	
	
	
	@BeforeClass
	public static void init() {
		manager = new MarkerResourceManager(null);
	}
	
	@Test
	/**
	 * Tests the functionality of adding a <code>MarkerResource</code> object
	 * into the internal marker list of the manager.
	 */
	public void testAddMarker() {
		manager.addMarker(new FloatVector3D(0, 0, 0), 13);
	    assertTrue(manager.getMarkerList().get(0).getLat() == 0
	    		&& manager.getMarkerList().get(0).getZoom() == 13);
	}
	
	@Test
	/**
	 * Tests removing a marker from the list of the manager.
	 */
	public void testRemoveMarker() {
		manager.addMarker(new FloatVector3D(1, 0, 0), 14);
		assertTrue(manager.getMarkerList().contains(new MarkerResource("Unbenannter Marker", 1, 0, 14)));
	
		manager.removeMarker(new FloatVector3D(1, 0, 0), 14, manager.getMarkerList().getSize()-1);
		assertFalse(manager.getMarkerList().contains(new MarkerResource("Unbenannter Marker", 1, 0, 14)));
	}
	
	public void testRenameMarker() {
		manager.addMarker(new FloatVector3D(1, 1, 0), 10);
		manager.renameMarker(new FloatVector3D(1, 1, 0), 10, "Name", manager.getMarkerList().getSize()-1);
		
		assertEquals(manager.getMarkerList().lastElement().getName(), "Name");
	}
	@Test
	/**
	 * 
	 */
	public void testNextTimeStamp() {
		
	}
	
	@Test
	/**
	 * Tests the reaction of the manager when an observed state has changed.
	 */
	public void testOnUpdate() {
		
	}
	
	@Test
	/**
	 * Tests whether the manager's loader can be stopped.
	 */
	public void testRequestLoaderStop() {
		
	}
}
