/**
 * 
 */
package com.atcloud.fem.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.commons.internal.CommonsServiceImpl;
import com.atcloud.dcache.DistributedCacheService;
import com.atcloud.dcache.internal.DistributedCacheServiceImpl;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.FEM;
import com.atcloud.model.ModelService;
import com.atcloud.model.SchemaFactorySourceLocator;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;
import com.atcloud.persistence.internal.PersistenceServiceImpl;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class FederationExecutionModelServiceImplTest {

	private static EntityManager em;
	private static FederationExecutionModelService federationExecutionModelService;

	private static ModelService modelService;
	private static PersistenceService persistenceService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		federationExecutionModelService = new FederationExecutionModelServiceImpl();

		CommonsService commonsService = new CommonsServiceImpl();

		commonsService.start();

		federationExecutionModelService.setCommonsService(commonsService);

		persistenceService = new PersistenceServiceImpl();

		/*
		 * Initialize the embedded DB
		 */

		try {

			InitialContextFactoryBuilder icfb =
					commonsService.getInitialContextFactoryBuilder();

			assertNotNull(icfb);

			NamingManager.setInitialContextFactoryBuilder(icfb);

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

		persistenceService.start();

		federationExecutionModelService.setPersistenceService(persistenceService);

		DistributedCacheService distributeCacheService =
				new DistributedCacheServiceImpl();

		distributeCacheService.start();

		federationExecutionModelService.setDcacheService(distributeCacheService);

		em.getTransaction().begin();

		federationExecutionModelService.start();

		em.getTransaction().commit();

		modelService = new ModelServiceImpl();

		modelService
				.setSchemaFactorySourceLocator(new SchemaFactorySourceLocator() {

					@Override
					public String getURL(String sourceName)
							throws ATCloudDataModelException {
						return System.getenv("ATCLOUD_SCHEMA_DIR") + "/" + sourceName;

					}

				});

		modelService.start();

		federationExecutionModelService.setModelService(modelService);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		em.getTransaction().begin();

		persistenceService.deleteAllFEMs();

		em.getTransaction().commit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		em.getTransaction().begin();

		persistenceService.deleteAllFEMs();

		em.getTransaction().commit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	/**
	 * 
	 */
	@Test
	public void testAddFEM() {

		String femName = "Test FEM";

		FEM fem = modelService.createFEM(femName);

		fem.setFemID(UUID.randomUUID().toString());

		fem.setAutoStart(true);
		fem.setDefDurStrtupPrtclMSecs(0L);
		fem.setFederationExecutionMSecs(0L);
		fem.setJoinFederationMSecs(0L);
		fem.setRegisterPublicationMSecs(0L);
		fem.setRegisterSubscriptionMSecs(0L);
		fem.setRegisterToRunMSecs(0L);
		fem.setWaitForStartMSecs(0L);
		fem.setWaitTimeAfterTermMSecs(0L);

		em.getTransaction().begin();

		try {

			federationExecutionModelService.addFEM(fem);

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		List<FEM> fems = federationExecutionModelService.getAllFEMs();

		assertTrue(fems.contains(fem));

		String newDescription = "This is a NEW description";
		fem.setDescription(newDescription);

		/*
		 * Update the FEM
		 */

		em.getTransaction().begin();

		try {
			federationExecutionModelService.updateFEM(fem);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		FEM queriedFEM = federationExecutionModelService.getFEM(fem.getName());

		assertNotNull(queriedFEM);

		assertTrue(queriedFEM.equals(fem));

		FEM queriedWithIDFEM = null;

		try {
			queriedWithIDFEM =
					federationExecutionModelService.getFEMByID(fem.getFemID());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(queriedWithIDFEM);

		assertTrue(queriedWithIDFEM.equals(fem));
	}

}
