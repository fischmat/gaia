package sep.gaia.main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sep.gaia.controller.KeyboardMoveAdapter;
import sep.gaia.controller.KeyboardRotationAdapter;
import sep.gaia.controller.MouseClickedListener;
import sep.gaia.controller.MouseDraggedListener2d;
import sep.gaia.controller.MouseDraggedListener3d;
import sep.gaia.controller.MouseHoverListener;
import sep.gaia.controller.MouseZoomListener;
import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.renderer.GaiaRenderer;
import sep.gaia.renderer.Mode3D;
import sep.gaia.renderer.layer.CompassAdapter;
import sep.gaia.renderer.layer.CompassLayer;
import sep.gaia.renderer.layer.CopyrightAdapter;
import sep.gaia.renderer.layer.CopyrightLayer;
import sep.gaia.renderer.layer.MarkerAdapter;
import sep.gaia.renderer.layer.MarkerLayer;
import sep.gaia.renderer.layer.POIAdapter;
import sep.gaia.renderer.layer.POILayer;
import sep.gaia.renderer.layer.ScreenshotLayer;
import sep.gaia.renderer.layer.TileAdapter;
import sep.gaia.renderer.layer.TileLayer;
import sep.gaia.renderer.layer.WeatherAdapter;
import sep.gaia.renderer.layer.WeatherLayer;
import sep.gaia.renderer.layer.WikipediaAdapter;
import sep.gaia.renderer.layer.WikipediaLayer;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.markeroption.MarkerResourceManager;
import sep.gaia.resources.markeroption.MarkerStorage;
import sep.gaia.resources.poi.POIManager;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.resources.weather.WeatherManager;
import sep.gaia.resources.wikipedia.WikipediaData;
import sep.gaia.resources.wikipedia.WikipediaManager;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.GeoState;
import sep.gaia.state.StateManager;
import sep.gaia.state.TileState;
import sep.gaia.ui.GaiaCanvas;
import sep.gaia.ui.MainWindow;
import sep.gaia.ui.MarkerPanel;
import sep.gaia.ui.PoiBar;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.FPSAnimator;

/** 
 * The GaiaAssembler class contains the main method and is thus the entry-point
 * for the program-flow.
 * 
 * @author Matthias Fisch, Johannes Bauer, Michael Mitterer
 */
public class Gaia {
	private static final long serialVersionUID = 1L;

	// GUI components.
	private GLCanvas canvas;
	private MarkerPanel markerPanel;
	private PoiBar poiBar;

	// Renderer.
	private GaiaRenderer renderer;
	final FPSAnimator animator = new FPSAnimator(60);

	// Tile.
	private TileManager tileManager;
	private TileAdapter tileAdapter;
	private TileLayer tileLayer;
	
	private WikipediaManager wikipediaManager;
	
	private POIManager poiManager;

