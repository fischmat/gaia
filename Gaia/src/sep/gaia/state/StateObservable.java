package sep.gaia.state;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * A class implemented by the abstract class <code>State</code> in order
 * to be observed by any class that implements the <code>StateObserver</code>
 * interface.
 * A <code>StateObservable</code> informs each of its observers whenever
 * its internal state is changed.
 * 
 * @author Max Witzelsperger, Johannes Bauer, Matthias Fisch
 *
 */
public abstract class StateObservable extends Observable {
	
	/**
	 * The list of all observers of <code>this</code>.
	 */
	private Set<StateObserver> observerList = new HashSet<>();
	
	/**
	 * A lock for managing access to <code>observerList</code>.
	 */
	private Lock observerListLock = new ReentrantLock();
	
	/**
     * Method to make <code>obs</code> observe <code>this</code>.
     * <code>obs</code> is added to an internal list of observers of
     * <code>this</code> and will be notified whenever the state of
     * <code>this</code> is changed.
     * 
     * @param obs the observer to be added to the list of the observers
     * of <code>this</code>
	 */
	public void register(StateObserver obs) {
		
		// Lock the list for adding:
		observerListLock.lock();
		this.observerList.add(obs);
		observerListLock.unlock();
	}

	/**
	 * Removes <code>obs</code> from the list of the observers of 
	 * <code>this</code>.
	 * 
	 * @param obs the observer of <code>this</code> to be removed
	 */
	public void unregister(StateObserver obs) {

		// Lock the set to remove the observer:
		observerListLock.lock();
		this.observerList.remove(obs);
		observerListLock.unlock();
	}
	
	/**
	 * Returns all observers registered.
	 * @return All observers registered.
	 */
	public Set<StateObserver> getObservers() {
		// Lock the observer-set for use in this thread:
		observerListLock.lock();
		// Make a copy of the set for use in this thread:
		Set<StateObserver> observers = new HashSet<>(observerList);
		observerListLock.unlock(); // Let other threads use the set again
		
		return observers;
	}
	
	/**
	 * Method to make all registered observers of <code>this</code>
	 * update themselves.
	 * To be used whenever the state of <code>this</code> has changed.
	 */
	public void notifyStateObservers() {
		
		// Lock the observer-set for use in this thread:
		observerListLock.lock();
		
		// Invoke the onUpdate-event on all observers:
		for (StateObserver obs : this.observerList) {
			obs.onUpdate((State) this);
		}
		
		observerListLock.unlock();
	}
}