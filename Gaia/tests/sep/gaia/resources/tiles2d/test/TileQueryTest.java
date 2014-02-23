package sep.gaia.resources.tiles2d.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.NotADummyException;
import sep.gaia.resources.tiles2d.TileQuery;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.util.IntegerVector3D;

/**
 * 
 * @author Johannes Bauer
 */
public class TileQueryTest {

    @Test
    public void testGetResourcesbyPriority() {
	fail("Not yet implemented");
    }

    @Test(expected = NotADummyException.class)
    /**
     * Test ob hinzugefügte Resource in der Query bleiben.
     * 
     * !!! UM AUF PRIORITAET ZU TESTEN, DARF DIE GET METHODE KEINE COLLECTION
     *  UEBERGEBEN! COLLECTIONS HABEN KEINE SORTIERUNG bzw.
     *  SIND NICHT IN GEORDNET ZU ITERIEREN.
     */
    public void testGetResources() {
	// Create resource dummies.
	TileResource r1 = new TileResource(new IntegerVector3D(0, 0, 1));
	TileResource r2 = new TileResource(new IntegerVector3D(1, 0, 1));
	TileResource r3 = new TileResource(new IntegerVector3D(0, 1, 1));
	TileResource r4 = new TileResource(new IntegerVector3D(1, 1, 1));

	// Add resources to a collection.
	Collection<DataResource> resources = new HashSet<>();
	resources.add(r1);
	resources.add(r2);
	resources.add(r3);
	resources.add(r4);

	// Pass the collection to a tileQuery.
	TileQuery query = new TileQuery(resources);

	assertEquals(query.getResources(), resources);
    }

    @Test
    /**
     * Test, if a exception is thrown if a non dummy object is added.
     */
    public void testAddResource() {
	TileResource r1 = new TileResource(new IntegerVector3D(0, 0, 1));
	r1.setDummy(false);
	Collection<DataResource> collection = new HashSet<>();
	TileQuery query = new TileQuery(collection);
	
	boolean exceptionFired = false;
	try {
	    query.addResource(r1, 0);	    
	} catch (NotADummyException e) {
	    exceptionFired = true;
	}
	
	assertEquals(exceptionFired, true);
    }

}
