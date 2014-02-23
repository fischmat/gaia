package sep.gaia.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import sep.gaia.renderer.Mode3D;
import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.state.StateManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to switch the view to a marker which he/she set before by
 * pressing an item in the list or delete or rename a marker in the markerbar.
 * 
 * @author Johannes Bauer, Michael Mitterer
 */
public class MarkerBarListener extends MouseAdapter {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	private JList<String> list;
	private DefaultListModel<MarkerResource> markerList;
	
	public static int objectNumber;
	private long time;
	
	
	/**
	 * MarkerBarListener constructor
	 * @param markerList 
	 * 
	 * @param state The current <code>GLState</code>
	 */
	public MarkerBarListener(JList<String> list, DefaultListModel<MarkerResource> markerList, GLState state) {
		this.state = state;
		this.list = list;
		this.markerList = markerList;
		objectNumber = list.getSelectedIndex();
		//markerList.g
	}
	
	//@Override
	/*public void actionPerformed(ActionEvent arg0) {
		arg0.
	}*/

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() > 1) {
			//System.out.println(markerList.get(list.getSelectedIndex()).getName());
			MarkerResource marker = markerList.get(list.getSelectedIndex());
			FloatVector3D vector = new FloatVector3D(marker.getLon(), marker.getLat(), marker.getZoom());
			
			// If currently in 3D-mode, switch to 2D-mode:
			if(!state.is2DMode()) {
				state.setZoom(Mode3D.MAX_3D_LEVEL);
				state.zoom(-1);
			}
			
			// Set the new position, but don't update, because we will change also the zoom:
			state.setPosition(vector, false);
			// Set the new zoom and invoke update now:
			state.setZoom(marker.getZoom());
		} else {
			objectNumber = list.getSelectedIndex();
		}
	}
}
