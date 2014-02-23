package sep.gaia.resources.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.resources.DuplicateFreeQueue;

/**
 * 
 * Class to test all public methods of the class
 * <code>sep.gaia.resources.DuplicateFreeQueue</code>.
 * 
 * @author Max Witzelsperger
 *
 */
public class DuplicateFreeQueueTest {

	/**
	 * The instance of <code>DuplicateFreeQueue</code> to be tested.
	 */
	private static DuplicateFreeQueue<Integer> queue;
	
	@BeforeClass
	/**
	 * Initializes the queue.
	 */
	public static void initQueue() {
		
		queue = new DuplicateFreeQueue<Integer>();
	}
	
	@After
	/**
	 * Makes the queue empty after each single test. The <code>clear</code>
	 * method is not used because it is to be tested itself.
	 */
	public void clearQueue() {
		
		queue = new DuplicateFreeQueue<Integer>();
	}
	
	@Test
	/**
	 * Tests the <code>push</code> and the <code>pop</code> method.
	 */
	public void testPushAndPop() {
		
		queue.push(0);
		queue.push(1);
		
		queue.pop();
		
		assertEquals(new Integer(1), queue.pop());
	}
	
	@Test
	/**
	 * Tests the <code>push</code> method without priority
	 * and the <code>next</code> method.
	 */
	public void testPushAndNext() {
		
		queue.push(0);
		queue.push(1);
		
		assertEquals(new Integer(0), queue.next());
	}
	
	@Test
	/**
	 * Tests the <code>push</code> push method with priority and
	 * the <code>next</code> method.
	 */
	public void testPrioritizedPushAndNext() {
		
		queue.push(0, 10);
		queue.push(1, 5);
		queue.push(2, 1000000);
		queue.push(3, -20);
		
		assertEquals(new Integer(2), queue.next());
	}
	
	@Test
	/**
	 * Tests the <code>isEmpty</code> method.
	 */
	public void testIsEmpty() {
		
		boolean firstEmpty = queue.isEmpty();
		
		queue.push(0);
		
		boolean stillEmpty = queue.isEmpty();
		
		assertTrue(firstEmpty && !stillEmpty);
	}
	
	@Test
	/**
	 * Tests the <code>clear</code> method.
	 */
	public void testClear() {
		
		queue.push(0);
		queue.push(1);
		
		queue.clear();
		
		assertTrue(queue.isEmpty());
	}
	
	@Test
	/**
	 * Tests whether values can not be added twice.
	 */
	public void testDuplicateFreeness() {
		
		queue.push(0);
		queue.push(0);
		
		queue.pop();
		
		assertTrue(queue.isEmpty());
	}
}
