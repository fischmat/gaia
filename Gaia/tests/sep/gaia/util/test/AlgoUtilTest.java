package sep.gaia.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;
import sep.gaia.util.IntegerVector3D;

/**
 * 
 * @author Johannes Bauer
 *
 */
public class AlgoUtilTest {

	public static final float EPSILON = 0.0001f;
	
	@Test
	public void testGlToGeoFloatB() {
		
		/* TEST 1: */
		//FloatVector3D glVector1 = new FloatVector3D(8192.0f, 8192.0f, 32.0f);
		//FloatVector3D CMP_geoVector1 = new FloatVector3D(45.0f, 45.0f, 32.0f);
		// Test
		//FloatVector3D TEST_geoVector1 = AlgoUtil.glToGeo(glVector1);
		//assertEquals(CMP_geoVector1.getX(), TEST_geoVector1.getX(), EPSILON);
		//assertEquals(CMP_geoVector1.getY(), TEST_geoVector1.getY(), EPSILON);
		
		/* TEST 2: */
		//FloatVector3D glVector2 = new FloatVector3D(0.0f, 0.0f, 32.0f);
		//FloatVector3D CMP_geoVector2 = new FloatVector3D(0.0f, 0.0f, 32.0f);
		// Test
		//FloatVector3D TEST_geoVector2 = AlgoUtil.glToGeo(glVector2);
		//assertEquals(CMP_geoVector2.getX(), TEST_geoVector2.getX(), EPSILON);
		//assertEquals(CMP_geoVector2.getY(), TEST_geoVector2.getY(), EPSILON);
		
		/* TEST 3: */
		//FloatVector3D glVector3 = new FloatVector3D(0.0f, -16384.0f, 32.0f);
		//FloatVector3D CMP_geoVector3 = new FloatVector3D(-90.0f, 0.0f, 32.0f);
		// Test
		//FloatVector3D TEST_geoVector3 = AlgoUtil.glToGeo(glVector3);
		//assertEquals(CMP_geoVector3.getX(), TEST_geoVector3.getX(), EPSILON);
		//assertEquals(CMP_geoVector3.getY(), TEST_geoVector3.getY(), EPSILON);
		
		/* TEST 4: */
		//FloatVector3D glVector4 = new FloatVector3D(4096.0f, -6144.0f, 32.0f);
		//FloatVector3D CMP_geoVector4 = new FloatVector3D(-33.75f, 22.5f, 32.0f);
		// Test
		//FloatVector3D TEST_geoVector4 = AlgoUtil.glToGeo(glVector4);
		//assertEquals(CMP_geoVector4.getX(), TEST_geoVector4.getX(), EPSILON);
		//assertEquals(CMP_geoVector4.getY(), TEST_geoVector4.getY(), EPSILON);
		
		/* TEST 5: */
		//FloatVector3D glVector5 = new FloatVector3D(1044.1843f, 5101.5f, 32.0f);
		//FloatVector3D CMP_geoVector5 = new FloatVector3D(48.7492f, 11.4546f, 32.0f);
		// Test
		//FloatVector3D TEST_geoVector5 = AlgoUtil.glToGeo(glVector5);
		//assertEquals(CMP_geoVector5.getX(), TEST_geoVector5.getX(), EPSILON);
		//assertEquals(CMP_geoVector5.getY(), TEST_geoVector5.getY(), EPSILON);	
		
	}

	@Test
	public void testGeoToGl() {
		/* REICHT EIGENTLICH DEN TEST testGlToGeoFloatVector3D UMZUDREHEN*/
		/* TODO */
		fail("Not yet implemented");
	}

