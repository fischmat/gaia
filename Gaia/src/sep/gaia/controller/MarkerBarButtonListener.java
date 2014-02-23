package sep.gaia.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.resources.markeroption.MarkerResourceManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

public class MarkerBarButtonListener implements ActionListener {

	private DefaultListModel<MarkerResource> markerList;
	
	public MarkerBarButtonListener(DefaultListModel<MarkerResource> markerList) {//GeoState state) {
		this.markerList = markerList;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (MarkerBarListener.objectNumber < markerList.getSize() && MarkerBarListener.objectNumber >= 0) {
			MarkerResourceManager markerResource = (MarkerResourceManager) ResourceMaster.getInstance().getResourceManager("Marker");
			MarkerResource marker = markerList.get(MarkerBarListener.objectNumber);
			FloatVector3D vector = new FloatVector3D(marker.getLat(), marker.getLon(), marker.getZoom());
			
			if (arg0.getActionCommand() == "Umbenennen") {
				String input = JOptionPane.showInputDialog("Bitte einen neuen Namen für den Marker eingeben:");
				if (!input.isEmpty() && !input.startsWith(" ")) {
					markerResource.renameMarker(vector, AlgoUtil.glToTileZoom(marker.getZoom()), input, MarkerBarListener.objectNumber);
				}
			} else if (arg0.getActionCommand() == "Löschen") {
				markerResource.removeMarker(vector, AlgoUtil.glToTileZoom(marker.getZoom()), MarkerBarListener.objectNumber);
				// Invoke updates by states:
				StateManager.getInstance().getState(StateType.GLState).notifyManager();
			}
		}
	}
}
