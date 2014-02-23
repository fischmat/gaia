package sep.gaia.state.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.StateManager;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.state.State;
import sep.gaia.state.TileState;
import sep.gaia.util.AlgoUtil;

public class ConcreteStateManagerTest {

	private static StateManager manager;
	private static Set<State> states = new LinkedHashSet<State>();
	private static GLState glState;
	private static GeoState geoState;
	private static TileState tileState;

	
	@BeforeClass
	/**
	 * Initializes the GL state with zoom level 20 and the other state
	 * representations as <code>null</code> values in order for
	 * <code>manager</code> to update them later.
	 */
	public static void initStates() {		
		glState = new GLState(20); // a GLState with zoom level 20
		
		manager = (StateManager) StateManager.getInstance(glState);
		
		/* initialize the other states with default values */
		geoState = new GeoState(0, 0, 0, null);
		tileState = new TileState(0, null, 0, 0);
		
	}
	
	@Test
	/**
	 * Tests if the <code>TileState</code> and the <code>GeoState</code>
	 * saved in <code>manager</code> are updated after the invocation
	 * of <code>stateChanged</code>.
	 */
	public void testStateChanged() {
				
		/*
		 * tileState and geoState are expected to be updated because the
		 * manager 'thinks' that glState has changed
		 */
		manager.stateChanged(StateType.GLState);
		
		AlgoUtil.glToGeo(glState, geoState);
		AlgoUtil.glToTile(glState, tileState);
		
		boolean glToGeoWorked = 
			geoState.getZoom() == ((GeoState) manager.getState(StateType.GeoState)).getZoom();
		boolean glToTileWorked =
			tileState.getZoom() == ((TileState) manager.getState(StateType.TileState)).getZoom();
		
		assertTrue(glToTileWorked && glToGeoWorked);
		
		manager.stateChanged(StateType.GeoState);
		manager.stateChanged(StateType.TileState);
		
		GLState gState = new GLState(0);
		AlgoUtil.geoToGL((GeoState) manager.getState(StateType.GeoState), gState);
		
		assertEquals(gState, manager.getState(StateType.GLState));
	}
}
