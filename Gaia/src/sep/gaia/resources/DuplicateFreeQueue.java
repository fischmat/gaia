package sep.gaia.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a single-direction queue with unique entries (checked by
 * <code>equals()</code>).
 * Furthermore an priority can be assigned to each entry.
 * The element with the highest priority will be returned first independent of
 * its actual position.
 * If priorities are equal, the element first inserted is returned.
 * All methods in this class are designed for asynchronous access.
 * 
 * @author Matthias Fisch
 *
 * @param <T> The type of the queues entries.
 */
public class DuplicateFreeQueue<T> {

	/**
	 * The priority entries are set to if none is specified.
	 */
	public static final int DEFAULT_PRIORITY = 0;

	/**
	 * List of all elements currently managed.
	 */
	private Map<Integer, List<T>> queues = new HashMap<Integer, List<T>>();
	
	/**
	 * Lock to prevent unexpected modification of <code>queues</code>
	 * at asynchronous access.
	 */
	private Lock queuesLock = new ReentrantLock();
	
	/**
	 * Adds an object with default priority (<code>DEFAULT_PRIORITY</code>).
	 * 
	 * @param content The object to be inserted.
	 * 
	 * @return <code>true</code> if <code>content</code> has been successfully 
	 * added to the queue. 
	 * <code>false</code> if there is already an equal objected contained.
	 */
	public boolean push(T content) {		
		// Add content with default priority:
		return push(content, DEFAULT_PRIORITY);
	}
	
	/**
	 * Adds an object with a specific priority.
	 * 
	 * @param content The object to be inserted.
	 * @param priority The priority of the object to be inserted. A higher
	 * value means preferred handling.
	 * 
	 * @return <code>true</code> if and only if adding <code>content</code>
	 * was successful
	 */
	public boolean push(T content, int priority) {
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Entries of all priorities must be checked on equality to content:
		boolean unique = true; // Flag if an equal entry was not found yet
		Iterator<List<T>> currentListIter = queues.values().iterator();
		while(currentListIter.hasNext() && unique) {
			// Get the current queue:
			List<T> currentList = currentListIter.next();
			// Check if an equal entry is contained in this queue:
			unique = !currentList.contains(content);
		}
		
		// If content has no identical entry in any list:
		if(unique) {
			// Get list of all entries with the specified:
			List<T> priorityList = queues.get(priority);
			
			// If the list does not exist yet:
			if(priorityList == null) {
				priorityList = new LinkedList<>(); // Create the list
				queues.put(priority, priorityList); // Add it associated with its priority
			}
			
			// Add content at the beginning:
			priorityList.add(0, content);
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		// Return if another, equal entry was found:
		return unique;
	}
	
	/**
	 * Returns the list from <code>queues</code> which is mapped to the 
	 * highest priority and not empty.
	 * @return List with elements of maximum priority or <code>null</code>
	 * if there is no non-empty list.
	 */
	private List<T> getHighestPrioritizedList() {
		List<T> maxPrioList = null;
		
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// If there are prioritized queues:
		if(!queues.isEmpty()) {
			int maxPriority = Integer.MIN_VALUE;
			
			// Iterate all priorities and determine the greatest:
			for(Integer currentPriority : queues.keySet()) {
				if(currentPriority > maxPriority) {
					List<T> currentList = queues.get(currentPriority);
					// Check if there is data in the list with current priority:
					if(currentList != null && !currentList.isEmpty()) {
						// Set the maximal list/priority at least temporarily:
						maxPrioList = currentList;
						maxPriority = currentPriority;
					}
				}
			}
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		return maxPrioList;
	}
	
	/**
	 * Returns the next object and removes it from the queue.
	 * The element with the highest priority will be returned first independent
	 * of its actual position.
	 * If priorities are equal, the element first inserted is returned.
	 * 
	 * @return The next object or <code>null</code> if the queue is empty.
	 */
	public T pop() {
		T nextEntry = null;
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Get the list of elements with the highest priority:
		List<T> maxPrioList = getHighestPrioritizedList();
		
		if(maxPrioList != null) {
			// Get the last entry of the determined list:
			int nextEntryIndex = maxPrioList.size() - 1;
			nextEntry = maxPrioList.get(nextEntryIndex);
			// Now remove the entry:
			maxPrioList.remove(nextEntryIndex);
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		return nextEntry;
	}
	
	/**
	 * Returns the next object without removing it from the queue.
	 * The element with the highest priority will be returned first independent
	 * of its actual position.
	 * If priorities are equal, the element first inserted is returned.
	 * 
	 * @return The next object or <code>null</code> if the queue is empty.
	 */
	public T next() {
		T nextEntry = null;
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Get the list of elements with the highest priority:
		List<T> maxPrioList = getHighestPrioritizedList();
		
		if(maxPrioList != null) {
			// Get the last entry of the determined list:
			int nextEntryIndex = maxPrioList.size() - 1;
			nextEntry = maxPrioList.get(nextEntryIndex);
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		return nextEntry;
	}
	
	/**
	 * Returns if the queue is empty.
	 * 
	 * @return <code>true</code> if the queue is empty. <code>false</code> if
	 * it is not.
	 */
	public boolean isEmpty() {
		boolean empty = true; // Initially assume the queue is empty
		
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Iterate the lists of all priorities:
		Iterator<List<T>> listIterator = queues.values().iterator();
		while(listIterator.hasNext() && empty) {
			List<T> currentList = listIterator.next();
			empty = currentList.isEmpty();
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		return empty;
	}
	
	/**
	 * Returns the count of all entries in the queue independent of their
	 * respective priority.
	 * @return The count of all entries in the queue.
	 */
	public int size() {
		int countOfEntries = 0;
		
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Iterate the lists of all priorities:
		for(List<T> currentList : queues.values()) {
			countOfEntries += currentList.size(); // Add the count of elements in this list
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		
		return countOfEntries;
	}
	
	public boolean contains(T obj) {
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Iterate the lists of all priorities:
		for(List<T> currentList : queues.values()) {
			if(currentList.contains(obj)) {
				queuesLock.unlock();
				return true;
			}
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
		return false;
	}
	
	/**
	 * Removes all entries from the queue.
	 */
	public void clear() {
		// Operations on the queue-map will be done, so lock the queues:
		queuesLock.lock();
		
		// Iterate the lists of all priorities:
		for(List<T> currentList : queues.values()) {
			currentList.clear();
		}
		
		// Operations have been done. Unlock the queues:
		queuesLock.unlock();
	}
}
