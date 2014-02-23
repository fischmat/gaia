package sep.gaia.state;

import java.util.HashMap;
import java.util.Map;

import sep.gaia.util.AlgoUtil;

/**
 * 
 * @author Johannes Bauer
 * 
 */
public class StateManager implements AbstractStateManager {

	/**
	 * The states to be managed.
	 */
	private Map<StateType, State> states;

	/**
	 * The unique instance of <code>this</code>.
	 */
	private static AbstractStateManager instance;

	private static boolean isInitialized;
	
	/**
	 * Generates a new <code>ConcreteStateManager</code>.
	 * 
	 * @param states
	 *            the set of the states that <code>this</code> has to manage
	 */
	private StateManager(GLState glState) {
		this.states = new HashMap<>();

		// Create states.
		GeoState geoState = new GeoState(1024, 0, 0, null);
		//AlgoUtil.glToGeo(glState, geoState);
		TileState tileState = new TileState(0, null, 0, 0);
		AlgoUtil.glToTile(glState, tileState);

		states.put(StateType.GLState, glState);
		states.put(StateType.TileState, tileState);
		states.put(StateType.GeoState, geoState);
		
		isInitialized = true;
	}

	/**
	 * Gets the unique instance object. If it is not existent, this method
	 * creates one.
	 * 
	 * @return the unique instance of <code>this</code>
	 */
	public static AbstractStateManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					"No initial GLState object overgiven.");
		}
		return instance;
	}

	public static AbstractStateManager getInstance(GLState state) {
		if (instance == null) {
			instance = new StateManager(state);
		}

		return instance;
	}

	/**
	 * Method to be invoked when the given argument <code>stateType</code> has
	 * changed. All other <code>State</code> objects managed by
	 * <code>this</code> will be updated in order to match the change.
	 * 
	 * @param stateType
	 *            the <code>StateType</code> object that has changed
	 */
	public void stateChanged(StateType stateType) {
		if (stateType == StateType.GeoState) {

			this.geoStateChanged();
		} else if (stateType == StateType.GLState) {

			this.glStateChanged();
		} else {

			this.tileStateChanged();
		}

	}

	private void tileStateChanged() {

		/*GeoState newGeoState = AlgoUtil.tileToGeo((TileState) this.states
				.get(StateType.TileState));
		GLState newGLState = AlgoUtil.tileToGL((TileState) this.states
				.get(StateType.TileState));
		GLState oldGLState = (GLState) this.states.get(StateType.GLState);

		// Update all members not actualized by AlgorithmUtility
		newGLState.setModelViewMatrix(oldGLState.getModelViewMatrix());
		newGLState.setProjectionMatrix(oldGLState.getProjectionMatrix());
		newGLState.setViewPort(oldGLState.getViewPort());
		newGLState.setxRotation(oldGLState.getxRotation());
		newGLState.setyRotation(oldGLState.getyRotation());

		this.states.put(StateType.GeoState, newGeoState);
		this.states.put(StateType.GLState, newGLState);*/
		/*
		TileState tileState = (TileState) getState(StateType.TileState);
		GeoState geoState = (GeoState) getState(StateType.GeoState);
		GLState glState = (GLState) getState(StateType.GLState);
		
		AlgoUtil.tileToGeo(tileState, geoState);
		AlgoUtil.tileToGL(tileState, glState);*/
	}

	private void glStateChanged() {
		
		GLState glState = (GLState) getState(StateType.GLState);
		GeoState geoState = (GeoState) getState(StateType.GeoState);
		TileState tileState = (TileState) getState(StateType.TileState);
		
		AlgoUtil.glToGeo(glState, geoState);
		AlgoUtil.glToTile(glState, tileState);
		
		glState.notifyStateObservers();
		geoState.notifyStateObservers();
		tileState.notifyStateObservers();
	}

	private void geoStateChanged() {

		/*GLState newGLState = AlgoUtil.geoToGL((GeoState) this.states
				.get(StateType.GeoState));
		TileState newTileState = AlgoUtil.geoToTile((GeoState) this.states
				.get(StateType.GeoState));
		GLState oldGLState = (GLState) this.states.get(StateType.GLState);

		// Update all members not actualized by AlgorithmUtility
		newGLState.setModelViewMatrix(oldGLState.getModelViewMatrix());
		newGLState.setProjectionMatrix(oldGLState.getProjectionMatrix());
		newGLState.setViewPort(oldGLState.getViewPort());
		newGLState.setxRotation(oldGLState.getxRotation());
		newGLState.setyRotation(oldGLState.getyRotation());

		this.states.put(StateType.GLState, newGLState);
		this.states.put(StateType.TileState, newTileState);*/
		
		GLState glState = (GLState) getState(StateType.GLState);
		GeoState geoState = (GeoState) getState(StateType.GeoState);
		TileState tileState = (TileState) getState(StateType.TileState);

		AlgoUtil.geoToGL(geoState, glState);
		AlgoUtil.geoToTile(geoState, tileState);
		
		geoState.notifyStateObservers();
		glState.notifyStateObservers();
		tileState.notifyStateObservers();
	}

	public static boolean isInitialized() {
		return isInitialized;
	}
	
	@Override
	public State getState(StateType type) {
		return this.states.get(type);

	}

}