	public Gaia() {
		setNativeLookAndFeel();
		addShutdownHook();
		
		/* FOR DEVELOPEMENT */
		Logger.getInstance().setMinimumLevel(0);
		
		// Standard profile.
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities glcap = new GLCapabilities(profile);

		// Set color depth.
		glcap.setRedBits(8);
		glcap.setGreenBits(8);
		glcap.setBlueBits(8);
		glcap.setAlphaBits(8);

		canvas = GaiaCanvas.getInstance(glcap);
		canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		canvas.setSize(new Dimension(512, 512));
		canvas.setFocusable(true);

		// Animator runs the display Method at specified FPS.

		animator.add(canvas);

		GLState glState = new GLState(Mode3D.MIN_3D_LEVEL);
		StateManager manager = (StateManager) StateManager.getInstance(glState);
		GeoState geoState = (GeoState) manager.getState(StateType.GeoState);
		TileState tileState = (TileState) manager.getState(StateType.TileState);

		// Resource: tile
		tileAdapter = new TileAdapter();
		tileLayer = new TileLayer(tileAdapter);
		tileManager = new TileManager(profile);
		tileManager.register(tileAdapter);
		tileManager.disable();
		
		ResourceMaster.getInstance().addResourceManager(tileManager);
		
		MarkerResourceManager markerManager = null;
		MarkerStorage markerStorage = new MarkerStorage(markerManager);
		markerManager = new MarkerResourceManager(markerStorage);
		ResourceMaster.getInstance().addResourceManager(markerManager);
		
		MarkerAdapter markerAdapter = new MarkerAdapter();
		MarkerLayer markerLayer = new MarkerLayer(markerAdapter);
		
		glState.register(markerManager);
		markerManager.register(markerAdapter);
		
		// POI resource
		poiManager = new POIManager();
		ResourceMaster.getInstance().addResourceManager(poiManager);
		
		POIAdapter poiAdapter = new POIAdapter();
		POILayer poiLayer = new POILayer(poiAdapter);
		
		poiManager.register(poiAdapter);
		glState.register(poiManager);
		
		// Wikipedia resource
		wikipediaManager = new WikipediaManager();
		ResourceMaster.getInstance().addResourceManager(wikipediaManager);
		
		WikipediaAdapter wikiAdapter = new WikipediaAdapter();
		WikipediaLayer wikiLayer = new WikipediaLayer(wikiAdapter);

		ScreenshotLayer screenshotLayer = new ScreenshotLayer();

		//poiManager.register(wikiAdapter);
		wikipediaManager.register(wikiAdapter);
		glState.register(wikipediaManager);
		
		//wikipediaManager.load(new WikipediaData("passau"));
		
		markerPanel = new MarkerPanel();
		poiBar = new PoiBar();

		// Tile resource
		glState.register(tileManager);
		renderer = GaiaRenderer.getInstance(tileLayer);
		
		// Weather resource
		WeatherManager weatherManager = new WeatherManager();
		glState.register(weatherManager);
		WeatherAdapter weatherAdapter = new WeatherAdapter();
		weatherManager.register(weatherAdapter);
		
		ResourceMaster.getInstance().addResourceManager(weatherManager);
		// Weather layer
		WeatherLayer weatherLayer = new WeatherLayer(weatherAdapter);
		
		// Compass
		CompassAdapter compassAdapter = new CompassAdapter();
		glState.register(compassAdapter);
		CompassLayer compassLayer = new CompassLayer(compassAdapter);
		
		CopyrightAdapter copyrightAdapter = new CopyrightAdapter();
		glState.register(copyrightAdapter);
		CopyrightLayer copyrightLayer = new CopyrightLayer(copyrightAdapter);
		
		
		// Build renderer layer cascade. FOR DEBUGGIN /* TODO */
		tileLayer.add(markerLayer);
		markerLayer.add(poiLayer);
		poiLayer.add(wikiLayer);
		wikiLayer.add(weatherLayer);
		weatherLayer.add(screenshotLayer);
		screenshotLayer.add(compassLayer);
		compassLayer.add(copyrightLayer);
		
		// Register listener.
		canvas.addMouseWheelListener(new MouseZoomListener(glState));
		MouseDraggedListener2d canvas2DMouseDraggedListener = new MouseDraggedListener2d(glState);
		canvas.addMouseMotionListener(canvas2DMouseDraggedListener);
		canvas.addMouseListener(canvas2DMouseDraggedListener);
		
		canvas.addMouseMotionListener(new MouseDraggedListener3d(glState));
		
		KeyboardMoveAdapter kbMoveAdapter = new KeyboardMoveAdapter(glState);
		canvas.addKeyListener(kbMoveAdapter);
		canvas.addMouseListener(new MouseClickedListener(glState, canvas));
		canvas.addKeyListener(new KeyboardRotationAdapter(glState));
		
		//canvas.addKeyListener(new KeyboardListener());

		// renderer.currentMode = renderer.mode2D;
		canvas.addGLEventListener(renderer);
		
		MainWindow window = new MainWindow(canvas, markerPanel, poiBar, screenshotLayer);
		glState.register(weatherAdapter);
		
		canvas.addMouseMotionListener(new MouseHoverListener(glState, window.getInfoBar(), markerAdapter, poiAdapter, wikiAdapter));
		
		// Request focus for canvas.
		canvas.setVisible(true);
		canvas.requestFocusInWindow();
		
		animator.start();
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		        ResourceMaster master = ResourceMaster.getInstance();
		        master.broadcastExitEvent();
		        System.out.println("Good bye!");
		    }
		});
	}
	
	private void setNativeLookAndFeel() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {

		}
	}

	/**
	 * The main entry point of the application. Commandline-options are
	 * currently not supported.
	 * 
	 * @param args
	 *            Not supported.
	 */
	public static void main(String[] args) {
		Environment env = Environment.getInstance();
		
		for(String arg : args) {
			switch(arg) {
			case "--clear-cache": new File(env.getString(EnvVariable.TILE_CACHE_INDEX_FILE)).delete();
			}
		}
		
		Gaia instance = new Gaia();

	}
}