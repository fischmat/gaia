package sep.gaia.resources.tiles2d.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;

import javax.media.opengl.GLProfile;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.tiles2d.TileCache;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.util.IntegerVector3D;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class TileCacheTest {

	/**
	 * Reference to the cache tested.
	 */
	private static TileCache cache;
	
	/**
	 * The manager associated with the cache.
	 */
	private static TileManager manager;
	
	/**
	 * The OpenGL-profile used for the test.
	 */
	private static GLProfile profile;
	
	@BeforeClass
	public static void init() {
		// Get a profile for desktop OpenGL 1.x up to 3.0:
		profile = GLProfile.get(GLProfile.GL2);
		
		manager = new TileManager(profile);
		
		if(manager.getCurrentStyle() == null) {
			fail("For this test at least one style is needed. Check tilestyles.xml");
		}
		
		cache = new TileCache(manager, profile);
	}
	
	/**
	 * Generates and returns the path where the cache-file for <code>tile</code> should be stored.
	 * The path is formatted as follows:
	 * <i>&lt;CACHE_ROOT_DIR&gt;/&lt;URL-encoded style&gt;/z/x/y.&lt;style image-suffix&gt;</i>.
	 * @param tile The resource the cached file is for.
	 * @param suffix The suffix the file should have.
	 * @return The generated path.
	 */
	private static String getCacheFilePath(TileResource tile, String suffix) {
		IntegerVector3D coords = tile.getCoord();
		return getCacheFilePath(coords.getX(), coords.getY(), coords.getZ(), tile.getStyle().getLabel(), suffix);
	}
	
	/**
	 * Generates and returns the path where the cache-file for a tile with given criteria should be stored.
	 * The path is formatted as follows:
	 * <i>&lt;CACHE_ROOT_DIR&gt;/&lt;URL-encoded style&gt;/z/x/y.&lt;style image-suffix&gt;</i>.
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @param zoom The zoom-level of the tile.
	 * @param style The label of the tiles style.
	 * @param suffix The suffix the stored file would have.
	 * @return The generated path.
	 */
	private static String getCacheFilePath(int x, int y, int zoom, String style, String suffix) {
		Environment environment = Environment.getInstance();
		String separator = System.getProperty("file.separator");
		
		// Start with the caches root-directory:
		StringBuilder pathBuilder = new StringBuilder(environment.getString(EnvVariable.CACHE_ROOT_DIR));
		pathBuilder.append(separator);
		
		// The URL-encoded style is the next sub-directory:
		try {
			pathBuilder.append(URLEncoder.encode(style, "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
		
		// The path is followed by z/x/y.suffix:
		pathBuilder.append(separator);
		pathBuilder.append(zoom);
		pathBuilder.append(separator);
		pathBuilder.append(x);
		pathBuilder.append(separator);
		pathBuilder.append(y);
		pathBuilder.append(".");
		pathBuilder.append(suffix);
		
		return pathBuilder.toString();
	}
	
	/**
	 * Returns the texture data stored in the cache-file for <code>tile</code>.
	 * @param tile The resource the cached file is for.
	 * @param profile The OpenGL-profile to use when creating texture-data.
	 * @param suffix The suffix the file should have.
	 * @return The texture-data read or <code>null</code> if an error occured on reading, especially
	 * if the file was not found.
	 */
	private static TextureData readCachedFile(TileResource tile, GLProfile profile, String suffix) {
		
		
		File cacheFile = new File(getCacheFilePath(tile, suffix));
		
		if(cacheFile.exists()) {
			try {
				// Return the texture-data read from the file:
				return TextureIO.newTextureData(profile, cacheFile, false, suffix);
			} catch(IOException e) {
				return null;
			}
			
		} else { // The file does not exist:
			return null;
		}
	}
	
	
	/**
	 * Copies the content of <code>sourceFilePath</code> to the file <code>destFilePath</code>.
	 * If the destination does not exist, it will be created. If it exists, it will be replaced.
	 * @throws IOException Thrown if an error occurs on I/O-operations.
	 */
	private static void copyFile(String sourceFilePath, String destFilePath) throws IOException {
		
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		
		// If the target does not exist yet, a new file must be created:
	    if(!destFile.exists()) {
	    	destFile.getParentFile().mkdirs();
	        destFile.createNewFile();
	    }

	    // Transfer data:
	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    } finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	/**
	 * Tests the adding of both resources with valid and invalid image-information.
	 * After adding the presence of the texture-data on disk is checked.
	 */
	@Test
	public void testAdd() {
		// This test assumes an empty cache:
		cache.clear();
		
		// Create a tile-resource with valid texture-data:
		IntegerVector3D coords = new IntegerVector3D(1, 1, 5);
		TileResource tile = new TileResource(coords);
		
		String suffix = manager.getCurrentStyle().getImageSuffix();
		
		// The iamge used for testing:
		File tileImageFile = new File("test/tiles/osm-5-1-1." + suffix);
		
		TextureData texData;
		try {
			texData = TextureIO.newTextureData(profile, tileImageFile, false, suffix);
			tile.setTextureData(texData);
		} catch (IOException e) {
			fail("The image-file needed for testing cannot be read!");
			return;
		}
		
		tile.setStyle(manager.getCurrentStyle());
		tile.setDummy(false);
		
		// Adding should succeed:
		assertEquals(cache.add(tile), true);
		
		// The texture-data stored in the cached file should match the one in the resource:
		TextureData readData = readCachedFile(tile, profile, suffix);
		assertEquals(texData.getBuffer(), readData.getBuffer());
		
		// Now create a tile without image-information:
		coords = new IntegerVector3D(5, 3, 5);
		tile = new TileResource(coords);
		tile.setStyle(manager.getCurrentStyle());
		tile.setDummy(false);
		
		// Adding should fail now:
		assertEquals(cache.add(tile), false);
	}

	/**
	 * Tests the insertion and retrieval of tile-resources.
	 * A check of the actual presence of the tile on disk is not performed.
	 */
	@Test
	public void testAddGet() {
		// This test assumes an empty cache:
		cache.clear();
		
		// Create a tile-resource with valid texture-data:
		IntegerVector3D coords = new IntegerVector3D(1, 1, 5);
		TileResource tile = new TileResource(coords);
		
		String suffix = manager.getCurrentStyle().getImageSuffix();
		
		// The iamge used for testing:
		File tileImageFile = new File("test/tiles/osm-5-1-1." + suffix);
		
		TextureData texData;
		try {
			texData = TextureIO.newTextureData(profile, tileImageFile, false, suffix);
			tile.setTextureData(texData);
		} catch (IOException e) {
			fail("The image-file needed for testing cannot be read!");
			return;
		}
		
		tile.setStyle(manager.getCurrentStyle());
		tile.setDummy(false);
		
		// Adding should succeed:
		assertEquals(cache.add(tile), true);
		
		TileResource result = cache.get(tile.getKey());
		
		assertEquals(result, tile);
		assertEquals(result.getTimestamp(), tile.getTimestamp());
	}
	
	/**
	 * Tests the initial loading of data from the cache index file.
	 */
	@Test
	public void testLoad() {
		
		// Copy required files:
		try {
			String cacheIndexPath = Environment.getInstance().getString(EnvVariable.TILE_CACHE_INDEX_FILE);
			copyFile("test/tilecache_test.xml", cacheIndexPath);
			copyFile("test/tiles/osm-5-6-7.png", getCacheFilePath(6, 7, 5, "OpenStreetMap - DE", "png"));
		} catch (IOException e) {
			fail("Error copying test-files!");
		}
		
		// (Re-)create the cache:
		cache = new TileCache(manager, profile);
		
		// One tile must be loaded now:
		assertEquals(1, cache.size());
	}
	
	@Test
	public void testAddWriteback() {
		// This test assumes an empty cache:
		cache.clear();
		
		// Create a tile-resource with valid texture-data:
		IntegerVector3D coords = new IntegerVector3D(6, 7, 5);
		TileResource tile = new TileResource(coords);
		
		String suffix = manager.getCurrentStyle().getImageSuffix();
		
		// The image used for testing:
		File tileImageFile = new File("test/tiles/osm-5-6-7." + suffix);
		
		TextureData texData;
		try {
			texData = TextureIO.newTextureData(profile, tileImageFile, false, suffix);
			tile.setTextureData(texData);
		} catch (IOException e) {
			fail("The image-file needed for testing cannot be read!");
			return;
		}
		
		tile.setStyle(manager.getCurrentStyle());
		tile.setTimestamp(1337);
		tile.setDummy(false);
		
		// Adding should succeed:
		assertEquals(cache.add(tile), true);
		
		String cacheIndexPath = Environment.getInstance().getString(EnvVariable.TILE_CACHE_INDEX_FILE);
		File cacheIndex = new File(cacheIndexPath);
		cacheIndex.delete();
		
		// Write the cache-index-file:
		cache.writeBack();
		
		assertEquals(true, cacheIndex.exists());
	}
	
	/**
	 * Test the removal strategy of the cache.
	 * @throws InterruptedException
	 */
	@Test
	public void testAddManage() {
		// This test assumes an empty cache:
		cache.clear();
		
		// Limit the cache size to 10 bytes:
		cache.setMaximumSizeOnDisk(10);
		
		// Create a tile-resource with valid texture-data:
		IntegerVector3D coords = new IntegerVector3D(1, 1, 5);
		TileResource tile = new TileResource(coords);
		
		String suffix = manager.getCurrentStyle().getImageSuffix();
		
		// The image used for testing:
		File tileImageFile = new File("test/tiles/osm-5-1-1." + suffix);
		
		TextureData texData;
		try {
			texData = TextureIO.newTextureData(profile, tileImageFile, false, suffix);
			tile.setTextureData(texData);
		} catch (IOException e) {
			fail("The image-file needed for testing cannot be read!");
			return;
		}
		
		tile.setStyle(manager.getCurrentStyle());
		tile.setDummy(false);
		
		// Adding should succeed:
		assertEquals(cache.add(tile), true);
		
		// The cached file should exist now:
		File cachedFile = new File(getCacheFilePath(tile, suffix));
		assertEquals(true, cachedFile.exists());
		
		// Clean the cache:
		cache.manage();
		
		// Wait for the asynchronous file-deletion to complete:
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			fail("Test interrupted!");
		}
		
		// The file should now not be present any more and cache-size should be zero:
		assertEquals(0, cache.size());
		assertEquals(false, cachedFile.exists());
		
		// Reset cache-size:
		cache.setMaximumSizeOnDisk(Long.MAX_VALUE);
	}
	
	@Test
	public void testMaximumVSCurrentSize() {
		cache.clear();
		
		// Create a tile-resource with valid texture-data:
		IntegerVector3D coords = new IntegerVector3D(1, 1, 5);
		TileResource tile = new TileResource(coords);
		
		String suffix = manager.getCurrentStyle().getImageSuffix();
		// The image used for testing:
		File tileImageFile = new File("test/tiles/osm-5-1-1." + suffix);
		
		TextureData texData;
		try {
			texData = TextureIO.newTextureData(profile, tileImageFile, false, suffix);
			tile.setTextureData(texData);
		} catch (IOException e) {
			fail("The image-file needed for testing cannot be read!");
			return;
		}
		
		tile.setStyle(manager.getCurrentStyle());
		tile.setDummy(false);
	
		cache.add(tile);
		cache.setMaximumSizeOnDisk(0);
		cache.manage();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail("Test was interrupted!");
		}
		
		assertTrue(cache.getCurrentSizeOnDisk() == 0);
	}
}
