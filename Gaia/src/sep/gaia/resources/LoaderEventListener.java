package sep.gaia.resources;

import java.util.Collection;

/**
 * An interface for reacting if the corresponding loader has partially processed
 * a query and therefore new data is avaliable.
 * @author Matthias Fisch
 *
 * @param <R> Type of the resources the information which availability implementing instances
 * want to be informed about.
 */
public interface LoaderEventListener<R extends DataResource> {

	/**
	 * Will be called if the loader the implementation of this listener is assigned to,
	 * has partially processed a query.
	 * @param resources The loaded resources mixed with dummy-objects representing those not yet loaded.
	 */
	public void onResourcesAvailable(Collection<R> resources);
}
