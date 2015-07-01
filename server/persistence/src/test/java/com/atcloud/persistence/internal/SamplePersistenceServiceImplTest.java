/**
 * 
 */
package com.atcloud.persistence.internal;

import static org.junit.Assert.fail;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
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

import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class SamplePersistenceServiceImplTest {

	private static PersistenceService persistenceService =
			new PersistenceServiceImpl();
	private static EntityManager em;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		/**
		 * Setup the PersistenceServiceImpl
		 */

		/**
		 * Initialize the embedded DB
		 */
		try {
			NamingManager
					.setInitialContextFactoryBuilder(new MyInitialcontextFactoryBuilder());
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

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testNothing() {

	}

	/**
	 * 
	 * 
	 * @author <a href=mailto:support@atcloud.com>support</a>
	 * @version $Revision: $
	 */
	public static final class MyInitialcontextFactoryBuilder implements
			InitialContextFactoryBuilder {
		@Override
		public InitialContextFactory createInitialContextFactory(
				Hashtable<?, ?> environment) throws NamingException {
			return new MyInitialContextFactory();
		}
	}

}
