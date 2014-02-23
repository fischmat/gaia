package sep.gaia.controller.settings;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.tiles2d.TileCache;
import sep.gaia.resources.tiles2d.TileManager;


/**
 * This class is to implement the <code>ActionListener</code> interface
 * to check if the user wants to change the maximal cache size.
 * 
 * @author Max Witzelsperger
 *
 */
public class SliderListener implements ChangeListener {

	private static final long MEGABYTE_TO_BYTE = 1024 * 1024;
	
	/**
	 * the current size of the cache, which is to be set to 0 when the maximum
	 * cache size changes
	 */
	private JLabel cacheSize;
	
	private static long maximumSizeOnDisk;
	
	/**
	 * Constructs a new <code>SliderListener</code>, which changes the size
	 * of the tile cache dependent on the status of the slider that
	 * <code>this</code> is attached to. When the cache size is changed,
	 * the cached data gets automatically deleted.
	 * 
	 * @param cacheSize shows the current cache size to the user, which
	 * is set to 0 when <code>stateChanged</code> is invoked
	 */
	public SliderListener(JLabel cacheSize) {
		assert cacheSize != null : "JLabel with cache size must not be null!";
		
		this.cacheSize = cacheSize;
	}
	
	@Override
	public void stateChanged(ChangeEvent c) {

		JSlider source = (JSlider)c.getSource();
		TileManager manager = (TileManager) ResourceMaster.getInstance().getResourceManager(TileManager.MANAGER_LABEL);
		
		TileCache cache = manager.getCache();
		cache.setMaximumSizeOnDisk(source.getValue() * MEGABYTE_TO_BYTE);
		this.cacheSize.setText("0");
		cache.clear();
		
		cache.manage();
		SliderListener.maximumSizeOnDisk = source.getValue();
	}
	
	public static long getMaximum() {
		return maximumSizeOnDisk;
	}

}
