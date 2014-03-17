package sep.gaia.resources.geoimage;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sep.gaia.resources.DataResourceManager;
import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.state.State;

public class GeoImageManager extends DataResourceManager<GeoImageData> implements LoaderEventListener<GeoImageData> {
	
	/**
	 * The label used to identify a <code>GeoImageManager</code>.
	 */
	private static final String MANAGER_LABEL = "GeoImageManager";
	
	/**
	 * List of all directories containing image-files to be managed.
	 */
	private List<String> imageLibraryPaths = new LinkedList<>();
	
	/**
	 * All files of supported type (see <code>SUPPORTED_FILE_TYPES</code>) contained in a library. 
	 */
	private List<GeoImageData> imageResources = new LinkedList<>();
	
	/**
	 * Loader used for parsing directories asynchronously.
	 */
	private Loader<GeoImageParseQuery, GeoImageData> loader;
	
	public GeoImageManager() {
		super(MANAGER_LABEL);
		
		this.loader = new Loader<>(null, new GeoImageParseWorkerFactory(), null);
		this.loader.addListener(this);
		this.loader.start();
	}
	
	/**
	 * Adds all image-files contained in a directory <code>libraryRootPath</code>.
	 * @param libraryRootPath The path to add.
	 * @return <code>true</code> if and only if the path was added successfully.
	 */
	public boolean addImageLibrary(String libraryRootPath) {
		return addImageLibrary(libraryRootPath, true);
	}
	
	/**
	 * Adds all image-files contained in a directory <code>libraryRootPath</code>.
	 * @param libraryRootPath The directory to add.
	 * @return <code>true</code> if and only if the path was added successfully.
	 */
	private boolean addImageLibrary(String libraryRoot, boolean addToList) {
		// First check if library was already added:
		boolean unique = true;
		Iterator<String> libIter = imageLibraryPaths.iterator();
		while(libIter.hasNext() && unique) {
			String currentPath = libIter.next();
			unique = !libraryRoot.equals(currentPath);
		}
		
		// If the directory is not already in list.
		if(unique && loader != null) {
			
			loader.request(new GeoImageParseQuery(libraryRoot));
			
			// Add path to list (e.g. if not in recursive call):
			if(addToList) {
				imageLibraryPaths.add(libraryRoot);
			}
			
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public void onUpdate(State state) {	
		notifyAll(imageResources);
	}

	@Override
	public void requestLoaderStop() {
		if(loader != null) { // If loader exists:
			loader.interrupt(); // set the interrupt-flag to request termination.
		}
	}

	@Override
	public void onResourcesAvailable(Collection<GeoImageData> resources) {
		imageResources.addAll(resources);
		
		notifyAll(imageResources);
	}

	/**
	 * @return List of all paths being a scan-root.
	 */
	protected List<String> getImageLibraryPaths() {
		return imageLibraryPaths;
	}

	/**
	 * @return The geo-tagged images with their location found in the directories returned by
	 * <code>getImageLibraryPaths()</code>.
	 */
	protected List<GeoImageData> getImageResources() {
		return imageResources;
	}
}
