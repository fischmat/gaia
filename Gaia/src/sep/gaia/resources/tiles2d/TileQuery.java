package sep.gaia.resources.tiles2d;

import java.util.Collection;
import java.util.Map;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.NotADummyException;
import sep.gaia.resources.Query;
import sep.gaia.resources.tiles2d.Style.SubServer;

/**
 * <code>TileQuery</code> is for requesting <code>TileResource</code> objects.
 * Each <code>TileQuery</code> contains a <code>Collection</code> of <code>TileResource</code> (dummys).
 * @author Johannes Bauer
 */
public class TileQuery extends Query {
	
	/**
	 * The subserver to use for this query or <code>null</code>
	 * if all subservers from <code>style</code> should be used.
	 */
	private SubServer subServer;
	
	/**
	 * Initializes the query.
	 * @param resource The dummies, specifying the coordinates and zoom of the tiles to load. They
	 * will be modified by the loader.
	 * @param subServer The subserver to use for loading or <code>null</code> if the worker should choose a subserver.
	 * @throws NotADummyException Thrown if any entry in <code>resources</code> has not set the dummy-flag.
	 */
	public TileQuery(Collection<DataResource> resources, SubServer subServer) throws NotADummyException {
		super(resources);
		this.subServer = subServer;
	}
	
	/**
	 * Initializes the query.
	 * @param resource The dummies, specifying the coordinates and zoom of the tiles to load. They
	 * will be modified by the loader.
	 * @throws NotADummyException Thrown if any entry in <code>resources</code> has not set the dummy-flag.
	 */
	public TileQuery(Collection<DataResource> resources) throws NotADummyException {
		super(resources);
	}
	
	
	/**
	 * Initializes by a collection of dummy-resources to be filled with the
	 * information gathered by the query.
	 * @param resources The collection of dummy-resources to be filled with the
	 * information gathered by the query mapped to their respective priority.
	 * @throws NotADummyException Thrown if any resource in <code>resources.keySet()</code>
	 * does not have the dummy-flag set.
	 */
	public TileQuery(Map<DataResource, Integer> resources)
			throws NotADummyException {
		super(resources);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the subserver to use for this query.
	 * @return The subserver to use for this query or <code>null</code>
	 * if all subservers from <code>style</code> should be used.
	 */
	public SubServer getSubServer() {
		return subServer;
	}

	/**
	 * Sets the subserver to use for this query.
	 * @param subServer The subserver to use for this query or <code>null</code>
	 * if all subservers from <code>style</code> should be used.
	 */
	public void setSubServer(SubServer subServer) {
		this.subServer = subServer;
	}
	
	
}