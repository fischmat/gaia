package sep.gaia.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import sep.gaia.renderer.layer.CompassAdapter;
import sep.gaia.renderer.layer.WeatherAdapter;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.state.StateManager;
import sep.gaia.state.StateObserver;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.RayCast;

/**
 * TODO
 * @author Johannes Bauer
 */
public class MouseDraggedListener2d implements MouseMotionListener, MouseListener {

	private GLState glState;
	
	private Set<StateObserver> observerBackup = new HashSet<>();
	
	private int oldX = -1;
	private int oldY = -1;
	
	private boolean mouseDragged = false;
	
	public MouseDraggedListener2d(GLState glState) {
		this.glState = glState;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {	
		
		// If the button was just pressed:
		if(oldX == -1 && oldY == -1) {
			
			// Unregister all unnecessary observers and remember them,
			// in order to prevent them to invoke block unnecessary:
			for(StateObserver observer : glState.getObservers()) {
				if(!(observer instanceof TileManager) && !(observer instanceof CompassAdapter) 
						&& !(observer instanceof WeatherAdapter)) {
					
					observerBackup.add(observer);
					glState.unregister(observer);
				}
			}
		}
		
		if (!glState.is2DMode()) {
			return;
		}
		
		int x = e.getX();
		int y = e.getY();
		
		int dX = 0;
		int dY = 0;

		if (oldX > -1 && oldY > -1) {
			dX = oldX - x;
			dY = y - oldY;
			
			// Convert the distance moved in window to GL-coordinates:
			int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
			final float glMoveX = AlgoUtil.glCoordsPerPixelRange(dX, tileZoom);
			final float glMoveY = AlgoUtil.glCoordsPerPixelRange(dY, tileZoom);
			
			float rotationZ = glState.getRotation().getZ();
			FloatVector3D direction = new FloatVector3D(glMoveX, glMoveY, 0);
			direction.rotateAroundZ(-rotationZ);
			
			// Translate the current position:
			glState.translate(direction);			
		}
		
		oldX = x;
		oldY = y;
		
		mouseDragged = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mouseDragged) {
			mouseDragged = false;
			
			oldX = -1;
			oldY = -1;
			
			// Re-register the remembered observers:
			for(StateObserver observer : observerBackup) {
				glState.register(observer);
			}
			
			// Empty the remembered observers:
			observerBackup.clear();
			
			glState.notifyManager();
			glState.notifyStateObservers();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
