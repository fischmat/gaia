package sep.gaia.resources.geoimage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.util.FloatVector3D;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class GeoImageParseWorker extends AbstractLoaderWorker<GeoImageParseQuery, GeoImageData> {

	/**
	 * possible extensions of all supported file-types.
	 */
	private static final String[] SUPPORTED_FILE_TYPES = {"jpg", "jpeg", "tif", "tiff", "png", "bmp", "gif"};
	
	public GeoImageParseWorker(GeoImageParseQuery subQuery) {
		super(subQuery);
	}

	@Override
	public void run() {
		GeoImageParseQuery query = getSubQuery();
		
		if(query != null && query.getParseRoot() != null) {
			File root = new File(query.getParseRoot());
			
			setResults(parseDirectory(root));
			
		} else {
			setResults(new LinkedList<GeoImageData>());
		}
	}
	
	private Collection<GeoImageData> parseDirectory(File libraryRoot) {
		
		Collection<GeoImageData> resources = new LinkedList<>();
		
		// Scan all contained files recursively for a supported extension:
		for(File contained : libraryRoot.listFiles()) {
			if(contained.isDirectory()) {
				resources.addAll(parseDirectory(contained));
				
			} else {
				// The current entry is a file. Check if extension is supported:
				String path = contained.getAbsolutePath();
				int extensionBegIdx = path.lastIndexOf(".") + 1;
				if(extensionBegIdx > 0) {
					String extension = path.substring(extensionBegIdx);
					if(isExtensionSupported(extension)) {
						
						// Create the resource and add it to list:
						try {
							GeoImageData resource = getGeoImageData(contained);
							if(resource != null) {
								resources.add(resource);
							}
							
						} catch(IOException | ImageProcessingException e) {
							// If an error occured, simply skip the file.
						}
						
					}
				}
			}
		}
		
		return resources;
	}

	/**
	 * Reads the location the image <code>imageFile</code> was taken at from the files Exif-data.
	 * @param imageFile The image-file to retrieve information for.
	 * @return A resource containing the file and the location read or <code>null</code> if the Exif-header or the 
	 * GPS-directory was not present in it.
	 * @throws ImageProcessingException Thrown on error when reading Exif-header.
	 * @throws IOException Thrown if error on I/O.
	 */
	private GeoImageData getGeoImageData(File imageFile) throws ImageProcessingException, IOException {
		// Retrieve the GPS-section of the Exif-header:
		Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
		if(metadata != null) {
			GpsDirectory directory = metadata.getDirectory(GpsDirectory.class);
			
			if(directory != null) {
				// Convert the libraries location-representation:
				GeoLocation geoLocation = directory.getGeoLocation();
				FloatVector3D geoVector = new FloatVector3D((float)geoLocation.getLatitude(), (float)geoLocation.getLongitude(), 0);
				
				// Create the data-object:
				return new GeoImageData(imageFile, geoVector);
			}
		}
		return null;
	}
	
	/**
	 * Returns whether an file-extension is supported and thus the Exif-data can be read.
	 * @param extension The Extension to be checked.
	 * @return <code>true</code> if the extension is supported. <code>false</code> if not.
	 */
	private boolean isExtensionSupported(String extension) {
		for(String supportedExt : SUPPORTED_FILE_TYPES) {
			if(supportedExt.toLowerCase().equals(extension)) {
				return true;
			}
		}
		return false;
	}
}