	@Test
	public void testGlToTileFloatVector3D() {
		/* TEST 1: */
		FloatVector3D glVector1 = new FloatVector3D(0, 0, 1024.0f);
		IntegerVector3D CMP_tileVector1 = new IntegerVector3D(16, 16, 5);
		// Test
		IntegerVector3D TEST_tileVector1 = AlgoUtil.glToTile(glVector1);
		assertEquals(CMP_tileVector1.getX(), TEST_tileVector1.getX());
		assertEquals(CMP_tileVector1.getY(), TEST_tileVector1.getY());
		assertEquals(CMP_tileVector1.getZ(), TEST_tileVector1.getZ());
		
		/* TEST 2: */
		FloatVector3D glVector2 = new FloatVector3D(-1024.0f, 3072.0f, 1024.0f);
		IntegerVector3D CMP_tileVector2 = new IntegerVector3D(15, 13, 5);
		// Test
		IntegerVector3D TEST_tileVector2 = AlgoUtil.glToTile(glVector2);
		assertEquals(CMP_tileVector2.getX(), TEST_tileVector2.getX());
		assertEquals(CMP_tileVector2.getY(), TEST_tileVector2.getY());
		assertEquals(CMP_tileVector2.getZ(), TEST_tileVector2.getZ());
		
		/* TEST 3: */
		FloatVector3D glVector3 = new FloatVector3D(1024.0f, 1024.0f, 1024.0f);
		IntegerVector3D CMP_tileVector3 = new IntegerVector3D(17, 15, 5);
		// Test
		IntegerVector3D TEST_tileVector3 = AlgoUtil.glToTile(glVector3);
		assertEquals(CMP_tileVector3.getX(), TEST_tileVector3.getX());
		assertEquals(CMP_tileVector3.getY(), TEST_tileVector3.getY());
		assertEquals(CMP_tileVector3.getZ(), TEST_tileVector3.getZ());
		
		/* TEST 4: */
		FloatVector3D glVector4 = new FloatVector3D(1050.0f, 1017.0f, 1024.0f);
		IntegerVector3D CMP_tileVector4 = new IntegerVector3D(17, 15, 5);
		// Test
		IntegerVector3D TEST_tileVector4 = AlgoUtil.glToTile(glVector4);
		assertEquals(CMP_tileVector4.getX(), TEST_tileVector4.getX());
		assertEquals(CMP_tileVector4.getY(), TEST_tileVector4.getY());
		assertEquals(CMP_tileVector4.getZ(), TEST_tileVector4.getZ());
		
		/* TEST 5: */
		FloatVector3D glVector5 = new FloatVector3D(-1536.0f, -512.0f, 1024.0f);
		IntegerVector3D CMP_tileVector5 = new IntegerVector3D(14, 16, 5);
		// Test
		IntegerVector3D TEST_tileVector5 = AlgoUtil.glToTile(glVector5);
		assertEquals(CMP_tileVector5.getX(), TEST_tileVector5.getX());
		assertEquals(CMP_tileVector5.getY(), TEST_tileVector5.getY());
		assertEquals(CMP_tileVector5.getZ(), TEST_tileVector5.getZ());
		
		/* TEST 6: */
		FloatVector3D glVector6 = new FloatVector3D(-505.0f, -505.0f, 512.0f);
		IntegerVector3D CMP_tileVector6 = new IntegerVector3D(31, 32, 6);
		// Test
		IntegerVector3D TEST_tileVector6 = AlgoUtil.glToTile(glVector6);
		assertEquals(CMP_tileVector6.getX(), TEST_tileVector6.getX());
		assertEquals(CMP_tileVector6.getY(), TEST_tileVector6.getY());
		assertEquals(CMP_tileVector6.getZ(), TEST_tileVector6.getZ());
		
	}

	@Test
	public void testGeoToTileFloatVector3D() {
		FloatVector3D in1 = new FloatVector3D(12.7570f, 48.7300f, 13);
		IntegerVector3D out1 = new IntegerVector3D(0, 0, 0);
		IntegerVector3D exp1 = new IntegerVector3D(4386, 2822, 13);
		out1 = AlgoUtil.geoToTile(in1);
		assertEquals(exp1.getX(), out1.getX());
		assertEquals(exp1.getY(), out1.getY());
		
		FloatVector3D in2 = new FloatVector3D(12.9113f, 48.7341f, 15);
		IntegerVector3D out2 = new IntegerVector3D(0, 0, 0);
		IntegerVector3D exp2 = new IntegerVector3D(17559, 11290, 15);
		out2 = AlgoUtil.geoToTile(in2);
		assertEquals(exp2.getX(), out2.getX());
		assertEquals(exp2.getY(), out2.getY());
		
		/* ÜBERPRÜFEN... SCHLÄGT NOCH FEHL */
		FloatVector3D in3 = new FloatVector3D(-17.8546f, -41.4720f, 11);
		IntegerVector3D out3 = new IntegerVector3D(0, 0, 0);
		IntegerVector3D exp3 = new IntegerVector3D(787, 1127, 11);
		out3 = AlgoUtil.geoToTile(in3);
		assertEquals(exp3.getX(), out3.getX());
		assertEquals(exp3.getY(), out3.getY());
	}

