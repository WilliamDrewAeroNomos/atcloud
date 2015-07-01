/**
 * 
 */
package com.atcloud.persistence.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
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
import com.atcloud.model.AirFlightPosition;
import com.atcloud.model.ModelService;
import com.atcloud.model.Simulation;
import com.atcloud.model.SimulationMetrics;
import com.atcloud.model.SimulationStatusEnumType;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class MetricsPersistenceServiceImplTest {

	private static EntityManager em;

	private static ModelService modelService;

	private static PersistenceService persistenceService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		CommonsService commonsService = new CommonsServiceImpl();

		/**
		 * Setup the MetricServiceImpl
		 */
		persistenceService = new PersistenceServiceImpl();

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
		 * Create unique index on SIMULATION
		 */
		em.getTransaction().begin();

		Query q =
				em.createNativeQuery("CREATE UNIQUE INDEX "
						+ "I_SIMULATION_SIMULATIONID ON SIMULATION (SIMULATIONID)");

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
		 * Remove all the Scenarios, SimulationSimMetricsAssocs and
		 * SimulationMetrics rows before each test starts
		 */
		em.getTransaction().begin();

		persistenceService.deleteAllSimulations();

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

		persistenceService.deleteAllSimulations();

		em.getTransaction().commit();
	}

	/**
	 * 
	 */
	@Test
	public void testSimulationandSimulationMetricsCrud() {

		Calendar c = Calendar.getInstance();

		Simulation simulation = new Simulation();

		simulation.setTimestamp(c);
		simulation.setScenarioID(UUID.randomUUID().toString());
		simulation.setSimulationID(UUID.randomUUID().toString());
		simulation.setName("Test simulation");
		simulation.setDescription("Testing 1 2 3");
		simulation
				.setLoadedTimestampMSecs(Calendar.getInstance().getTimeInMillis());
		simulation.setStatus(SimulationStatusEnumType.LOADED);

		try {

			em.getTransaction().begin();

			persistenceService.addSimulation(simulation);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Record the metrics for the simulation
		 */

		SimulationMetrics simulationMetrics = new SimulationMetrics();

		simulationMetrics.setTimestamp(c);

		em.getTransaction().begin();

		persistenceService.recordSimulationExecutionMetrics(simulation,
				simulationMetrics);

		em.getTransaction().commit();

		Simulation simulationRetrieved =
				persistenceService.getSimulation(simulation.getSimulationID());

		assertNotNull(simulationRetrieved);

		assertTrue(simulationRetrieved.equals(simulation));

	}

	/**
	 * 
	 */
	@Test
	public void testRecordSimulationFlightPositionData() {

		Calendar c = Calendar.getInstance();

		Simulation simulation = new Simulation();
		simulation.setSimulationID(UUID.randomUUID().toString());
		simulation.setScenarioID(UUID.randomUUID().toString());
		simulation.setDescription("Testing the persistence functionality");
		simulation.setName("Test simulation");
		simulation.setLoadedTimestampMSecs(c.getTimeInMillis());
		simulation.setStatus(SimulationStatusEnumType.LOADED);

		simulation.setTimestamp(c);

		SimulationMetrics simulationMetrics = new SimulationMetrics();

		simulationMetrics.setTimestamp(c);

		AirFlightPosition airFlightPosition = new AirFlightPosition();

		airFlightPosition.setFlightPositionID(UUID.randomUUID().toString());
		airFlightPosition.setTimestamp(c);
		airFlightPosition.setAltitudeFt(30000);
		airFlightPosition.setAirspeedKts(450);

		em.getTransaction().begin();
		try {
			persistenceService.recordSimulationData(simulation, airFlightPosition);
			em.getTransaction().commit();
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
			em.getTransaction().rollback();
		}
	}
}
