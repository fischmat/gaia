package sep.gaia.renderer.layer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.DuplicateFreeQueue;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Adapter-class providing functionality for scheduled creation and caching of textures.
 * 
 * <a id="caching"><h4>Caching</h4></a>
 * A <code>TextureAdapter</code> holds all textures currently required for
 * drawing in its <i>primary texture-cache</i>. Entries of it will not be destroyed
 * as long as they are part of it.
 * If textures are not required any longer, they are shifted from primary to
 * <i>secondary texture-cache</i>, where textures are held for future use 
 * and thus must not be created again.
 * If the maximum count of textures (<code>MAXSIZE_SECONDARY_TEXCACHE</code>) 
 * in the secondary texture-cache is reached, a certain amount of the textures
 * first inserted are destroyed and removed from the cache (using LRU-removal).<br>
 * <br>
 * <a id="glcontext"><h4>Scheduled texture-creation in the OpenGL-context</h4></a>
 * Because of textures must be created in the OpenGL-thread context, they
 * must be scheduled (<code>scheduleTextureCreation()</code>) and 
 * <code>performGLCalls()</code> must be called regulary from the OpenGL-thread.
 * <br>
 * <br>
 * @param <R> The type of resources that should be processed by the adapter.
 * 
 * @author Matthias Fisch (specification), Matthias Fisch (implementation), 
 * Johannes Bauer (implementation)
 *
 */
abstract class TextureAdapter<R extends DataResource> extends ResourceAdapter<R> {
	
	/**
	 * Bean for associating an image or texture with a name.
	 * 
	 * @param <T> The type of the image to be stored.
	 * 
	 * @author Matthias Fisch
	 *
	 */
	private class NamedImage<T> {
		
		/**
		 * The name of the image stored.
		 */
		private String name;
		
		/**
		 * The image stored.
		 */
		private T image;

		/**
		 * Initializes the pair.
		 * @param name The name of the image stored.
		 * @param image The image stored.
		 */
		public NamedImage(String name, T image) {
			super();
			this.name = name;
			this.image = image;
		}

		/**
		 * Returns the name of the image stored.
		 * @return The name of the image stored.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name of the image stored.
		 * @param name The name of the image stored.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Returns the image stored.
		 * @return The image stored.
		 */
		public T getImage() {
			return image;
		}

