package sep.gaia.resources.tiles2d.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.media.opengl.GLProfile;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.ResourceObserver;
import sep.gaia.resources.tiles2d.TileManager;
import sep.gaia.resources.tiles2d.TileResource;
import sep.gaia.state.TileState;
import sep.gaia.util.IntegerBoundingBox;
import sep.gaia.util.IntegerVector3D;

/**
 * 
 * @author Johannes Bauer
 */
public class TileManagerTest {

    private static TileManager manager;
    private static TileState state;
    private static Observer observer;
    private static Collection<TileResource> toCompare;

    @BeforeClass
    public static void init() {
	// Prepare state. 9 tiles are visible.
	IntegerBoundingBox bbox = new IntegerBoundingBox(new IntegerVector3D(5,
		5, 10), new IntegerVector3D(7, 7, 10));
	state = new TileState(10, bbox, 6, 6);

	// Prepare the 9 TileResource objects for comparing.
	/* TODO Load manually 9 TileResource objects with their content. */
	toCompare = new HashSet<>();

	// Manager
	manager = new TileManager(GLProfile.getDefault());

	// Resource observer for checking the loaded resources.
	// Resource observer is empty at the beginning,
	// e.g. holds no TileResources.
	observer = new Observer();
	manager.register(observer);
    }
    
    @After
    public void resetObserverAndLoader() {
    	manager.enable();
    	observer = new Observer();
    }

    @Test
    public void testRequestLoaderStop() {
	// Stop loader.
	manager.requestLoaderStop();
	// Update Manager with new State.
	manager.onUpdate(state);

	// Wait for 10s. Loader should not load...
	try {
	    Thread.sleep(20000l);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// ResourceObserver should not get updates.
	Collection<TileResource> received = observer.resources;

	// So its list of resources should be empty.
	assertEquals(received.size(), 0);
    }

    @Test
    public void testGetAvailableStyles() {
	fail("Not yet implemented");
	/* TODO Styles generieren */
    }

    @Test
    public void testOnResourcesAvailable() {
	manager.onUpdate(state);

	/* TODO Ergebnisse mit vorhandenen TileResourcen vergleichen */
	// The loading procedure may take some time... 20s?
	try {
	    Thread.sleep(20000l);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	Collection<TileResource> received = observer.resources;

	// Test for same size.
	assertEquals(toCompare.size(), received.size());

	// Now check if all elements are equal.
	for (TileResource current : toCompare) {
	    TileResource equalReceivedRes = null;

	    // Get corresponding element from received collection.
	    Iterator<TileResource> iter = received.iterator();
	    while (iter.hasNext()) {
		TileResource currentReceived = iter.next();
		if (currentReceived.equals(current)) {
		    equalReceivedRes = currentReceived;
		}
	    }

	    // Check if TileResource objects are equal.
	    assertEquals(equalReceivedRes, current);
	    // Check if images are equal.
	    assertEquals(equalReceivedRes.getTextureData(), current.getTextureData());
	}

    }
    
    @Test
    public void testEnableSetOnline() {
	
	manager.enable();
	manager.setOnline(true);
	manager.disable();
	manager.setOnline(false);
	manager.enable();
	manager.setOnline(true);
	
	manager.onUpdate(state);

	/* TODO Ergebnisse mit vorhandenen TileResourcen vergleichen */
	// The loading procedure may take some time... 20s?
	try {
	    Thread.sleep(20000l);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	Collection<TileResource> received = observer.resources;

	// Test for same size.
	assertEquals(toCompare.size(), received.size());

	// Now check if all elements are equal.
	for (TileResource current : toCompare) {
	    TileResource equalReceivedRes = null;

	    // Get corresponding element from received collection.
	    Iterator<TileResource> iter = received.iterator();
	    while (iter.hasNext()) {
		TileResource currentReceived = iter.next();
		if (currentReceived.equals(current)) {
		    equalReceivedRes = currentReceived;
		}
	    }

	    // Check if TileResource objects are equal.
	    assertEquals(equalReceivedRes, current);
	    // Check if images are equal.
	    assertEquals(equalReceivedRes.getTextureData(), current.getTextureData());
	}

    }

    @Test
    public void testLoadStylesFromXML() {
	manager.loadStylesFromXML();
	assertEquals(false, manager.getAvailableStyles().isEmpty());
	/* TODO */
    }

    /**
     * Implementation of <code>ResourceObserver</code> for receiving loaded
     * <code>TileResource</code> objects.
     * 
     */
    private static class Observer implements ResourceObserver<TileResource> {

	Collection<TileResource> resources;
	
	public Observer() {
		resources = new HashSet<TileResource>();
	}

	@Override
	public void onUpdate(Collection<TileResource> resources) {
	    this.resources = resources;

	}

	@Override
	public void onClear() { } // do nothing
    }
}
