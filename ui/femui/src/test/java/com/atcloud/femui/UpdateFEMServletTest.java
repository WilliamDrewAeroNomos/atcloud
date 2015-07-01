/**
 * 
 */
package com.atcloud.femui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.openjpa.persistence.PersistenceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.internal.CommonsServiceImpl;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.fem.internal.FederationExecutionModelServiceImpl;
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
public class UpdateFEMServletTest {

	private static ModelService modelService;
	private static PersistenceService persistenceService =
			new PersistenceServiceImpl();
	private static EntityManager em;
	private static UpdateFEMServlet updateFEMServlet;
	private static FEM fem = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		CommonsService commonsService = new CommonsServiceImpl();

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

		assertNotNull(persistenceService);

		persistenceService.setEntityManager(em);

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

		persistenceService.setModelService(modelService);

		FederationExecutionModelService femExecutionModelService =
				new FederationExecutionModelServiceImpl();

		femExecutionModelService.setCommonsService(commonsService);
		femExecutionModelService.setPersistenceService(persistenceService);
		femExecutionModelService.setModelService(modelService);

		femExecutionModelService.start();

		updateFEMServlet = new UpdateFEMServlet();

		updateFEMServlet.setModelService(modelService);
		updateFEMServlet.setFemService(femExecutionModelService);
		updateFEMServlet.setCommonsService(commonsService);

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
	 * Test method for
	 * {@link com.atcloud.femui.UpdateFEMServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 */
	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {

		fem = modelService.createFEM("TestFEM from doGet");

		/*
		 * Add a new FEM
		 */

		em.getTransaction().begin();

		try {

			persistenceService.add(fem);

			em.getTransaction().commit();

		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

		FEM femJustAdded = persistenceService.getFEM(fem.getName());

		/*
		 * Update the FEM via the UpdateFEMServlet
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();

		femJustAdded.setDescription("This is a test of the FEM update");

		request.setContent(fem.toString().getBytes());
		MockHttpServletResponse reponse = new MockHttpServletResponse();

		try {

			em.getTransaction().begin();

			updateFEMServlet.doGet(request, reponse);

			em.flush();

			em.getTransaction().commit();

			FEM retrievedUpdatedFEM =
					persistenceService.getFEM(femJustAdded.getName());

			assertTrue(retrievedUpdatedFEM.equals(fem));

		} catch (ServletException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.atcloud.femui.UpdateFEMServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * .
	 */
	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() {

		fem = modelService.createFEM("TestFEM From doPost");

		/*
		 * Add a new FEM
		 */

		em.getTransaction().begin();

		try {

			persistenceService.add(fem);

			em.getTransaction().commit();

		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

		FEM femJustAdded = persistenceService.getFEM(fem.getName());

		/*
		 * Update the FEM via the UpdateFEMServlet
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();

		femJustAdded.setDescription("This is a test of the FEM update");

		request.addParameter("FEM", fem.toString());

		request.setContent(fem.toString().getBytes());
		MockHttpServletResponse reponse = new MockHttpServletResponse();

		try {

			em.getTransaction().begin();

			updateFEMServlet.doPost(request, reponse);

			em.flush();

			em.getTransaction().commit();

			FEM retrievedUpdatedFEM =
					persistenceService.getFEM(femJustAdded.getName());

			assertTrue(retrievedUpdatedFEM.equals(fem));

		} catch (ServletException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testDoPostHttpServletRequestHttpServletResponseWithError() {

		fem = modelService.createFEM("TestFEM From doPost");

		/*
		 * Add a new FEM
		 */

		em.getTransaction().begin();

		try {

			persistenceService.add(fem);

			em.getTransaction().commit();

		} catch (PersistenceException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

		FEM femJustAdded = persistenceService.getFEM(fem.getName());

		/*
		 * Update the FEM via the UpdateFEMServlet
		 */
		MockHttpServletRequest request = new MockHttpServletRequest();

		femJustAdded.setDescription("This is a test of the FEM update");

		String bogusString = "Blah, blah, blah";

		request.setContent(bogusString.toString().getBytes());
		MockHttpServletResponse reponse = new MockHttpServletResponse();

		try {

			em.getTransaction().begin();

			updateFEMServlet.doPost(request, reponse);

			em.flush();

			em.getTransaction().commit();

			FEM retrievedUpdatedFEM =
					persistenceService.getFEM(femJustAdded.getName());

			assertTrue(retrievedUpdatedFEM.equals(fem));

		} catch (ServletException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
