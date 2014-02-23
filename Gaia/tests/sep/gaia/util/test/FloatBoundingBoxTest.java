
package sep.gaia.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

public class FloatBoundingBoxTest {

	@Test
	public void testContains() {
		FloatVector3D outerUpperLeft = new FloatVector3D(-5.18f, 1.74f, 0);
		FloatVector3D outerUpperRight = new FloatVector3D(-0.26f, 5.18f, 0);
		FloatVector3D outerLowerLeft = new FloatVector3D(-1.74f, -3.18f, 0);
		FloatVector3D outerLowerRight = new FloatVector3D(3.18f, 0.26f, 0);
		
		FloatVector3D innerUpperLeft = new FloatVector3D(-2, 3, 0);
		FloatVector3D innerUpperRight = new FloatVector3D(0.9f, 2.22f, 0);
		FloatVector3D innerLowerLeft = new FloatVector3D(-2.65f, 0.59f, 0);
		FloatVector3D innerLowerRight = new FloatVector3D(0.25f, -0.19f, 0);
		
		FloatBoundingBox outer = new FloatBoundingBox(outerUpperLeft, outerUpperRight, outerLowerLeft, outerLowerRight);
		FloatBoundingBox inner = new FloatBoundingBox(innerUpperLeft, innerUpperRight, innerLowerLeft, innerLowerRight);
		
		assertEquals(true, outer.contains(inner));
		assertEquals(false, inner.contains(outer));
		
		inner = new FloatBoundingBox(outer);
		assertEquals(true, outer.contains(inner));
	}

	
	@Test
	public void testContainsPoint() {
		FloatVector3D upperLeft = new FloatVector3D(-5.18f, 1.74f, 0);
		FloatVector3D upperRight = new FloatVector3D(-0.26f, 5.18f, 0);
		FloatVector3D lowerLeft = new FloatVector3D(-1.74f, -3.18f, 0);
		FloatVector3D lowerRight = new FloatVector3D(3.18f, 0.26f, 0);
		
		FloatVector3D inner = new FloatVector3D(-2.65f, 0.59f, 0);
		FloatVector3D outer = new FloatVector3D(-100, 100, 0);
		
		FloatBoundingBox box = new FloatBoundingBox(upperLeft, upperRight, lowerLeft, lowerRight);
		
		assertEquals(true, box.contains(inner));
		assertEquals(false, box.contains(outer));
	}
}
