/**
 * 
 */
package com.atcloud.license.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

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
import com.atcloud.license.LicenseService;
import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.License;
import com.atcloud.model.ModelService;
import com.atcloud.model.Organization;
import com.atcloud.model.SchemaFactorySourceLocator;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;
import com.atcloud.persistence.internal.PersistenceServiceImpl;
import com.atcloud.user.UserService;
import com.atcloud.user.internal.UserServiceImpl;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class LicenseServiceImplTest {

	private static EntityManager em;
	private static UserService userService;

	private static ModelService modelService;
	private static PersistenceService persistenceService;
	private static LicenseService licenseService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		userService = new UserServiceImpl();

		CommonsService commonsService = new CommonsServiceImpl();

		commonsService.start();

		userService.setCommonsService(commonsService);

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

		userService.setPersistenceService(persistenceService);

		DistributedCacheService distributeCacheService =
				new DistributedCacheServiceImpl();

		distributeCacheService.start();

		userService.setDcacheService(distributeCacheService);

		em.getTransaction().begin();

		userService.start();

		em.getTransaction().commit();

		modelService = new ModelServiceImpl();

		persistenceService.setModelService(modelService);

		modelService
				.setSchemaFactorySourceLocator(new SchemaFactorySourceLocator() {

					@Override
					public String getURL(String sourceName)
							throws ATCloudDataModelException {
						return System.getenv("ATCLOUD_SCHEMA_DIR") + "/" + sourceName;

					}

				});

		modelService.start();

		licenseService = new LicenseServiceImpl();

		licenseService.setModelService(modelService);

		licenseService.setPersistenceService(persistenceService);

		licenseService.start();

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
	public void testLicenseValidity() {
		/*
		 * Create the Organization
		 */

		Organization org = modelService.createOrganization();

		/*
		 * Add the Organization
		 */
		em.getTransaction().begin();

		persistenceService.add(org);

		em.getTransaction().commit();

		/*
		 * Create a license
		 */

		License license = modelService.createLicense();

		/*
		 * Add the License
		 */
		em.getTransaction().begin();

		persistenceService.add(license);

		em.getTransaction().commit();

		/*
		 * Add the license to the organization
		 */

		em.getTransaction().begin();

		persistenceService.assignLicenseToOrganization(license.getLicenseID(),
				org.getOrgID());

		em.getTransaction().commit();

		/*
		 * Get the licenses for the organization
		 */

		List<License> licenses = persistenceService.getLicenses(org);

		assertNotNull(licenses);

		assertTrue(licenses.size() == 1);

		Iterator<License> licenseIter = licenses.iterator();

		assertNotNull(licenseIter);

		/*
		 * This should contain the single license that we added to the organization
		 */
		while (licenseIter.hasNext()) {
			License nextLicense = licenseIter.next();
			assertNotNull(nextLicense);
			assertTrue(nextLicense.equals(license));
		}

		try {
			/*
			 * Should be false since it hasn't been activated yet
			 */
			assertFalse(licenseService.isValidLicense(license.getLicenseID()));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			/*
			 * Should be false since it's not a valid license key
			 */
			assertFalse(licenseService.isValidLicense("bogus-license-key"));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			em.getTransaction().begin();
			licenseService.activateLicense(license.getLicenseID());
			em.getTransaction().commit();
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			/*
			 * Should be true since it was just activated
			 */
			assertTrue(licenseService.isValidLicense(license.getLicenseID()));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			em.getTransaction().begin();
			licenseService.deactiveLicense(license.getLicenseID());
			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			/*
			 * Should be false since we just deactivated it
			 */
			assertFalse(licenseService.isLicenseActive(license.getLicenseID()));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			em.getTransaction().begin();
			licenseService.activateLicense(license.getLicenseID());
			em.getTransaction().commit();
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		try {
			/*
			 * Should be true since it was just reactivated
			 */
			assertTrue(licenseService.isLicenseActive(license.getLicenseID()));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}
	}
}
