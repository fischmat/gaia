package sep.gaia.resources;

import java.util.Collection;
import java.util.HashSet;

import sep.gaia.state.State;
import sep.gaia.state.StateObserver;

/**
 * This class is an abstract class for all object to be managed. It observes the
 * actual state of the state objects and concrete implementations can load data
 * by the means of the <code>Loader</code>. Object of the class can be observed
 * in order to react on the availability of new data. The loading process of the
 * data can be activated and deactivated, e.g. to prevent unnecessary loading of
 * data. Furthermore an off-line flag can be set, e.g. to signal the concrete
 * implementations not to transmit any further data over the network. So
 * observers are informed about actualizations by data located in the random
 * access memory.
 * 
 * @author Johannes Bauer (Spezifikation: Matthias Fisch)
 * 
 */
public abstract class DataResourceManager<R extends DataResource> extends
		ResourceObservable<R> implements StateObserver {

	/**
	 * Flag to tell if the manager is activated and shall inform about the
	 * change of state.
	 */
	private boolean enabled;

	/**
	 * Flag to tell if the manager is on-line. That means if the manager shall
	 * inform about changes of state but shall not load any data.
	 */
	private boolean online;	

	/**
	 * The label is to identify a <code>DataResourceManager</code>
	 * implementation. (E.g. necessary in the <code>ResourceMaster</code> class.
	 */
	private final String label;

	/**
	 * This method initializes an activated manager which is not off-line but
	 * has no loader for the moment.
	 * 
	 * @param label
	 *            The name of the ResourceManager.
	 */
	public DataResourceManager(String label) {
		super();
		this.label = label;
		this.enabled = false;
		this.online = false;
	}

	/**
	 * This method initializes an activated manager.
	 * 
	 * @param enabled
	 *            Flag to tell if the manager is activated at the beginning,
	 *            i.e. if the manager shall inform about changes of state.
	 * 
	 * @param offline
	 *            Flag to tell if the manager is off-line at the beginning, i.e.
	 *            if the manager shall inform about changes of state but shall
	 *            not load any data.
	 */
	public DataResourceManager(String label, boolean enabled, boolean online) {
		super();
		this.label = label;
		this.enabled = enabled;
		this.online = online;
	}

	/**
	 * This method activates the manager, i.e. that the manager shall inform
	 * about changes of state.
	 */
	public void enable() {
		this.enabled = true;
	}

	/**
	 * This method quits the manager, i.e. that the manager shall no longer
	 * inform about changes of state and holds no current used data.
	 */
	public void disable() {
		this.enabled = false;
		this.online = false;
		//this.requestLoaderStop();
	}

	/**
	 * This method sets the manager on-line if and only if <code>online</code>
	 * is true, i.e. the manager shall inform about changes of state but shall
	 * not load any data from tile api server.
	 * 
	 * @param online
	 *            <code>true</code> to set the manager off-line.
	 */
	public void setOnline(boolean online) {
		this.online = online;
		requestLoaderStop();
	}

	/**
	 * This method sends a message to the loader to tell it to quit for the next
	 * possible moment.
	 */
	public abstract void requestLoaderStop();
	
	/**
	 * Called when the manager was last used at runtime.
	 * May be overridden by subclasses to do work, like writing data to disk.
	 * The default implementation doesn't do anything.
	 */
	public void onExit() { }

	/**
	 * This method tells if the manager is activated, i.e. if the manager shall
	 * inform about changes of state.
	 * 
	 * @return <code>true</code> if and only if the manager is activated.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * This method tells if the manager is on-line, i.e. if the manager shall
	 * inform about changes of state but shall not load any data (if it is no
	 * online).
	 * 
	 * @return <code>true</code> if and only if the manager is on-line.
	 */
	public boolean isOnline() {
		return this.online;
	}

	/**
	 * Returns the label of this <code>DataResourceManager</code> in order to
	 * identify it.
	 * 
	 * @return
	 */
	public String getLabel() {
		return this.label;
	}
}
