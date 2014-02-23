package sep.gaia.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collection;

import sep.gaia.renderer.layer.GLResource;
import sep.gaia.renderer.layer.MarkerAdapter;
import sep.gaia.renderer.layer.POIAdapter;
import sep.gaia.renderer.layer.WikipediaAdapter;
import sep.gaia.state.GLState;
import sep.gaia.ui.GAIAInfoBar;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.RayCast;

/**
 * Listener for processing mouse-hover events, such as the mouse being over a marker.
 * @author Matthias Fisch
 *
 */
public class MouseHoverListener implements MouseMotionListener {

	/**
	 * The state used by this listener to retrieve information about the
	 * current position, etc.
	 */
	private GLState state;
	
	/**
	 * The application windows info-bar.
	 */
	private GAIAInfoBar infoBar;
	
	/**
	 * The adapter responsible for calculating draw-areas of markers.
	 */
	private MarkerAdapter markerAdapter;
	
	/**
	 * The adapter responsible for calculating draw-areas of POIs.
	 */
	private POIAdapter poiAdapter;
	
	/**
	 * The adapter responsible for calculating draw-areas of Wikipedia-icons.
	 */
	private WikipediaAdapter wikiAdapter;
	
	/**
	 * Initializes the listener.
	 * @param state The state used by this listener to retrieve information about the
	 * current position, etc.
	 */
	public MouseHoverListener(GLState state, GAIAInfoBar infoBar, MarkerAdapter markerAdapter, POIAdapter poiAdapter, WikipediaAdapter wikiAdapter) {
		this.state = state;
		this.infoBar = infoBar;
		this.markerAdapter = markerAdapter;
		this.poiAdapter = poiAdapter;
		this.wikiAdapter = wikiAdapter;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(state != null) {
			int canvasWidth = GaiaCanvas.getInstance().getWidth();
			int canvasHeight = GaiaCanvas.getInstance().getHeight();
			
			FloatBoundingBox box = state.getBoundingBox();
			float viewWidth = box.getWidth();
			float viewHeight = box.getHeight();
			FloatVector3D position = state.getPosition();
			FloatVector3D rotation = state.getRotation();
			
			FloatVector3D hoverPos = RayCast.linearCast(e.getX(), e.getY(), canvasWidth, canvasHeight, viewWidth, viewHeight, position, rotation);
			hoverPos.setZ(state.getZoom());
			
			boolean showInfo = false;
			
			if(state.is2DMode()) {
				if(markerAdapter != null) {
					Collection<GLResource> markerResources = markerAdapter.getGLResources();
					for(GLResource resource : markerResources) {
						FloatBoundingBox drawnArea = resource.getBox();
						
						if(drawnArea.contains(hoverPos)) {
							showInfo = true;
							infoBar.setStatusMessage(resource.getName());
						}
					}
				}
				
				if(poiAdapter != null) {
					Collection<GLResource> poiResources = poiAdapter.getGLResources();
					for(GLResource resource : poiResources) {
						FloatBoundingBox drawnArea = resource.getBox();
						
						if(drawnArea.contains(hoverPos)) {
							showInfo = true;
							infoBar.setStatusMessage(resource.getName());
						}
					}
				}
				
				if(wikiAdapter != null) {
					Collection<GLResource> wikiResources = wikiAdapter.getGLResources();
					for(GLResource resource : wikiResources) {
						FloatBoundingBox drawnArea = resource.getBox();
						
						if(drawnArea.contains(hoverPos)) {
							showInfo = true;
							infoBar.setStatusMessage(resource.getName());
						}
					}
				}
				
				float height = getHeight(state.getZoom())*1000;
				FloatVector3D geoPosition = AlgoUtil.glToGeo(hoverPos);
				infoBar.setPosition(geoPosition.getX(), geoPosition.getY(), height);
			}
			
			if(!showInfo) {
				infoBar.setStatusMessage("");
			}
		}
	}

	private float getHeight(float glZoom) {
		FloatVector3D left = new FloatVector3D(0, 0, 0);
		FloatVector3D right = new FloatVector3D(glZoom, 0, glZoom);
		
		return AlgoUtil.calculateGeoDistance(left, AlgoUtil.glToGeo(right));
	}
}
