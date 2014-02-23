package sep.gaia.resources.poi.test;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.poi.POICategory;
import sep.gaia.resources.poi.POIFilter;
import sep.gaia.resources.poi.SubCategory;

public class POICategoryTest {

	private static POICategory cat;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cat = new POICategory("Cat", new LinkedList<SubCategory>(), false, new POIFilter());
	}

	@Test
	public void test() {
		cat.activate(true);
		Collection<SubCategory> subCategories = new LinkedList<>();
		HashMap<String, String> map = new HashMap<>();
		map.put("key", "val");
		subCategories.add(new SubCategory("SubCat", new POIFilter(map), true));
		
		cat.setSubcategories(subCategories);
		cat.activate(false);
		
		assertFalse(cat.isActivated());
		for (SubCategory sCat : cat.getSubcategories()) {
			assertFalse(sCat.isActivated());
		}
	}

}
