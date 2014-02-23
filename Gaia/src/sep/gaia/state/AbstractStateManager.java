package sep.gaia.state;

import java.util.Map;

/**
 * Interface to manage the different categories of states by updating all
 * states whenever one of them is changed.
 * An implementing class has to be a Singleton.
 * 
 * @author Max Witzelsperger
 *
 */
public interface AbstractStateManager {

	/**
	 * Enumerates all possible types of concrete states to be managed.
	 * @author Matthias Fisch
	 *
	 */
	public enum StateType {GLState, GeoState, TileState}	
	
	/**
	 * Returns the state of type <code>type</code> managed.
	 * @param type The type of the state to return.
	 * @return The state of type <code>type</code> or <code>null</code>
	 * if there is none.
	 */
	public State getState(StateType type);
	
}