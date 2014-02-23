package sep.gaia.state;

/**
 * Interface to be implemented in order to observe instances of subclasses of
 * <code>State</code>. A class which implements this interface will be informed whenever
 * an observed instance of a subclass of <code>State</code> is changed.
 * 
 * @author Max Witzelsperger
 *
 */
public interface StateObserver {
	
	/**
	 * This method specifies what action is taken when the observed
	 * <code>State</code> object is changed.
	 * 
	 * @param state the observed <code>State</code> object that has
	 * changed
	 */
	public void onUpdate(State state);
	
}
