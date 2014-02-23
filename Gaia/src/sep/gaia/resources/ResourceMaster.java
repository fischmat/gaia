/**
 * 
 */
package sep.gaia.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sep.gaia.util.Logger;

/**
 * This class holds all <code>DataResourceManager</code> implementations. The
 * <code>DataResourceManager</code> objects can be identified by their labels.
 * 
 * This is necessary for iterating
 * 
 * @author Johannes Bauer
 */
public class ResourceMaster {

	/**
	 * This <code>Collection</code> contains all registered
	 * <code>DataResourceManager</code> implementations, identified by their
	 * labels.
	 */
	private Map<String, DataResourceManager<?>> managers = new HashMap<>();

	private static ResourceMaster instance;
	
	private ResourceMaster() { }
	
	/**
	 * 
	 * @param label
	 *            The label that indicates the requested
	 *            <code>DataResourceManager</code>.
	 * @return The <code>DataResourceManager</code> corresponding to the
	 *         passed <code>label</code.
	 */
	public DataResourceManager<?> getResourceManager(String label) {
		return managers.get(label);
	}

	/**
	 * Adds a <code>DataResourceManager</code> to the collection.
	 * 
	 * @param resourceManager
	 */
	public boolean addResourceManager(DataResourceManager<?> resourceManager) {
		if (managers.containsKey(resourceManager.getLabel())) {
			return false;
		}

		// Add a ResourceManager with its label as a key.
		managers.put(resourceManager.getLabel(), resourceManager);
		return true;
	}
	
	public void enableAll() {
		for(DataResourceManager<?> manager : managers.values()) {
			manager.enable();
		}
	}
	
	public void disableAll() {
		for(DataResourceManager<?> manager : managers.values()) {
			manager.disable();
		}
	}
	
	/**
	 * Invokes the <code>onExit()</code>-Event on each registered manager asynchronously.
	 */
	public synchronized void broadcastExitEvent() {
		
		List<Thread> threads = new LinkedList<>();
		
		// Iterate all managers:
		Iterator<DataResourceManager<?>> managerIter = managers.values().iterator();
		while(managerIter.hasNext()) {
			
			// Get the next manager to pass the signal to:
			final DataResourceManager<?> manager = managerIter.next();
			
			// Routine for invoking the event asynchronously:
			Runnable eventRoutine = new Runnable() {
				@Override
				public void run() {
					manager.onExit();
				}
			};
			
			// Start the thread:
			Thread currentThread = new Thread(eventRoutine);
			currentThread.start();
			threads.add(currentThread);
		}
		
		// Block execution until all routines finished:
		for(Thread currentThread : threads) {
			try {
				currentThread.join();
			} catch (InterruptedException e) {
				Logger.getInstance().warning("Unexpected interruption on Exit-event.");
			}
		}
	}
	
	public static ResourceMaster getInstance() {
		if(instance == null) {
			instance = new ResourceMaster();
		}
		return instance;
	}
}