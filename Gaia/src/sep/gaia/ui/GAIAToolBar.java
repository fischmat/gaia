package sep.gaia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sep.gaia.controller.KeyboardLocationAdapter;
import sep.gaia.controller.menubar.WeatherMenubarListener;
import sep.gaia.controller.toolbar.ComponentHideListener;
import sep.gaia.controller.toolbar.MarkerToolbarListener;
import sep.gaia.controller.toolbar.RotationDownListener;
import sep.gaia.controller.toolbar.RotationUpListener;
import sep.gaia.controller.toolbar.ScreenshotToolbarListener;
import sep.gaia.controller.toolbar.SearchToolbarListener;
import sep.gaia.controller.toolbar.ZoomInListener;
import sep.gaia.controller.toolbar.ZoomOutListener;
import sep.gaia.renderer.layer.ScreenshotLayer;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.StateManager;
import sep.gaia.util.FloatVector3D;

/**
 * The toolbar.
 * 
 * @author Johannes Bauer, Michael Mitterer
 *
 */
public class GAIAToolBar extends JPanel {

	public GAIAToolBar(MarkerPanel markerPanel, PoiBar poiBar, ScreenshotLayer screenshotLayer) {
		super();
		final GLState glState = (GLState) StateManager.getInstance().getState(
				StateType.GLState);

		setLayout(new BorderLayout());
		JPanel leftTools = new JPanel(new FlowLayout());
		JPanel rightTools = new JPanel(new FlowLayout());
		
		// Marker show button.
		ImageIcon markerListIcon = createIcon(
				"/sep/gaia/renderer/icons/32/markerlist.png",
				"Eigene Orte anzeigen/verstecken");
		JButton markerPanelVisibilityButton = new JButton(markerListIcon);
		markerPanelVisibilityButton.setToolTipText("Markerliste ein-/ausblenden");
		markerPanelVisibilityButton
				.addActionListener(new ComponentHideListener(markerPanel));

		// POI show button.
		ImageIcon poibarIcon = createIcon(
				"/sep/gaia/renderer/icons/32/poibar.png",
				"POI bar anzeigen/verstecken");
		JButton poiBarVisibilityButton = new JButton(poibarIcon);// "res/icon16/poibar.png"));
		poiBarVisibilityButton.setToolTipText("POI Bar aus-/einblenden");
		poiBarVisibilityButton.addActionListener(new ComponentHideListener(
				poiBar));

		// Weather button.
		ImageIcon weatherIcon = createIcon(
				"/sep/gaia/renderer/icons/32/weather.png",
				"Wetter anzeigen/verstecken");
		JButton weatherButton = new JButton(weatherIcon);
		weatherButton.addActionListener(new WeatherMenubarListener());
		weatherButton.setToolTipText("Wetter anzeigen/ausblenden");

		// Screenshot button.
		ImageIcon screenshotIcon = createIcon(
				"/sep/gaia/renderer/icons/32/screenshot.png",
				"Screenshot des Kartenausschnitts erstellen");
		JButton screenshotButton = new JButton(screenshotIcon);// "res/icon16/screenshot.png"));
		screenshotButton.setToolTipText("Screenshot erstellen");
		screenshotButton.addActionListener(new ScreenshotToolbarListener(screenshotLayer));

		// Marker button.
		ImageIcon markerIcon = createIcon(
				"/sep/gaia/renderer/icons/32/marker.png", "Markieren von Orten");
		JButton markerButton = new JButton(markerIcon);
		markerButton.setToolTipText("Marker setzen");
		markerButton.addActionListener(new MarkerToolbarListener(glState, markerPanel));

		ImageIcon cwIcon = createIcon(
				"/sep/gaia/renderer/icons/32/rotate_clockwise_z_2d.png", "Karte im Uhrzeigersinn drehen");
		JButton rotateClockwiseButton = new JButton(cwIcon);
		rotateClockwiseButton.setToolTipText("Karte im Uhrzeigersinn drehen");
		rotateClockwiseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				glState.rotate(new FloatVector3D(0, 0, 10));

			}
		});

		ImageIcon ccwIcon = createIcon(
				"/sep/gaia/renderer/icons/32/rotate_cclockwise_z_2d.png", "Karte gegen den Uhrzeigersinn drehen");
		JButton rotateCounterClockwiseButton = new JButton(ccwIcon);
		rotateCounterClockwiseButton.setToolTipText("Karte gegen den Uhrzeigersinn drehen");
		rotateCounterClockwiseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				glState.rotate(new FloatVector3D(0, 0, -10));

			}
		});
		
		ImageIcon upIcon = createIcon(
				"/sep/gaia/renderer/icons/32/rotate_up_x_2d.png", "Karte oben um die X Achse drehen");
		JButton rotateUpXButton = new JButton(upIcon);
		rotateUpXButton.setToolTipText("Karte nach vorne kippen");
		rotateUpXButton.addActionListener(new RotationUpListener(glState));
		ImageIcon downIcon = createIcon(
				"/sep/gaia/renderer/icons/32/rotate_down_x_2d.png", "Karte unten um die X Achse drehen");
		JButton rotateDownXButton = new JButton(downIcon);
		rotateDownXButton.setToolTipText("Karte nach hinten kippen");
		rotateDownXButton.addActionListener(new RotationDownListener(glState));
		// Zoom out button.
		ImageIcon zoomOutIcon = createIcon(
				"/sep/gaia/renderer/icons/32/zoomneg.png", "Hinauszoomen");
		JButton zoomOutButton = new JButton(zoomOutIcon);
		zoomOutButton.setToolTipText("Hinauszoomen");
		zoomOutButton.addActionListener(new ZoomOutListener(glState));

		// Zoom in button.
		ImageIcon zoomInIcon = createIcon(
				"/sep/gaia/renderer/icons/32/zoompos.png", "Hineinzoomen");
		JButton zoomInButton = new JButton(zoomInIcon);
		zoomInButton.setToolTipText("Hineinzoomen");
		zoomInButton.addActionListener(new ZoomInListener(glState));
		 
		// Style combo box.
		StyleComboBox styleBox = new StyleComboBox(glState);
		styleBox.setPreferredSize(new Dimension(170, 30));
		styleBox.setToolTipText("Kartenauswahl");

		// Search field.
		JTextField searchField = new JTextField("Hier Suchbegriff eingeben.");
		searchField.setToolTipText("Ortssuche");
		searchField.setPreferredSize(new Dimension(180, 30));
		searchField.addKeyListener(new KeyboardLocationAdapter(searchField));
		searchField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				((JTextField) e.getSource()).setText("");
			}
		});
		ImageIcon searchIcon = createIcon(
				"/sep/gaia/renderer/icons/32/search.png", "Suchen nach Orten");
		JButton searchButton = new JButton(searchIcon);// "res/icon16/search.png"));
		searchButton.setToolTipText("Ortssuche starten");
		searchButton.addActionListener(new SearchToolbarListener(searchField));

		// Add components.
		leftTools.add(markerPanelVisibilityButton);
		leftTools.add(poiBarVisibilityButton);
		leftTools.add(weatherButton);
		leftTools.add(screenshotButton);
		leftTools.add(markerButton);
		// leftTools.add(tileList);
		leftTools.add(zoomOutButton);
		leftTools.add(zoomInButton);
		leftTools.add(rotateClockwiseButton);
		leftTools.add(rotateCounterClockwiseButton);
		leftTools.add(rotateUpXButton);
		leftTools.add(rotateDownXButton);
		leftTools.add(styleBox);

		
		rightTools.add(searchField);
		rightTools.add(searchButton);

		add(leftTools, BorderLayout.WEST);
		add(rightTools, BorderLayout.EAST);

		setVisible(true);
	}

	/**
	 * Creates a new ImageIcon for the buttons
	 * 
	 * @param pathToImage
	 *            The path to the Icons (which are included in it)
	 * @param shortDescription
	 *            The short description of the usage of the corresponding button
	 * @return The new ImageIcon
	 */
	protected ImageIcon createIcon(String pathToImage, String shortDescription) {
		java.net.URL imageURL = getClass().getResource(pathToImage);
		if (imageURL != null) {
			return new ImageIcon(imageURL, shortDescription);
		} else {
			System.err.println("Couldn't find the image.");
			return null;
		}
	}
}