		/**
		 * Sets the image stored.
		 * @param image The image stored.
		 */
		public void setImage(T texture) {
			this.image = texture;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof NamedImage) {
				return ((NamedImage)obj).getName().equals(name);
			} else {
				return false;
			}
		}
		
		
	}

	/**
	 * Highest possible number of textures to be enabled at the same time.
	 */
	private static final int MAXSIZE_SECONDARY_TEXCACHE = 128;
	
	/**
	 * Textures to be destroyed when MAX_ENABLED_TEXTURES is reached.
	 */
	private static final int DESTROY_COUNT = 64;
	
	/**
	 * Queue holding the images that the OpenGL-Thread must bind.
	 */
	private DuplicateFreeQueue<NamedImage<TextureData>> toBeBound = new DuplicateFreeQueue<>();
	
	/**
	 * All those textures and their names, which are currently required.
	 */
	private List<NamedImage<Texture>> primaryTextures = new ArrayList<>();
	
	/**
	 * Lock for blocking write-access to <code>primaryTextures</code>.
	 */
	private Lock primaryTexturesLock = new ReentrantLock();
	
	/**
	 * Containes all textures that were shifted from primary cache.
	 * If a certain size is reached, some of them may be deleted using LRU-removal.
	 */
	private List<NamedImage<Texture>> secondaryTextures = new LinkedList<>();
	
	/**
	 * Lock for blocking write-access to <code>secondaryTextures</code>.
	 */
	private Lock secondaryTexturesLock = new ReentrantLock();
	
	/**
	 * Method called when new resources are available.
	 * It must be overridden and should make all elements in 
	 * <code>resources</code> fitting the respective layer.
	 * Note that this method is called outside the OpenGL-thread and thus 
	 * must not contain any OpenGL-specific calls.
	 * To schedule texture-creation call <code>scheduleTextureCreation()</code>
	 * and it will be created later in the GL-context.
	 * <br>
	 * <b>Important: </b> For the caching to work correctly, all methods overriding
	 * must call this implementation via <code>super.onUpdate(resources)</code>
	 * 
	 * @param resources The resources recently updated.
	 */
	@Override
	public void onUpdate(Collection<R> resources) {
		if(resources != null) {
			// Get collection of the keys of all resources:
			Collection<String> keys = new LinkedList<>();
			for(R resource : resources) {
				keys.add(resource.getKey());
			}
			// Update primary cache:
			shiftNonRequired(keys);
		}
	}

	/**
	 * Creates all textures scheduled.
	 * Also it will check if the amount of textures in the secondary texture-cache
	 * has exceeded its limit (MAXSIZE_SECONDARY_TEXCACHE) and - if required - destroys 
	 * a certain amount of them using a Least-Recently-Used (LRU) removal-strategy.
	 * This method must be called by the OpenGL-thread to prevent
	 * unexpected behavior.
	 */
	protected void performGLCalls(GL2 gl) {
		 while(!toBeBound.isEmpty()) {
			 NamedImage<TextureData> namedTexData = toBeBound.pop();
			 
			 // Create the texture from the tiles image without auto-generating mipmaps:
			 Texture texture;
			 try {
				 texture = TextureIO.newTexture(namedTexData.getImage());
				 
				 //System.out.println("Created a texture " + namedTexData.getName());
				 
			 } catch(GLException e) {
				 Logger.getInstance().error("Tried to create texture from non-GL context.");
				 return;
			 }
			 
			 NamedImage<Texture> namedTexture = new NamedImage<Texture>(namedTexData.getName(), 
					 													texture);
			 
			 // Add the entry to the primary texture-cache:
			 primaryTexturesLock.lock();
			 primaryTextures.add(namedTexture);
			 primaryTexturesLock.unlock();
			 
			 // Before removing entries from secondary cache, lock it:
			 secondaryTexturesLock.lock();
			 
			 // Check if the secondary texture-cache has exceeded its limit:
			 if(secondaryTextures.size() >= MAXSIZE_SECONDARY_TEXCACHE) {
				 
				 // Destroy the first inserted entries:
				 for(int i = 0; i < DESTROY_COUNT; i++) {
					 Texture removedTexture = secondaryTextures.get(0).getImage();
					 removedTexture.destroy(gl);
					 secondaryTextures.remove(0);
				 }
				 System.gc();
			 }
			 
			 // Removing entries done. So free the lock:
			 secondaryTexturesLock.unlock();
		 }
	}
	
	/**
	 * Shifts all textures contained in the primary texture-cache into the
	 * secondary texture-cache (cf. <a href="#caching">Caching</a>).
	 * @param required The keys of all those textures still required 
	 * and will therefore stay in the primary texture-cache if already contained.
	 */
	protected void shiftNonRequired(Collection<String> required) {
		// All entities removed from primary cache must be remembered for later removal:
		Collection<NamedImage<Texture>> shifted = new LinkedList<>();
		
		// When shifting from primary to secondary cache, both are write-accessed. 
		// So lock them:
		primaryTexturesLock.lock();
		secondaryTexturesLock.lock();
		// Iterate the primary texture-cache:
		for (NamedImage<Texture> currentPrimary : primaryTextures) {
			// If the entry is no longer required for drawing:
			if(!required.contains(currentPrimary.getName())) {
				// Move it to secondary cache:
				secondaryTextures.add(currentPrimary);
				shifted.add(currentPrimary); // Remember for removal
			}
		}
		
		// Now remove all remembered textures from primary texture-cache:
		primaryTextures.removeAll(shifted);
		
		// Free the locks, so other threads can access the caches:
		secondaryTexturesLock.unlock();
		primaryTexturesLock.unlock();
	}
	
	/**
	 * Schedules the creation of a texture to be identified by <code>key</code>.
	 * The texture will be created later by the OpenGL-thread.
	 * This method first checks if a texture with the same key already exists in
	 * secondary texture-cache. If it does, it is shifted into primary cache.
	 * If a texture identifiable with <code>key</code> is already in primary
	 * texture-cache, this method takes no effect. If none of the latter cases
	 * is current, the texture will be created by the OpenGL-thread and put into primary
	 * texture cache.
	 * 
	 * @param key The key the texture should be identified with.
	 * @param texData The data of the texture to create.
	 */
	protected void scheduleTextureCreation(String key, TextureData texData) {
		
		boolean cacheHit = toBeBound.contains(new NamedImage<TextureData>(key, texData));
		
		// First check if the texture is already in the secondary-cache:
		secondaryTexturesLock.lock();
		ListIterator<NamedImage<Texture>> secondaryTexIter = secondaryTextures.listIterator();
		while(secondaryTexIter.hasNext() && !cacheHit) {
			NamedImage<Texture> current = secondaryTexIter.next();
			
			if (current.getName().equals(key)) { // If found in secondary-cache:
				// Lock the caches, because they will be write-accessed:
				primaryTexturesLock.lock();
				
				// Shift texture into primary cache:
				secondaryTextures.remove(current);
				primaryTextures.add(current);
				
				// Unlock the caches again, to allow other threads access on them:
				primaryTexturesLock.unlock();
				cacheHit = true;
			}
		}
		secondaryTexturesLock.unlock();
		
		if(!cacheHit) {
			
			primaryTexturesLock.lock();
			// Check if required texture is already in primary cache:
			boolean exists = false;
			ListIterator<NamedImage<Texture>> primaryTexIter = primaryTextures.listIterator();
			while(primaryTexIter.hasNext() && !exists) {
				NamedImage<Texture> current = primaryTexIter.next();
				if(current.getName().equals(key)) {
					exists = true;
				}
			}
			primaryTexturesLock.unlock();
			
			if(!exists) {
				// Schedule the texture for creation in OpenGL-context:
				toBeBound.push(new NamedImage<TextureData>(key, texData));
			}
		}
	}
	
	/**
	 * Schedules the creation of a texture from the specified file 
	 * to be identified by <code>key</code>.
	 * The texture will be created later by the OpenGL-thread.
	 * This method first checks if a texture with the same key already exists in
	 * secondary texture-cache. If it does, it is shifted into primary cache.
	 * If a texture identifiable with <code>key</code> is already in primary
	 * texture-cache, this method takes no effect. If none of the latter cases
	 * is current, the texture will be created by the OpenGL-thread and put into primary
	 * texture cache.
	 * 
	 * @param profile The GL-profile to use for texture creation.
	 * @param key The key the texture should be identified with.
	 * @param texFilePath The file the texture will be created from.
	 * @throws IOException Thrown if <code>texFile</code> does not exist or an error
	 * occurs on reading the file.
	 */
	protected void scheduleTextureCreation(GLProfile profile, String key, String texFilePath) throws IOException {
		
		boolean inCache = toBeBound.contains(new NamedImage<TextureData>(key, null));
		
		primaryTexturesLock.lock();
		Iterator<NamedImage<Texture>> primaryCacheIter = primaryTextures.iterator();
		while(primaryCacheIter.hasNext() && !inCache) {
			NamedImage<Texture> entry = primaryCacheIter.next();
			inCache = entry.getName().equals(key);
		}
		primaryTexturesLock.unlock();
		
		secondaryTexturesLock.lock();
		Iterator<NamedImage<Texture>> secondaryCacheIter = secondaryTextures.iterator();
		while(secondaryCacheIter.hasNext() && !inCache) {
			NamedImage<Texture> entry = secondaryCacheIter.next();
			inCache = entry.getName().equals(key);
		}
		secondaryTexturesLock.unlock();
		
		// If the texture was not found in cache:
		if(!inCache) {
			// Determine the position of the files suffix:
			int suffixPosition = texFilePath.lastIndexOf(".") + 1;
			
			// If the suffix was found:
			if(suffixPosition > 0) {
				
				// Get the file-suffix:
				String suffix = texFilePath.substring(suffixPosition);
				
				// Generate the texture-data from the file:
				File textureFile = new File(texFilePath);
				
				TextureData textureData;
				try {
					textureData = TextureIO.newTextureData(profile, textureFile, false, suffix);
				} catch(RuntimeException e) {
					Logger.getInstance().error("Invalid format of image: " + texFilePath + " Details: " + e.getMessage());
					return;
				}
				
				// Schedule the creation of the texture data:
				scheduleTextureCreation(key, textureData);
			} else {
				
				throw new IOException("Invalid suffix of file " + texFilePath);
			}
		}
		
	}
	
	/**
	 * Checks first if a texture with the given key is contained in primary texture-cache.
	 * If it is not the secondary texture-cache is checked.
	 * For a description of the caches see <a href="#caching">Caching</a>.
	 * @param key The name of the texture.
	 * @return The texture identified by key or <code>null</code> if it is not found in both caches.
	 */
	public Texture getTexture(String key) {
		// Search in primary texture-cache:
		primaryTexturesLock.lock();
		for(NamedImage<Texture> current : primaryTextures) {
			if(current.getName().equals(key)) {
				primaryTexturesLock.unlock();
				return current.getImage();
			}
		}
		primaryTexturesLock.unlock();
		
		secondaryTexturesLock.lock();
		// Search in secondary texture-cache:
		for(NamedImage<Texture> current : secondaryTextures) {
			if(current.getName().equals(key)) {
				secondaryTexturesLock.unlock();
				return current.getImage();
			}
		}
		secondaryTexturesLock.unlock();
		return null;
	}
}