/**
 * 
 */
package com.atcloud.commons.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atcloud.commons.CommonsService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class CommonsServiceImplTest {

	private CommonsService cs = null;

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
		cs = new CommonsServiceImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		cs = null;
	}

	/**
	 * 
	 */
	@Test
	public void testFindAvailablePort() {

		assertNotNull(cs);
		int availablePort = cs.findAvailablePort(6000, 6010);
		assertTrue(availablePort != 0);
		assertTrue(cs.portAvailable(availablePort));
	}

}
