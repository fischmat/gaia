package sep.gaia.ui;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import sep.gaia.renderer.Mode3D;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.resources.locationsearch.Location;
import sep.gaia.resources.locationsearch.LocationSearch;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatVector3D;

/**
 * This class is to implement the user interface of the location search
 * function. It draws a JOptionPane which generates a JList of the matching
 * location names typed in by the user. After choosing one of the names, the
 * location is shown on the map.
 * 
 * @author Michael Mitterer, Fabian Buske, Johannes Bauer
 */
public class LocationWindow implements LoaderEventListener<Location> {
	
	private JOptionPane location = new JOptionPane();
	
	private JList<String> searchList;

	private LocationSearch search;
	
	private JDialog dialog;
	
	private DefaultListModel<String> resultsModel;

	/**
	 * Constructor for a new Window which lists the results of the location
	 * search.
	 * 
	 * A search for the location is fired and after the search results are
	 * complete, <code>onResourcesAvailable</code> is called.
	 */
	public LocationWindow() {
		resultsModel = new DefaultListModel<String>();
		searchList = new JList<String>(resultsModel);
		
		JScrollPane scrollPane = new JScrollPane(searchList);
		scrollPane.setPreferredSize(new Dimension(850, 300));
		scrollPane.setAlignmentX(JList.LEFT_ALIGNMENT);
		scrollPane.setVisible(true);
		
		Object[] message = {"Bitte wählen Sie eines der Suchergebnisse aus:", scrollPane};
		
		location = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_OPTION);
		dialog = location.createDialog("Suchergebnisse");
		dialog.setIconImage(IconFactory.getIcon("search.png"));
	}
	
	public void setSearch(LocationSearch search) {
		this.search = search;
	}

	@Override
	public void onResourcesAvailable(Collection<Location> resources) {
		GLState glState = (GLState) StateManager.getInstance().getState(
				StateType.GLState);
		float glZoom = glState.getZoom();
		
		resultsModel.clear();
		DefaultListModel<Location> resultsList = new DefaultListModel<Location>();
		
		for (Location loc : resources) {
			if (loc.getName() != null) {
				resultsModel.addElement(loc.getName());
				resultsList.addElement(loc);
			}
		}
		
		searchList.setSelectedIndex(0);
		
		if (!dialog.isVisible()) {
			dialog.setVisible(true);
		}
		
		if (location.getValue() instanceof Integer && (int) location.getValue() == 0) {
			Location location = resultsList.get(searchList.getSelectedIndex());
			float lon = location.getPosition()[0];
			float lat = location.getPosition()[1];

			// If currently in 3D-mode, switch to 2D-mode:
			if(!glState.is2DMode()) {
				glState.setZoom(Mode3D.MAX_3D_LEVEL);
				glState.zoom(-1);
			}
			
			FloatVector3D geoVector = new FloatVector3D(lat, lon, glZoom);
			FloatVector3D glVector = AlgoUtil.geoToGL(geoVector);
			glVector.setZ(0);
			
			glState.setPosition(glVector);
			glState.setZoom(8.0f);
			glState.updateBoundingBox();
						
		}
	}
	
	
}
