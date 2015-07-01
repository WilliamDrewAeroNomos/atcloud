/**
 * 
 */
package com.atcloud.persistence.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.commons.internal.CommonsServiceImpl;
import com.atcloud.model.FEM;
import com.atcloud.model.Federate;
import com.atcloud.model.Group;
import com.atcloud.model.ModelService;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class FEMPersistenceServiceImplTest {

	private static EntityManager em;

	private static final String femName = "Junit Test FEM";
	private static final String femDescriptionUpdate =
			"FEM UPDATED during JUnit testing";

	private static final String federateName = "Frasca CRJ";
	private static final String federateDescription =
			"Frasca FTD in Murfreesboro AP";
	private static final String femUpdatedDescriptionUpdate =
			"I just updated this to -> Frasca FTD in Murfreesboro AP";

	private static PersistenceService persistenceService;
	private static ModelService modelService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		CommonsService commonsService = new CommonsServiceImpl();

		persistenceService = new PersistenceServiceImpl();

		assertNotNull(persistenceService);

		/**
		 * Setup the FEMPersistenceServiceImpl
		 */

		/**
		 * Initialize the embedded DB
		 */
		try {
			NamingManager.setInitialContextFactoryBuilder(commonsService
					.getInitialContextFactoryBuilder());
			InitialContext ic = new InitialContext();
			EmbeddedDataSource ds = new EmbeddedDataSource();
			ds.setDatabaseName("target/test");
			ds.setCreateDatabase("create");
			ic.bind(
					"osgi:service/javax.sql.DataSource/(osgi.jndi.service.name=jdbc/derbyds)",
					ds);

			EntityManagerFactory emf =
					Persistence.createEntityManagerFactory("userTest",
							System.getProperties());

			em = emf.createEntityManager();
			persistenceService.setEntityManager(em);

		} catch (Throwable t) {
			fail(t.getLocalizedMessage());
		}

		modelService = new ModelServiceImpl();

		modelService
				.setSchemaFactorySourceLocator(new PersistenceTestingSchemaFactorySourceLocator());

		modelService.start();

		persistenceService.setModelService(modelService);

		/**
		 * Create unique index on NAME_
		 */
		em.getTransaction().begin();

		Query q =
				em.createNativeQuery("CREATE UNIQUE INDEX "
						+ "I_NAME_LASTNAME_FIRSTNAME ON NAME_ (LAST_, FIRST_)");

		q.executeUpdate();

		em.getTransaction().commit();

		/**
		 * Create unique index on ROLE_
		 */

		em.getTransaction().begin();

		q =
				em.createNativeQuery("CREATE UNIQUE INDEX "
						+ "I_ROLE_NAME ON ROLE_ (NAME_)");

		q.executeUpdate();

		em.getTransaction().commit();

		/**
		 * Create unique index on FEM
		 */
		em.getTransaction().begin();

		q =
				em.createNativeQuery("CREATE UNIQUE INDEX "
						+ "I_NAME_FEMNAME ON FEM (NAME_)");

		q.executeUpdate();

		em.getTransaction().commit();

		/**
		 * Create unique index on FEDERATE
		 */

		em.getTransaction().begin();

		q =
				em.createNativeQuery("CREATE UNIQUE INDEX "
						+ "I_NAME_FEDERATENAME ON FEDERATE (NAME_)");

		q.executeUpdate();

		em.getTransaction().commit();

	}

	/**
	 * 
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		/**
		 * Remove all the FEMs before each test starts
		 */
		em.getTransaction().begin();

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllFEMs();

		persistenceService.deleteAllFederates();

		em.getTransaction().commit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		/**
		 * Ensure that all FEMs are removed at the end of each test
		 */
		em.getTransaction().begin();

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllFEMs();

		persistenceService.deleteAllFederates();

		em.getTransaction().commit();
	}

	/**
	 * 
	 */
	@Test
	public void testFEMCrud() {

		FEM fem = createFEM(femName);

		/*
		 * Now add the new FEM
		 */

		em.getTransaction().begin();

		persistenceService.add(fem);

		em.getTransaction().commit();

		/*
		 * Test the unique INDEX constraint
		 */
		FEM dupFEM = createFEM(femName);

		em.getTransaction().begin();

		try {
			persistenceService.add(dupFEM);
			fail("This should have been a unique index violation");
		} catch (EntityExistsException e) {
			em.getTransaction().rollback();
		}

		/*
		 * Query the just added FEM and test that it's equal to the original FEM
		 * object
		 */

		FEM queriedFEM = persistenceService.getFEM(fem.getName());

		assertNotNull(queriedFEM);

		assertTrue(queriedFEM.equals(fem));

		/*
		 * Update this FEM with a new description
		 */
		fem.setDescription(femDescriptionUpdate);

		em.getTransaction().begin();

		try {
			persistenceService.update(fem);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Query the updated FEM from the persistent store and test that it's equal
		 * to the updated FEM object
		 */

		queriedFEM = persistenceService.getFEM(fem.getName());

		assertNotNull(queriedFEM);

		assertTrue(queriedFEM.equals(fem));

		/*
		 * Create and add a federate
		 */

		Federate federate = new Federate();

		federate.setName(federateName);
		federate.setDescription(federateDescription);

		em.getTransaction().begin();

		persistenceService.add(federate);

		em.getTransaction().commit();

		Federate dupFederate = new Federate();
		dupFederate.setName(federateName);

		em.getTransaction().begin();
		try {
			persistenceService.add(dupFederate);
			fail("This should have been a unique index violation");
		} catch (EntityExistsException e) {
			em.getTransaction().rollback();
		}

		/*
		 * Get the Federate that you just added and check if it's attributes
		 */

		Federate queriedFederate =
				persistenceService.getFederate(federate.getName());

		assertNotNull(queriedFederate);

		assertTrue(queriedFederate.equals(federate));

		/*
		 * Update the federate
		 */

		fem.setDescription(femUpdatedDescriptionUpdate);

		em.getTransaction().begin();

		try {
			persistenceService.update(federate);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Get the Federate that you just added and check if it's attributes have
		 * indeed been updated
		 */

		queriedFederate = persistenceService.getFederate(federate.getName());

		/*
		 * Should have found
		 */
		assertNotNull(queriedFederate);

		/*
		 * And should match the with the updated local object
		 */
		assertTrue(queriedFederate.equals(federate));

		/*
		 * Add the federate to the FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(queriedFederate, queriedFEM);

		em.getTransaction().commit();

		/*
		 * Get the list of federates assigned to the FEM.
		 */

		List<Federate> federateList = null;
		try {
			federateList = persistenceService.getFederatesInFEM(femName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		Iterator<Federate> federateIter = federateList.iterator();

		assertNotNull(federateIter);

		while (federateIter.hasNext()) {

			Federate nextFederate = federateIter.next();
			assertNotNull(nextFederate);

			/*
			 * This should be the Federate that we assigned earlier
			 */
			assertTrue(nextFederate.equals(queriedFederate));
		}

		/*
		 * Remove the federate from the FEM
		 */

		em.getTransaction().begin();

		persistenceService.removeFederateFromFEM(federateName, femName);

		em.getTransaction().commit();

		/*
		 * Get the list of federates assigned to the FEM. Note this should be empty
		 * after removing them in the previous statements
		 */

		try {
			federateList = persistenceService.getFederatesInFEM(femName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertTrue(federateList.size() == 0);

	}

	/**
	 * 
	 */
	@Test
	public void testMultipleFederateFEM() {

		String multiFederateFEMName = "multiFederateFEM";

		FEM multiFederateFEM = createFEM(multiFederateFEMName);

		/*
		 * Now add the new FEM
		 */

		em.getTransaction().begin();

		persistenceService.add(multiFederateFEM);

		em.getTransaction().commit();

		/*
		 * Test adding multiple federates to the FEM
		 */

		/*
		 * Create the first federate
		 */

		Federate multiFederateOne = new Federate();

		String multiFederateOneName = "multiFederateOne";

		multiFederateOne.setName(multiFederateOneName);
		multiFederateOne
				.setDescription("This is the first of two federates in this federation");

		em.getTransaction().begin();

		persistenceService.add(multiFederateOne);

		em.getTransaction().commit();

		/*
		 * Add the first federate to the FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(multiFederateOne, multiFederateFEM);

		em.getTransaction().commit();

		/*
		 * Create the second federate
		 */

		Federate multiFederateTwo = new Federate();

		String multiFederateTwoName = "multiFederateTwo";

		multiFederateTwo.setName(multiFederateTwoName);
		multiFederateTwo
				.setDescription("This is the second of two federates in this federation");

		em.getTransaction().begin();

		persistenceService.add(multiFederateTwo);

		em.getTransaction().commit();

		/*
		 * Add the second federate to the FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(multiFederateTwo, multiFederateFEM);

		em.getTransaction().commit();

		/*
		 * Get the list of federates assigned to the FEM.
		 */

		List<Federate> federateList = null;
		try {
			federateList =
					persistenceService.getFederatesInFEM(multiFederateFEM.getName());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(federateList);
		assertTrue(federateList.size() == 2);

		Iterator<Federate> federateIter = federateList.iterator();

		assertNotNull(federateIter);

		while (federateIter.hasNext()) {

			Federate nextFederate = federateIter.next();
			assertNotNull(nextFederate);

			assertTrue(nextFederate.equals(multiFederateOne)
					|| nextFederate.equals(multiFederateTwo));
		}

	}

	/**
	 * 
	 */
	@Test
	public void testSingleFederateInMultipleFEMs() {

		/*
		 * Create the first FEM
		 */
		String femNameOne = "femNameOne";

		FEM femOne = createFEM(femNameOne);

		/*
		 * Now add the first FEM
		 */
		em.getTransaction().begin();

		persistenceService.add(femOne);

		em.getTransaction().commit();

		/*
		 * Create the second FEM
		 */
		String femNameTwo = "femNameTwo";

		FEM femTwo = createFEM(femNameTwo);

		/*
		 * Now add the second FEM
		 */

		em.getTransaction().begin();

		persistenceService.add(femTwo);

		em.getTransaction().commit();

		/*
		 * Create the federate
		 */

		Federate federate = new Federate();

		String federateName = "federate";

		federate.setName(federateName);
		federate.setDescription("This federate is in two federations");

		/*
		 * Add the federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federate);

		em.getTransaction().commit();

		/*
		 * Add the federate to the first FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(federate, femOne);

		em.getTransaction().commit();

		/*
		 * Add the federate to the second FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(federate, femTwo);

		em.getTransaction().commit();

		/*
		 * Get the list of federates assigned to the first FEM.
		 */

		List<Federate> federateList = null;
		try {
			federateList = persistenceService.getFederatesInFEM(femOne.getName());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(federateList);
		assertTrue(federateList.size() == 1);

		Iterator<Federate> federateIter = federateList.iterator();

		assertNotNull(federateIter);

		while (federateIter.hasNext()) {

			Federate nextFederate = federateIter.next();
			assertNotNull(nextFederate);

			assertTrue(nextFederate.equals(federate));
		}

		/*
		 * Get the list of federates assigned to the second FEM.
		 */

		try {
			federateList = persistenceService.getFederatesInFEM(femTwo.getName());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(federateList);
		assertTrue(federateList.size() == 1);

		federateIter = federateList.iterator();

		assertNotNull(federateIter);

		while (federateIter.hasNext()) {

			Federate nextFederate = federateIter.next();
			assertNotNull(nextFederate);

			assertTrue(nextFederate.equals(federate));
		}

	}

	@Test
	public void testGetAllFederates() {

		/*
		 * Create the federate
		 */

		Federate federateOne =
				modelService.createFederate("Test Federate One", "Test federate #1");

		/*
		 * Add the federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateOne);

		em.getTransaction().commit();

		Federate federateTwo =
				modelService.createFederate("Test Federate Two", "Test federate #1");

		/*
		 * Add the second federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateTwo);

		em.getTransaction().commit();

		/*
		 * Query all the federates
		 */
		List<Federate> federateList = persistenceService.getFederates();

		/*
		 * Should be 2 federates in the list
		 */
		assertTrue(federateList.size() == 2);

		/*
		 * Both of them should be found in the returned list
		 */
		for (Federate nextFederate : federateList) {
			assertTrue((nextFederate.equals(federateOne))
					|| (nextFederate.equals(federateTwo)));
		}
	}

	@Test
	public void testGetAllFederatesInAGroup() {

		String testFederateOneName = "Test Federate One";
		String testFederateTwoName = "Test Federate Two";
		/*
		 * Create the federate
		 */

		Federate federateOne =
				modelService.createFederate(testFederateOneName, "Test federate #1");

		/*
		 * Add the federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateOne);

		em.getTransaction().commit();

		Federate federateTwo =
				modelService.createFederate(testFederateTwoName, "Test federate #1");

		/*
		 * Add the second federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateTwo);

		em.getTransaction().commit();

		/*
		 * Query all the federates
		 */
		List<Federate> federateList = persistenceService.getFederates();

		/*
		 * Should be 2 federates in the list
		 */
		assertTrue(federateList.size() == 2);

		/*
		 * Both of them should be found in the returned list
		 */
		for (Federate nextFederate : federateList) {
			assertTrue((nextFederate.equals(federateOne))
					|| (nextFederate.equals(federateTwo)));
		}

		Calendar calendar = Calendar.getInstance();
		String femNameOne = "FEM Name One";

		/*
		 * Create a group and make the active FEM the one we just added
		 */
		String groupName = "Federate Test Group";

		Group group = new Group();

		group.setDate(calendar);
		group.setDescription("This group contains a federate");
		group.setName(groupName);
		group.setActiveFEMName(femNameOne);

		/*
		 * Add the group
		 */
		em.getTransaction().begin();

		persistenceService.add(group);

		em.getTransaction().commit();

		/*
		 * Assign Test Federate One to the group
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToGroup(federateOne.getName(),
				group.getName());

		em.getTransaction().commit();

		List<Federate> federatesInGroup =
				persistenceService.getFederatesInGroup(group.getName());

		/*
		 * Test Federate One should be found in the returned list
		 */
		for (Federate nextFederate : federatesInGroup) {
			assertTrue(nextFederate.equals(federateOne));
		}

		/*
		 * Assign Test Federate Two to the group
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToGroup(federateTwo.getName(),
				group.getName());

		em.getTransaction().commit();

		federatesInGroup = persistenceService.getFederatesInGroup(group.getName());

		/*
		 * Test Federate One and Test Federate Two should be found in the returned
		 * list
		 */
		for (Federate nextFederate : federatesInGroup) {
			assertTrue(nextFederate.equals(federateOne)
					|| nextFederate.equals(federateTwo));
		}

	}

	/**
	 * 
	 */
	@Test
	public void testFederateCRUD() {

		/*
		 * Create 2 federates and then query them back to ensure they've been added
		 */
		String federateName = "Test Federate One";
		String federateDescription = "This is a description of a test federate";

		Federate federateOne = new Federate();
		federateOne.setName(federateName);
		federateOne.setDescription(federateDescription);

		/*
		 * Add the first federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateOne);

		em.getTransaction().commit();

		federateName = "Test Federate Two";

		Federate federateTwo = new Federate();
		federateTwo.setName(federateName);
		federateTwo.setDescription(federateDescription);

		/*
		 * Add the second federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federateTwo);

		em.getTransaction().commit();

		/*
		 * Query all the federates
		 */
		List<Federate> federateList = persistenceService.getFederates();

		/*
		 * Should be 2 federates in the list
		 */
		assertTrue(federateList.size() == 2);

		/*
		 * Both of them should be found in the returned list
		 */
		for (Federate nextFederate : federateList) {
			assertTrue((nextFederate.equals(federateOne))
					|| (nextFederate.equals(federateTwo)));
		}

	}

	/**
	 * 
	 */
	@Test
	public void testAssignFederateToGroup() {

		/*
		 * Create the FEM
		 */
		String femNameOne = "femNameOne";

		FEM femOne = createFEM(femNameOne);
		femOne.setDescription("Testing Federate assignment to Group");

		/*
		 * Now add the FEM
		 */
		em.getTransaction().begin();

		persistenceService.add(femOne);

		em.getTransaction().commit();

		/*
		 * Create the federate
		 */

		String federateName = "Test Federate1";
		String federateDescription = "This is the federate description";

		Federate federate = new Federate();
		federate.setName(federateName);
		federate.setDescription(federateDescription);

		/*
		 * Add the federate
		 */
		em.getTransaction().begin();

		persistenceService.add(federate);

		em.getTransaction().commit();

		Calendar calendar = Calendar.getInstance();

		/*
		 * Add the federate to the FEM
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToFEM(federate, femOne);

		em.getTransaction().commit();

		/**
		 * Create a group and make the active FEM the one we just added
		 */
		String groupName = "Federate Test Group";

		Group group = new Group();

		group.setDate(calendar);
		group.setDescription("This group contains a federate");
		group.setName(groupName);
		group.setActiveFEMName(femNameOne);

		/**
		 * Add the group
		 */
		em.getTransaction().begin();

		persistenceService.add(group);

		em.getTransaction().commit();

		/**
		 * Assign the federate to the group
		 */

		em.getTransaction().begin();

		persistenceService.addFederateToGroup(federateName, groupName);

		em.getTransaction().commit();

		/*
		 * Retrieve the group to which the federate belongs
		 */

		Group federateGroup = persistenceService.getFederateGroup(federateName);

		assertNotNull(federateGroup);

		assertTrue(group.equals(federateGroup));

		/*
		 * Retrieve the active FEM for the group which should be equal to the one we
		 * set earlier
		 */

		FEM groupFEM =
				persistenceService.getActiveFEMForGroup(federateGroup.getName());

		assertNotNull(groupFEM);

		assertTrue(groupFEM.equals(femOne));

		/*
		 * Get the active FEM via the federate name
		 */

		FEM activeFEM = persistenceService.getFederateActiveFEM(federateName);

		assertNotNull(activeFEM);

		assertTrue(activeFEM.equals(femOne));

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private FEM createFEM(String name) {

		FEM fem = new FEM();

		fem.setFemID(UUID.randomUUID().toString());
		fem.setScenarioID(UUID.randomUUID().toString());
		fem.setName(name);
		fem.setDescription("This is a standard FEM");
		fem.setLogicalStrtTimeMSecs(20000L);
		fem.setAutoStart(true);
		fem.setDefDurStrtupPrtclMSecs(10000L);
		fem.setPlannedStartTimeMSecs(Calendar.getInstance().getTimeInMillis());
		fem.setFederationExecutionMSecs(360000);
		fem.setJoinFederationMSecs(100000L);
		fem.setRegisterPublicationMSecs(30000L);
		fem.setRegisterSubscriptionMSecs(40000L);
		fem.setRegisterToRunMSecs(50000L);
		fem.setWaitForStartMSecs(70000L);
		fem.setWaitTimeAfterTermMSecs(80000L);

		return fem;
	}

}
