package sep.gaia.ui;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import sep.gaia.renderer.layer.ScreenshotLayer;

public class GAIAPanel extends JPanel {

	private GAIAToolBar toolBar;
	private GLCanvas canvas;
	private MarkerPanel markerPanel;
	private PoiBar poiBar;
	
	public GAIAPanel(GLCanvas canvas, MarkerPanel markerPanel, PoiBar poiBar, ScreenshotLayer screenshotLayer) {
		super();
		
		// Divide panel.
		setLayout(new BorderLayout());
		
		// Create components.
		this.canvas = canvas;
		this.markerPanel = markerPanel;
		this.poiBar = poiBar;
		createComponents(screenshotLayer);
		
		// Add components.
		add(toolBar, BorderLayout.NORTH);
		add(canvas, BorderLayout.CENTER);
		add(poiBar, BorderLayout.EAST);
		add(markerPanel, BorderLayout.WEST);
		
		validate();
		setPreferredSize(getPreferredSize());
		setVisible(true);
	}
	
	private void createComponents(ScreenshotLayer layer) {
		toolBar = new GAIAToolBar(markerPanel, poiBar, layer);
		markerPanel = new MarkerPanel();
	}
}
