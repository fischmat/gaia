package sep.gaia.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes a query to be performed by a loader (s. <code>AbstractLoader</code>).
 * This bean-class stores dummy-resources to be filled by the respective loading entity.
 * If additional information is required for the query to be performed, it can be provided
 * by subclassing this type.
 * @author Matthias Fisch
 *
 */
public class Query {

	/**
	 * Beanclass associating a resource with a priority. Higher values imply that the
	 * resource is preferably loaded.
	 * The class is comparable by the priority of the resource stored in it.
	 * @author Matthias Fisch
	 *
	 */
	private class PrioritizingEntry implements Comparable<PrioritizingEntry> {

		/**
		 * The priority entries are set to if none is specified.
		 */
		public static final int DEFAULT_PRIORITY = 0;
		
		/**
		 * The resource contained in this entry.
		 */
		private DataResource resource;

		/**
		 * The priority contained in this entry.
		 */
		private int priority;


		/**
		 * Initializes the entry by specifying the contained resource and its priority.
		 * @param resource The resource contained.
		 * @param priority The priority of the resource.
		 */
		public PrioritizingEntry(DataResource resource, int priority) {
			super();
			this.resource = resource;
			this.priority = priority;
		}

		/**
		 * Returns the resource contained.
		 * @return The resource contained.
		 */
		public DataResource getResource() {
			return resource;
		}


		/**
		 * Sets the resource contained.
		 * @param resource The resource contained.
		 */
		public void setResource(DataResource resource) {
			this.resource = resource;
		}

		/**
		 * Returns the priority of the resource contained.
		 * @return The priority of the resource contained.
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * Sets the priority of the resource contained.
		 * @param priority The priority of the resource contained.
		 */
		public void setPriority(int priority) {
			this.priority = priority;
		}

		@Override
		public int compareTo(PrioritizingEntry o) {
			return o.getPriority() - priority;
		}
	}

	/**
	 * Dummy-resources to be filled by the loader processing the query.
	 */
	private Set<PrioritizingEntry> resources = new HashSet<>();

	/**
	 * Initializes by a collection of dummy-resources to be filled with the
	 * information gathered by the query.
	 * @param resources The collection of dummy-resources to be filled with the
	 * information gathered by the query. Default-priority is assumed for all entries in the
	 * collection.
	 * @throws NotADummyException Thrown if any resource in <code>resources.keySet()</code>
	 * does not have the dummy-flag set.
	 */
	public Query(Collection<DataResource> resources) throws NotADummyException {
		// Iterate all resources:
		for(DataResource resource : resources) {
			// The resource must be a dummy:
			if(resource.isDummy()) {
				// Add entry:
				this.resources.add(new PrioritizingEntry(resource, PrioritizingEntry.DEFAULT_PRIORITY));
				
			} else {
				throw new NotADummyException("Resources in queries must be dummy.");
			}
		}
	}

	/**
	 * Initializes by a collection of dummy-resources to be filled with the
	 * information gathered by the query.
	 * @param resources The collection of dummy-resources to be filled with the
	 * information gathered by the query mapped to their respective priority.
	 * @throws NotADummyException Thrown if any resource in <code>resources.keySet()</code>
	 * does not have the dummy-flag set.
	 */
	public Query(Map<DataResource, Integer> resources) throws NotADummyException {
		// Iterate all keys:
		for(DataResource resource : resources.keySet()) {
			// The resource must be a dummy:
			if(resource.isDummy()) {
				// Get the priority of the current resource:
				int priority = resources.get(resource);
				// Add entry:
				this.resources.add(new PrioritizingEntry(resource, priority));
				
			} else {
				throw new NotADummyException("Resources in queries must be dummy.");
			}
		}
		
	}

	/**
	 * Returns the dummy-resources contained in the query sorted by priority.
	 * The first resources are those with highest priority.
	 * @return The dummy-resources contained in the query.
	 */
	public Collection<DataResource> getResourcesByPriority() {
		// Convert the set of prioritized entries to a list:
		List<PrioritizingEntry> tempList = new ArrayList<>(resources);
		
		// Now sort the list:
		Collections.sort(tempList);
		
		// And convert again into a list of resources by taking only this member from the entries:
		List<DataResource> list = new LinkedList<>();
		for(PrioritizingEntry currentEntry : tempList) {
			list.add(currentEntry.getResource());
		}
		return list;
	}


	/**
	 * Returns the dummy-resources contained in the query.
	 * @return The dummy-resources contained in the query.
	 */
	public Collection<DataResource> getResources() {
		// Convert into a list of resources by taking only this member from the entries:
		List<DataResource> list = new LinkedList<>();
		for(PrioritizingEntry currentEntry : resources) {
			list.add(currentEntry.getResource());
		}
		return list;
	}
	
	/**
	 * Sets the dummy-resources contained in the query. Default priority is assumed.
	 * @param resources The dummy-resources contained in the query.
	 * @throws NotADummyException Thrown if any resource in <code>resources</code>
	 * hasn't the dummy-flag set.
	 */
	protected void setResources(Collection<DataResource> resources) throws NotADummyException {
		// Iterate all passed resources:
		for(DataResource resource : resources) {
			// Resources must be dummy:
			if(resource.isDummy()) {
				// Add new entry with default priority:
				this.resources.add(new PrioritizingEntry(resource, PrioritizingEntry.DEFAULT_PRIORITY));
				
			} else {
				throw new NotADummyException("Resources in queries must be dummy.");
			}
		}
	}

	/**
	 * Adds <code>resource</code> with a priority of zero.
	 * The passed object must be a dummy-resource (see <code>DataResource.isDummy()</code>).
	 * @param resource The resource to be added.
	 * @param priority The priority of the resource to add.
	 * @throws NotADummyException Thrown if <code>resource</code> does not have the dummy-flag set.
	 */
	public void addResource(DataResource resource, int priority) throws NotADummyException {
		// Resource must be dummy:
		if(resource != null && resource.isDummy()) {
			// Add the resource:
			this.resources.add(new PrioritizingEntry(resource, priority));
			
		} else {
			throw new NotADummyException("Resources in queries must be dummy.");
		}
	}
}
