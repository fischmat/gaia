package sep.gaia.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import sep.gaia.util.FloatVector3D;
import sep.gaia.util.RayCast;

public class RayCastTest {

	private static final float EPSILON = 0.1f;
	
	@Test
	public void testGeneralLinearCast() {
		FloatVector3D from1 = new FloatVector3D(0, 2, 4);
		float angle1 = -35.01f;
		FloatVector3D CMP_section1 = new FloatVector3D(0, -0.8f, 0);
		FloatVector3D TEST_section1 = new FloatVector3D(0, 0, 0);
		TEST_section1 = RayCast.linearCast(from1, new FloatVector3D(angle1, 0, 0));
		assertEquals(CMP_section1.getX(), TEST_section1.getX(), EPSILON);
		assertEquals(CMP_section1.getY(), TEST_section1.getY(), EPSILON);
		assertEquals(CMP_section1.getZ(), TEST_section1.getZ(), EPSILON);
	}

	@Test
	public void testViewLinearCast() {
		fail("Not yet implemented");
	}

}
