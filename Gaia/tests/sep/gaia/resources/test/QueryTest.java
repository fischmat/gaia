package sep.gaia.resources.test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.NotADummyException;
import sep.gaia.resources.Query;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.util.IntegerVector3D;

/**
 * Class to test <code>sep.gaia.resources.Query</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class QueryTest {
	
	private static Query query;
	
	private static TileResource res;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		res = new TileResource(new IntegerVector3D(0, 0, 10));
		res.setDummy(true);
		query = new Query(new LinkedList<DataResource>());
	}

	@Test(expected = NotADummyException.class)
	public void testAddResource() {
		
		query.addResource(res, 0);		
		assertTrue(query.getResources().size() == 1);
		
		query.addResource(null, 0); // throws a NotADummyException
	}

}