	@Test
	public void testTileToGLIntegerVector3D() {
		/* TEST 1 */
		IntegerVector3D tile1 = new IntegerVector3D(17, 15, 5);
		FloatVector3D EXP_glVector1 = new FloatVector3D(1024.0f, 1024.0f, 1024.0f);
		
		FloatVector3D TEST_glVector1 = AlgoUtil.tileToGL(tile1);
		assertEquals(EXP_glVector1.getX(), TEST_glVector1.getX(), EPSILON);
		assertEquals(EXP_glVector1.getY(), TEST_glVector1.getY(), EPSILON);
		
		/* TEST 2 */
		IntegerVector3D tile2 = new IntegerVector3D(18, 15, 5);
		FloatVector3D EXP_glVector2 = new FloatVector3D(2048.0f, 1024.0f, 1024.0f);
		
		FloatVector3D TEST_glVector2 = AlgoUtil.tileToGL(tile2);
		assertEquals(EXP_glVector2.getX(), TEST_glVector2.getX(), EPSILON);
		assertEquals(EXP_glVector2.getY(), TEST_glVector2.getY(), EPSILON);
		
		/* TEST 3 */
		IntegerVector3D tile3 = new IntegerVector3D(13, 17, 5);
		FloatVector3D EXP_glVector3 = new FloatVector3D(-3072.0f, -1024.0f, 1024.0f);
		
		FloatVector3D TEST_glVector3 = AlgoUtil.tileToGL(tile3);
		assertEquals(EXP_glVector3.getX(), TEST_glVector3.getX(), EPSILON);
		assertEquals(EXP_glVector3.getY(), TEST_glVector3.getY(), EPSILON);
	}

	@Test
	public void testGlToTileZoom() {
		/* TEST 1 */
		float glZoom1 = 1024.0f;
		int EXP_tileZoom1 = 5;
		int TEST_tileZoom1 = AlgoUtil.glToTileZoom(glZoom1);
		assertEquals(EXP_tileZoom1, TEST_tileZoom1);
		
		/* TEST 2 */
		float glZoom2 = 512.0f;
		int EXP_tileZoom2 = 6;
		int TEST_tileZoom2 = AlgoUtil.glToTileZoom(glZoom2);
		assertEquals(EXP_tileZoom2, TEST_tileZoom2);
		
		/* TEST 3 */
		float glZoom3 = 1.0f;
		int EXP_tileZoom3 = 15;
		int TEST_tileZoom3 = AlgoUtil.glToTileZoom(glZoom3);
		assertEquals(EXP_tileZoom3, TEST_tileZoom3);
	}
	
	@Test
	public void testTileToGlZoom() {
		float tileZoom = 5.0f;
		float glZoom = 1024.0f;
		float result = AlgoUtil.tileToGLZoom(tileZoom);
		assertEquals(result, glZoom, EPSILON);
	}
	
	@Test
	public void glToFloatTileFloatVector3D() {
		/* TEST 1 */
		FloatVector3D glVector1 = new FloatVector3D(512.0f, 512.0f, 1024.0f);
		FloatVector3D EXP_floatTileVector1 = new FloatVector3D(16.5f, 15.5f, 5);
		// Test
		FloatVector3D TEST_floatTileVector1 = AlgoUtil.glToFloatTile(glVector1);
		assertEquals(EXP_floatTileVector1.getX(), TEST_floatTileVector1.getX(), EPSILON);
		assertEquals(EXP_floatTileVector1.getY(), TEST_floatTileVector1.getY(), EPSILON);
		assertEquals(EXP_floatTileVector1.getZ(), TEST_floatTileVector1.getZ(), EPSILON);
		
		/* TEST 2 */
		FloatVector3D glVector2 = new FloatVector3D(0.0f, 2048.0f, 1024.0f);
		FloatVector3D EXP_floatTileVector2 = new FloatVector3D(16.0f, 14.0f, 5);
		// Test
		FloatVector3D TEST_floatTileVector2 = AlgoUtil.glToFloatTile(glVector2);
		assertEquals(EXP_floatTileVector2.getX(), TEST_floatTileVector2.getX(), EPSILON);
		assertEquals(EXP_floatTileVector2.getY(), TEST_floatTileVector2.getY(), EPSILON);
		assertEquals(EXP_floatTileVector2.getZ(), TEST_floatTileVector2.getZ(), EPSILON);
		
		/* TEST 3 */
		FloatVector3D glVector3 = new FloatVector3D(-1536.0f, -2048.0f, 1024.0f);
		FloatVector3D EXP_floatTileVector3 = new FloatVector3D(14.5f, 18.0f, 5);
		// Test
		FloatVector3D TEST_floatTileVector3 = AlgoUtil.glToFloatTile(glVector3);
		assertEquals(EXP_floatTileVector3.getX(), TEST_floatTileVector3.getX(), EPSILON);
		assertEquals(EXP_floatTileVector3.getY(), TEST_floatTileVector3.getY(), EPSILON);
		assertEquals(EXP_floatTileVector3.getZ(), TEST_floatTileVector3.getZ(), EPSILON);
		
		/* TEST 4 */
		FloatVector3D glVector4 = new FloatVector3D(16368.0f, -16368.0f, 32.0f);
		FloatVector3D EXP_floatTileVector4 = new FloatVector3D(1023.5f, 1023.5f, 10);
		// Test
		FloatVector3D TEST_floatTileVector4 = AlgoUtil.glToFloatTile(glVector4);
		assertEquals(EXP_floatTileVector4.getX(), TEST_floatTileVector4.getX(), EPSILON);
		assertEquals(EXP_floatTileVector4.getY(), TEST_floatTileVector4.getY(), EPSILON);
		assertEquals(EXP_floatTileVector4.getZ(), TEST_floatTileVector4.getZ(), EPSILON);
	}
	
