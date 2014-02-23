package sep.gaia.resources.tiles2d;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GLProfile;

import jogamp.opengl.util.pngj.PngjException;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.DataResource;
import sep.gaia.util.IntegerVector3D;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A worker for sequentially loading map-tiles from a server.
 * 
 * @author Johannes Bauer (specification), Matthias Fisch (implementation)
 */
public class TileLoaderWorker extends AbstractLoaderWorker<TileQuery, TileResource> {

	/**
	 * The OpenGL-profile to use when loading texture-data.
	 */
	private GLProfile profile;
	
	/**
	 * Each <code>TileLoaderWorker</code> is instantiated with a "task". This
	 * task in form of a <code>Query</code> object or a extended object of
	 * <code>Query</code> is passed as a parameter.
	 * The <code>TileLoaderWorker</code> will work on this query.
	 * After its work is done, the results are stored and
	 * the <code>TileLoaderWorker</code> dies.
	 * 
	 * @param subQuery The query this <code>TileLoaderWorker</code> has to perform.
	 * @param profile The OpenGL-profile to use when loading texture-data.
	 * @param cache The cache to use for querying cached tiles.
	 */
	public TileLoaderWorker(TileQuery subQuery, GLProfile profile, Cache<TileResource> cache) {
		super(subQuery, cache);
		this.profile = profile;
	}

	@Override
	public void run() {
		TileQuery query = getSubQuery();
		if(query != null) {
			
			Style style = null;
			Collection<DataResource> resources = query.getResourcesByPriority();
			List<TileResource> tileResources = new LinkedList<>();
			// Cast all resources from the query and store them:
			for(DataResource resource : resources) {
				if(resource instanceof TileResource) {
					TileResource tile = (TileResource)resource;
					tileResources.add(tile);
					
					// If not done yet, set the style:
					if(style == null) {
						style = tile.getStyle();
					}
				}
			}
			
			if(style != null) {
				
				// Select a subserver:
				Style.SubServer subServer = query.getSubServer();
				if(subServer == null) {
					// If no subserver was specified, pick the first one:
					subServer = style.getSubServers().get(0);
				}
				
				List<TileResource> results = new LinkedList<>();
				
				// Add texture-data to each tile:
				Iterator<TileResource> tileIter = tileResources.iterator();
				while(tileIter.hasNext() && !isInterrupted()) {
					
					TileResource tile = tileIter.next();
					
					// Check if present in cache:
					boolean cacheHit = false;
					Cache<TileResource> cache = getCache();
					if(cache != null) {
						TileResource result = cache.get(tile.getKey());
						if(result != null) {
							results.add(result);
							cacheHit = true;
						}
					}
					
					// The texture was not found in cache, so load it from the server:
					if(!cacheHit) {
						TileResource result = getFromRemoteSource(tile, style, subServer);
						if(result != null) {
							results.add(result);
						}
					}
				}
				
				if(!isInterrupted()) {
					// Set all tile-resources as the workers result:
					setResults(results);
				}
			}
		}
	}

	private TileResource getFromRemoteSource(TileResource tile, Style style, Style.SubServer subServer) {
		// Get the vector (x, y, zoom):
		IntegerVector3D coords = tile.getCoord();
		
		URL url = null;
		try {
			url = style.generateUrl(subServer.getHostname(), coords.getZ(),
									coords.getX(), coords.getY());
		} catch (MalformedURLException e) {
			
			Logger.getInstance().error("Cannot generate a valid URL from from"
					+ "syntax: " + style.getSyntax() + " Arguments are $s="
					+ subServer.getHostname() + " $x=" + coords.getX() + " $y="
					+ coords.getY() + " $z=" + coords.getZ());
		}
		
		if(url != null) {
			
			TextureData textureData;
			try {
				// Create texture-data without auto-generating mipmaps:
				textureData = TextureIO.newTextureData(profile, url, false, style.getImageSuffix());
				
			} catch (IOException | PngjException e) {
				// TODO Set offline here!
				Logger.getInstance().warning("Unable to load tile from " + url.toString());
				return null;
			}
			
			//System.out.println("I've finished loading " + currentTile.getKey());
			
			tile.setTextureData(textureData);
			// Tile has been filled correctly, so remove dummy-flag:
			tile.setDummy(false);
			
			return tile;
		}
		return null;
	}
}