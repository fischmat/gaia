package sep.gaia.controller.locationsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import sep.gaia.resources.locationsearch.Location;
import sep.gaia.resources.markeroption.MarkerResource;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

/**
 * This is to implement the <code>ActionListener</code> Interface to check if
 * the user wants to jump to one of the search results in the view by selecting
 * an item in the resulting list.
 * 
 * @author Michael Mitterer
 */
public class LocationSearchListener implements ActionListener {
	/** 
	 * The <code>GLState</code> reference.
	 */
	private GLState state;
	private JList<String> list;
	private long time;
	private DefaultListModel<Location> locationList;
	
	/**
	 * LocationSearchListener constructor
	 * 
	 * @param state The current <code>GLState</code>
	 */
	public LocationSearchListener(JList<String> list, DefaultListModel<Location> locationList, GLState state) {
		this.state = state;
		this.list = list;
		this.locationList = locationList;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "OK") {
			Location location = locationList.get(list.getSelectedIndex());
			FloatVector3D vector = new FloatVector3D(location.getPosition()[0], location.getPosition()[1], 0);
			state.setPosition(AlgoUtil.geoToGL(vector));
			state.updateBoundingBox();
		}
	}

}
