package sep.gaia.resources.tiles2d.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GLProfile;

import org.junit.Test;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.tiles2d.Style;
import sep.gaia.resources.tiles2d.Style.SubServer;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.resources.tiles2d.TileQuery;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.util.IntegerVector3D;

/**
 * 
 * @author Johannes
 */
public class TileQuerySplitterTest {

    @Test
    /**
     * Check if a query with 3 resource dummies is splitted equally into 3
     *  subqueries with 1 resources for each.
     */
    public void testSplitQuery() {
	TileManager manager = new TileManager(GLProfile.getDefault());

	// Style with 3 subserver, each can hold 2 connections parallel
	List<Style.SubServer> subservers = new LinkedList<>();
	subservers.add(new SubServer("a.osm.de", 2));
	subservers.add(new SubServer("b.osm.de", 2));
	subservers.add(new SubServer("c.osm.de", 2));
	Style style = new Style("", subservers, "http://$s/style/$z/$x/$y.png");
	//manager.addStyle(style);


	// Create 7 TileResource dummies and put them into a collection.
	Collection<DataResource> tiledummies = new HashSet<>();
	TileResource r1 = new TileResource(new IntegerVector3D(0, 0, 2));
	TileResource r2 = new TileResource(new IntegerVector3D(0, 1, 2));
	TileResource r3 = new TileResource(new IntegerVector3D(1, 0, 2));
	TileResource r4 = new TileResource(new IntegerVector3D(1, 1, 2));
	TileResource r5 = new TileResource(new IntegerVector3D(2, 0, 2));
	TileResource r6 = new TileResource(new IntegerVector3D(2, 2, 2));
	TileResource r7 = new TileResource(new IntegerVector3D(2, 1, 2));
	r1.setDummy(true);
	r2.setDummy(true);
	r3.setDummy(true);
	r4.setDummy(true);
	r5.setDummy(true);
	r6.setDummy(true);
	r7.setDummy(true);

	tiledummies.add(r1);
	tiledummies.add(r2);
	tiledummies.add(r3);
	tiledummies.add(r4);
	tiledummies.add(r5);
	tiledummies.add(r6);
	tiledummies.add(r7);

	// Create a query with the elements from above.
	TileQuery query = new TileQuery(tiledummies);

	// Split the query.
	Collection<TileQuery> splittedQueries = manager.splitQuery(query);
	int numberOfSplittedQueries = splittedQueries.size();

	// A query with 7 elements and a server with 3 subserver,
	// each holds 2 connections parallel
	// - the query should be split into 6 subqueries.
	assertEquals(6, numberOfSplittedQueries);

    }
}
