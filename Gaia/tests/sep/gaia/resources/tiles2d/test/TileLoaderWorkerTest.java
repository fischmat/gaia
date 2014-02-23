package sep.gaia.resources.tiles2d.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GLProfile;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.tiles2d.Style;
import sep.gaia.resources.tiles2d.TileLoaderWorker;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.resources.tiles2d.TileQuery;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.util.IntegerVector3D;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * 
 * @author Johannes Bauer
 */
public class TileLoaderWorkerTest {

	private static TileManager manager;
	private static TileLoaderWorker worker;
	private static TileResource dummy;
	private static TileQuery query;
	private static Collection<DataResource> collection;
	private static TextureData toCompare;

	@BeforeClass
	public static void init() {
		List<Style.SubServer> subservers = new LinkedList<>();
		subservers.add(new Style.SubServer("a.tile.openstreetmap.org", 1));
		//Style style = new Style("osm", subservers, "http://$s/$z/$x/$y.png");
		Style style = new Style("OSM", subservers, "http://$s/$z/$x/$y.png");
		dummy = new TileResource(new IntegerVector3D(17, 10, 5));
		dummy.setStyle(style);
		dummy.setDummy(true);
		collection = new HashSet<>();
		collection.add(dummy);

		// Reference image.
		try {
			toCompare = TextureIO.newTextureData(GLProfile.getDefault(),
					(new File("test" + System.clearProperty("file.separator")
							+ "5-17-10.png")), false, "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Testbild nicht da.");
			e.printStackTrace();
		}
		
	}

	@Test
	public void testRun() {
		query = new TileQuery(collection);
		worker = new TileLoaderWorker(query, GLProfile.getDefault(), null);
		worker.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TileResource result = worker.getResults().iterator().next();

		assertEquals(result.isDummy(), false);
		assertEquals(result.getCoord(), new IntegerVector3D(17, 10, 5));
		assertEquals(toCompare, result.getTextureData());
	}
}
