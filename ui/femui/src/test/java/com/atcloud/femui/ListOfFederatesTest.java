/**
 * 
 */
package com.atcloud.femui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atcloud.model.Federate;
import com.atcloud.model.Federates;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class ListOfFederatesTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListOfFederates() {

		Federates fedDTO = new Federates();
		List<Federate> fedList = new ArrayList<Federate>();

		Federate f1 = new Federate();
		f1.setName("Frasca FTD");
		f1.setDescription("Frasca flight simulator @ Murfreesboro AP");

		fedList.add(f1);

		Federate f2 = new Federate();
		f2.setName("MTSU FOCUS lab");
		f2.setDescription("FOCUS lab at MTSU");

		fedList.add(f2);

		fedDTO.setFederates(fedList);

		List<Federate> fedListQueried = fedDTO.getFederates();

		assertNotNull(fedListQueried);

		assertTrue(fedListQueried.size() == 2);

		for (Federate thisFederate : fedListQueried) {
			assertTrue(thisFederate.equals(f1) || thisFederate.equals(f2));
		}

	}
}
