package sep.gaia.resources;

import java.io.Serializable;

/**
 * Least common denominator of all resources in GAIA.
 * 
 * Each data resource is extending this class.
 * 
 * @author Johannes Bauer (Spezifikation: Matthias Fisch)
 * 
 */
public abstract class DataResource implements Serializable {
	private static final long serialVersionUID = 1589958494141846840L;

	/**
	 * Dummy flag - necessary for "consuments" to know whether a
	 * <code>DataResource</code> instance is complete (contains all content).
	 */
	private boolean isDummy;
	
	/**
	 * The resources timestamp. This may be either a logical or a UNIX-timestamp dependent
	 * on the implementing class.
	 */
	private long timeStamp;

	/**
	 * Initializes a resources with the current timestamp (see <code>incrementTimestamp()</code>).
	 * The dummy-flag will be set to identify the resource as invalid.
	 */
	protected DataResource() {
		this.timeStamp = incrementTimestamp();
	}
	
	/**
	 * Returns the next (logical) timestamp for this object. This timestamp is needed
	 * for creating a happen-before-relation between different
	 * <code>DataResource</code> instances.
	 * 
	 * @return
	 */
	protected abstract long incrementTimestamp();

	/**
	 * Returns the timestamp for this object. This timestamp is needed
	 * for creating a happen-before-relation between different
	 * <code>DataResource</code> instances.
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timeStamp;
	}
	
	/**
	 * Sets the timestamp of this resource. This may either be a logical or
	 * UNIX-timestamp used to create a happened-before-relationship between resources.
	 * In both cases the semantics must be defined in a way fulfilling the following condition:
	 * If <i>t1</i> is the timestamp of <i>r1</i> and <i>t2</i> the timestamp of <i>r2</i>, then
	 * <i>t1</i> &lt; <i>t2</i> implies that <i>r1</i> was created before </i>r2</i>.
	 * @param timeStamp The timestamp of the resource.
	 */
	protected void setTimestamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Return whether this object is a dummy. Necessary for other classes that
	 * have access to this object in order to determine whether this object
	 * contains content yet.
	 * 
	 * @return True, if this object has no content (is a dummy), else returns
	 *         false.
	 */
	public boolean isDummy() {
		return isDummy;
	}

	/**
	 * Marks this object as a dummy so that other class can see, that this
	 * object contains no content yet.
	 * 
	 * @param isDummy
	 *            If true, the object is marked as a dummy. Else not.
	 */
	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	/**
	 * Returns the identifier, which uniquely identifies a
	 * <code>DataResource</code> object.
	 * 
	 * @return The identifier.
	 */
	public abstract String getKey();
}
