package sep.gaia.controller;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import javax.media.opengl.awt.GLCanvas;

import sep.gaia.renderer.layer.WikipediaAdapter;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.markeroption.MarkerResourceManager;
import sep.gaia.resources.wikipedia.WikipediaData;
import sep.gaia.resources.wikipedia.WikipediaManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.ui.WikipediaWindow;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.RayCast;

/**
 * This is to check if the user has pressed on a wikipedia symbol or on the
 * compass in the canvas or if the user wants to set a marker after selecting
 * the marker button in the toolbar.
 * 
 * If a wikipedia symbol was chosen, a wikipedia short description window pops up.
 * If the user selected the compass, the upper / lower half of the canvas is shown
 * in North / South direction.
 * 
 * @author Michael Mitterer
 */
public class MouseClickedListener implements MouseListener {
	public static boolean markerPressed = false;
	private static GLCanvas canvas;
	private static Cursor cursor;
	private static final float WIKIPEDIA_SIDE_LEN = 0.04f;
	
	public static void setCursor() {
		if (markerPressed) {
			canvas.setCursor(cursor);
		} else {
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	}
	
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState glState;
	
	/**
	 * MouseClickedListener constructor
	 * 
	 * @param glState The current <code>GLState</code>
	 * @param glCanvas The <code>GLCanvas</code> reference
	 */
	public MouseClickedListener(GLState glState, GLCanvas glCanvas) {
		this.glState = glState;
		canvas = glCanvas;

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image markerImage = toolkit.getImage(getClass().getResource(
				"/sep/gaia/renderer/icons/20/markerSetter.png"));
		cursor = toolkit.createCustomCursor(markerImage,
				new Point(canvas.getX(), canvas.getY()), "Marker cursor image");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		/*FloatVector3D upperleft = glState.getBoundingBox().getUpperLeft();
		FloatVector3D lowerright = glState.getBoundingBox().getLowerRight();
		float distance1 = Math.abs(lowerright.getX()-upperleft.getX());
		float distance2 = Math.abs(lowerright.getY()-upperleft.getY());
		
		FloatVector3D vector = new FloatVector3D(AlgoUtil.glCoordsPerPixelRange(
				e.getX(), canvas.getWidth(), distance1) + upperleft.getX(), -AlgoUtil.glCoordsPerPixelRange(
						e.getY(), canvas.getHeight(), distance2) + upperleft.getY(), glState.getZoom());*/
		
		
		int canvasWidth = GaiaCanvas.getInstance().getWidth();
		int canvasHeight = GaiaCanvas.getInstance().getHeight();
		
		FloatBoundingBox box = glState.getBoundingBox();
		FloatVector3D position = glState.getPosition();
		FloatVector3D rotation = glState.getRotation();
		
		FloatVector3D pos = RayCast.linearCast(e.getX(), e.getY(), canvasWidth, canvasHeight, box.getWidth(), box.getHeight(), position, rotation);
		
		
		
		/*boolean normalBoundingBox = (glState.getOrthogonalBBox().getUpperLeft()
				.getX() == upperleft.getX()) && (glState.getOrthogonalBBox().getUpperLeft()
						.getY() == upperleft.getY());*/
		
		// Check if compass was pressed (lower 48x48px area):
		if(e.getX() >= canvas.getWidth() - 48 && e.getY() >= canvas.getHeight() - 48) {
			glState.setRotation(new FloatVector3D(0, 0, 0));
			
			
		} else if (markerPressed && glState.is2DMode()) {
			if (glState.is2DMode()) {
				MarkerResourceManager markerManager = (MarkerResourceManager) ResourceMaster
						.getInstance().getResourceManager("Marker");
	
				FloatVector3D clickPos = getClickedPositionGl(e);
				clickPos.setZ(0);
				
				markerManager.addMarker(clickPos, glState.getZoom());
				markerPressed = false;
			} else {
				markerPressed = false;
			}
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			
			// Invoke update by states:
			glState.notifyManager();
			
			
		} else if (glState.is2DMode() && AlgoUtil.glToTileZoom(glState.getZoom()) >= 11) {
			
			int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
			
			WikipediaManager wikiManager = (WikipediaManager) ResourceMaster.getInstance().getResourceManager("Wikipedia");
			
			if (wikiManager.getCurrentWikipediaDatas() != null) {
				
				for(WikipediaData resource : wikiManager.getCurrentWikipediaDatas()) {
					FloatBoundingBox symbolBox = WikipediaAdapter.calculateSymbolBox(resource, tileZoom);
					
					if(symbolBox.contains(pos)) {
						new WikipediaWindow(resource.getSummaryText(), resource.getName());
						break;
					}
				}
			}
		}
	}
	
	private static FloatVector3D getClickedPositionGl(MouseEvent e) {
		int canvasWidth = GaiaCanvas.getInstance().getWidth();
		int canvasHeight = GaiaCanvas.getInstance().getHeight();
		
		GLState state = (GLState) StateManager.getInstance().getState(StateType.GLState);
		
		FloatVector3D position = state.getPosition();
		FloatVector3D rotation = state.getRotation();
		
		FloatBoundingBox box = state.getOrthogonalBBox();
		float viewWidth = box.getWidth();
		float viewHeight = box.getHeight();
		
		FloatVector3D intersect = RayCast.linearCast(e.getX(), e.getY(), canvasWidth, canvasHeight, viewWidth, viewHeight, position, rotation);
		
		return intersect;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
}