package sep.gaia.resources.poi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.poi.POICategory;
import sep.gaia.resources.poi.POIFilter;
import sep.gaia.resources.poi.POIManager;
import sep.gaia.resources.poi.SubCategory;

public class POIManagerTest {
	
	private static POIManager manager;
	
	@BeforeClass
	public static void init() {
		manager = new POIManager();
	}

	@Test
	public void testSetSubCategoryActive() {
			
		HashMap<String, String> map1 = new HashMap<>();
		HashMap<String, String> map2 = new HashMap<>();
		map1.put("key", "val");
		map2.put("anotherKey", "VAL");
		
		SubCategory subCategory = new SubCategory("SubCat", new POIFilter(map1), true);
		POICategory cat = new POICategory("Cat", new LinkedList<SubCategory>(), false, new POIFilter(map2));
		Collection<POICategory> cats = new LinkedList<>();
		cats.add(cat);
		
		Collection<SubCategory> subs = new LinkedList<>();
		subs.add(subCategory);
		cat.setSubcategories(subs);
		
		map1.put("anotherKey", "VAL");
		SubCategory subCategory2 = new SubCategory("SubCat", new POIFilter(map1), true);
		manager.setCategories(cats);
		manager.setSubCategoryActive(subCategory, false);
		
		assertTrue(subCategory.isActivated());
		assertFalse(subCategory2.isActivated());
	}
}