	@Test
	public void testTileToGLBox() {
		/* TEST 1 */
		IntegerVector3D tile1 = new IntegerVector3D(17, 15, 5);
		FloatVector3D CMP_upperLeft = new FloatVector3D(1024.0f, 1024.0f, 1024.0f);
		FloatVector3D CMP_lowerRight = new FloatVector3D(2048.0f, 0.0f, 1024.0f);
		FloatBoundingBox TEST_box = AlgoUtil.tileToGLBox(tile1);
		FloatVector3D TEST_upperLeft = TEST_box.getUpperLeft();
		FloatVector3D TEST_lowerRight = TEST_box.getLowerRight();
		assertEquals(CMP_upperLeft.getX(), TEST_upperLeft.getX(), EPSILON);
		assertEquals(CMP_upperLeft.getY(), TEST_upperLeft.getY(), EPSILON);
		assertEquals(CMP_upperLeft.getZ(), TEST_upperLeft.getZ(), EPSILON);
		assertEquals(CMP_lowerRight.getX(), TEST_lowerRight.getX(), EPSILON);
		assertEquals(CMP_lowerRight.getY(), TEST_lowerRight.getY(), EPSILON);
		assertEquals(CMP_lowerRight.getZ(), TEST_lowerRight.getZ(), EPSILON);
	}
	
	/*
	@Test
	public void testRotateAroundX() {
		fail("Not yet implemented.");
	}
	
	@Test
	public void testRotateAroundY() {
		fail("Not yet implemented.");
	}
	*/
	
	@Test
	public void testRotateAroundZ() {
		/* TEST 1 */
		float angle1 = 90.0f;
		FloatVector3D vector1 = new FloatVector3D(-3.0f, 4.0f, 0.0f);
		FloatVector3D vector2 = new FloatVector3D(-3.0f, -4.0f, 0.0f);
		FloatVector3D vector3 = new FloatVector3D(3.0f, -4.0f, 0.0f);
		FloatVector3D vector4 = new FloatVector3D(3.0f, 4.0f, 0.0f);
		FloatVector3D EXP_vector1 = new FloatVector3D(-4.0f, -3.0f, 0.0f);
		FloatVector3D EXP_vector2 = new FloatVector3D(4.0f, -3.0f, 0.0f);
		FloatVector3D EXP_vector3 = new FloatVector3D(4.0f, 3.0f, 0.0f);
		FloatVector3D EXP_vector4 = new FloatVector3D(-4.0f, 3.0f, 0.0f);
		// Test
		FloatVector3D TEST_vector1 = AlgoUtil.rotateAroundZ(vector1, angle1);
		FloatVector3D TEST_vector2 = AlgoUtil.rotateAroundZ(vector2, angle1);
		FloatVector3D TEST_vector3 = AlgoUtil.rotateAroundZ(vector3, angle1);
		FloatVector3D TEST_vector4 = AlgoUtil.rotateAroundZ(vector4, angle1);
		assertEquals(EXP_vector1.getX(), TEST_vector1.getX(), EPSILON);
		assertEquals(EXP_vector1.getY(), TEST_vector1.getY(), EPSILON);
		assertEquals(EXP_vector2.getX(), TEST_vector2.getX(), EPSILON);
		assertEquals(EXP_vector2.getY(), TEST_vector2.getY(), EPSILON);
		assertEquals(EXP_vector3.getX(), TEST_vector3.getX(), EPSILON);
		assertEquals(EXP_vector3.getY(), TEST_vector3.getY(), EPSILON);
		assertEquals(EXP_vector4.getX(), TEST_vector4.getX(), EPSILON);
		assertEquals(EXP_vector4.getY(), TEST_vector4.getY(), EPSILON);
	}

}
