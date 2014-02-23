package sep.gaia.resources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A <code>ResourceObservable</code> is observed by a
 * <code>ResourceObserver</code> implementation.
 * 
 * The <code>ResourceObserver</code> gets notified by this class, if something
 * was changing in this class.
 * 
 * @author Johannes Bauer
 * 
 */
public class ResourceObservable<R extends DataResource> {

	/**
	 * A list of observers, observing this object.
	 */
	private List<ResourceObserver<R>> observers = new LinkedList<>();

	/**
	 * Registers an observer for this management object. The observer will be
	 * informed about a change of state by calling <code>onUpdate()</code>,
	 * until he ends his abonnement by calling <code>unregister()</code>.
	 * 
	 * @param observer
	 *            The observer who wants to be registered.
	 * @return <code>true</code>, if the observer could be added successfully.
	 *         return <code>false</code>, if the observer has already been
	 *         registered.
	 */
	public boolean register(ResourceObserver<R> observer) {
		// Add no duplicated observer.
		if(!observers.contains(observer)) {

			return observers.add(observer);
		}

		// If observer is already registered, do nothing and return false.
		return false;
	}

	/**
	 * Removes an observer from the state changing informing list. This removed
	 * observer won't be notified in the future.
	 * 
	 * @param observer
	 *            The observer which should be removed.
	 * @return <code>true</code>, if <code>observer</code> was a registered
	 *         observer and the removing was successful. Else return
	 *         <code>false</code>.
	 */
	public boolean unregister(ResourceObserver<R> observer) {
		// Remove observer from the list of observers.
		return observers.remove(observer);
	}

	/**
	 * Notifies all observers that are registered to this
	 * <code>ResourceObservable</code>.
	 * 
	 * @param resources
	 *            A collection of resources which hold the change of the state.
	 */
	public void notifyAll(Collection<R> resources) {
		// Notify each observer in the list.
		ListIterator<ResourceObserver<R>> iterator = observers.listIterator();
		while(iterator.hasNext()) {
			
			ResourceObserver<R> currentObserver = iterator.next();
			currentObserver.onUpdate(resources);
		}
	}
	
	/**
	 * Invokes to <code>onClear()</code>-Event at each registered observer.
	 */
	public void clearAll(){
		// Clear each observer in the list.
		ListIterator<ResourceObserver<R>> iterator = observers.listIterator();
		while(iterator.hasNext()) {
			
			ResourceObserver<R> currentObserver = iterator.next();
			currentObserver.onClear();
		}
	}
}
