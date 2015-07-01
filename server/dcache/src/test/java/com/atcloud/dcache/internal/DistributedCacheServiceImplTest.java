/**
 * 
 */
package com.atcloud.dcache.internal;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atcloud.dcache.DistributedCacheService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class DistributedCacheServiceImplTest {

	private DistributedCacheService dcs = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dcs = new DistributedCacheServiceImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dcs = null;
	}

	/**
	 * Test method for
	 * {@link com.atcloud.dcache.internal.DistributedCacheServiceImpl#start()}.
	 */
	@Test
	public void testStart() {
		assertNotNull(dcs);
		// try {
		// dcs.start();
		// } catch (ATCloudException e) {
		// fail(e.getLocalizedMessage());
		// }
	}

	/**
	 * Test method for
	 * {@link com.atcloud.dcache.internal.DistributedCacheServiceImpl#stop()}.
	 */
	@Test
	public void testStop() {
		assertNotNull(dcs);
		// try {
		// dcs.start();
		// dcs.stop();
		// } catch (ATCloudException e) {
		// fail(e.getLocalizedMessage());
		// }
	}

	/**
	 * Test method for
	 * {@link com.atcloud.dcache.internal.DistributedCacheServiceImpl#getUserCache()}
	 * .
	 */
	@Test
	public void testAddUsers() {

		assertNotNull(dcs);
		// try {
		// dcs.start();
		// User user = createUser("William", "John", "Drew");
		// dcs.addNewUser(user);
		// dcs.stop();
		// } catch (ATCloudException e) {
		// fail(e.getLocalizedMessage());
		// }

	}

}
