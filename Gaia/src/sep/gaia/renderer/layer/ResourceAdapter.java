package sep.gaia.renderer.layer;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;

import sep.gaia.resources.DataResource;
import sep.gaia.resources.ResourceObserver;

/**
 * Class which instances receive updates about newly available resource
 * from a <code>DataResourceManager</code>. Those resources can be processed and
 * made fitting the requirements of the ddrawing layer.
 * @author Matthias Fisch
 *
 * @param <R> The type of resources that should be processed by the adapter.
 */
public abstract class ResourceAdapter<R extends DataResource> implements ResourceObserver<R> {

	/**
	 * Collection of all converted resources that are current.
	 */
	private Collection<GLResource> convertedResources;
	
	/**
	 * Used for locking the converted resources to prevent reading of incomplete data.
	 */
	private Lock convertedResourcesLock = new ReentrantLock();
	
	/**
	 * Method called when new resources are available.
	 * This method must be implemented and should make all elements in 
	 * <code>resources</code> fitting the respective layer.
	 * Note that this method is called outside the OpenGL-thread and thus 
	 * must not contain any OpenGL-specific calls.
	 * @param resources The resources recently updated.
	 */
	@Override
	public abstract void onUpdate(Collection<R> resources);
	
	/**
	 * May be overwritten in order to do some OpenGL-specific work.
	 * In this implementation the call returns immediately.
	 * This method must be called regularly by the OpenGL-thread.
	 * If OpenGL-calls should be only done once use <code>performGLInit()</code>.
	 */
	protected void performGLCalls(GL2 gl) { }
	
	/**
	 * May be overwritten in order to do some OpenGL-specific work.
	 * In this implementation the call returns immediately.
	 * This method must be called before the first use of the adapter 
	 * by the OpenGL-thread.
	 * If OpenGL-calls should be only done regularly use <code>performGLCalls()</code>.
	 */
	protected void performGLInit(GL2 gl) { }
	
	/**
	 * Returns all converted resources that are current.
	 * Note that this call may block if converting is currently in progress.
	 * @return All converted resources that are current.
	 */
	public Collection<GLResource> getGLResources() {
		Collection<GLResource> copy;
		
		convertedResourcesLock.lock();
		if (convertedResources != null) {
			copy = new HashSet<>(convertedResources);			
		} else {
			copy = new HashSet<>();
		}
		convertedResourcesLock.unlock();
		
		return copy;
	}

	/**
	 * Sets the converted resources.
	 * @param drawableResources The converted resources.
	 */
	protected void setGLResources(Collection<GLResource> drawableResources) {
		// Write-accessing convertedResources. Lock them:
		convertedResourcesLock.lock();
		
		this.convertedResources = drawableResources;
		
		// Write accessing done:
		convertedResourcesLock.unlock();
	}
	
	/**
	 * Removes all GL-Resources currently stored in the adapter.
	 */
	@Override
	public void onClear() {
		if(convertedResources != null) {
			convertedResources.clear();
		}
	}
}
