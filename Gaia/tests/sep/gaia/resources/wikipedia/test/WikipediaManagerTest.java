package sep.gaia.resources.wikipedia.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.wikipedia.WikipediaManager;
import sep.gaia.resources.wikipedia.WikipediaData;
import sep.gaia.state.GeoState;

public class WikipediaManagerTest {

	private static WikipediaManager manager;
	private static WikipediaData wallersdorf;

	@BeforeClass
	public static void init() {
		manager = new WikipediaManager();
	}

	@AfterClass
	public static void destroy() {
		manager = null;
	}

	@Before
	public void loadOneWikipediaData() {
		// WikipediaData für Wallersdorf
		wallersdorf = new WikipediaData("Wallersdorf", "Wallersdorf", 0, 0);
		manager.load(wallersdorf);

		// Wartezeit miteinberechnen?
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRequestLoaderStop() {
		manager = new WikipediaManager();
		manager.requestLoaderStop();
		manager.load(wallersdorf);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(manager.getCurrentWikipediaDatas().isEmpty());
	}

	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentWikipediaDatas() {
		for (WikipediaData currentWiki : manager.getCurrentWikipediaDatas()) {
			assertEquals(wallersdorf, currentWiki);
		}
	}

	@Test
	public void testDisable() {
		manager.disable();
		// Now, the WikipediaManager should be disabled and therefore, it holds
		// not WikipediaData objects.
		for (WikipediaData currentWiki : manager.getCurrentWikipediaDatas()) {
			assertEquals(null, currentWiki);
		}
	}

	@Test
	/**
	 * Update GeoState -> Notify WikipediaManager 
	 * -> WikipediaManager loads current WikipediaData
	 */
	public void testGetUsedResources() {
		// Ort: Poxau
		GeoState geoState = new GeoState(15, 12.56123f, 48.56123f, null);
		manager.onUpdate(geoState);
		WikipediaData poxau = new WikipediaData("Poxau", null, 0, 0);
		
		boolean sameUsedResources = false;
		
		// Short description equal?
		for (WikipediaData currentWiki : manager.getCurrentWikipediaDatas()) {
			if (currentWiki.getSummaryText().equals(poxau.getSummaryText())) {
				sameUsedResources = true;
			}
		}
		
		assert(sameUsedResources);
	}
	
	@Test
	public void testGetWikipediaByTitle() {
		assertTrue(null == manager.getWikipediaByTitle("Passau"));
		assertTrue(null != manager.getWikipediaByTitle("Wallersdorf"));
	}

}
