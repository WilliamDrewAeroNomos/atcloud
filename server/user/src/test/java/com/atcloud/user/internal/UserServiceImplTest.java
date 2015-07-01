/**
 * 
 */
package com.atcloud.user.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.Group;
import com.atcloud.model.ModelService;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.SchemaFactorySourceLocator;
import com.atcloud.model.User;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;
import com.atcloud.persistence.internal.PersistenceServiceImpl;
import com.atcloud.user.UserService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class UserServiceImplTest {

	private static EntityManager em;
	private static UserService userService;

	private static ModelService modelService;
	private static PersistenceService persistenceService;

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
				.setSchemaFactorySourceLocator(new MySchemaFactorySourceLocator());

		modelService.start();

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

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllOrganizations();

		em.getTransaction().commit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		em.getTransaction().begin();

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllOrganizations();

		em.getTransaction().commit();
	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#addUser(com.atcloud.model.User)}
	 * .
	 */
	@Test
	public void testAddUser() {

		/**
		 * Create a User
		 */
		User user = modelService.createUser();

		String firstName = "Steve";
		String middleName = "S";
		String lastName = "Steverson";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#getAllUsers()}.
	 */
	@Test
	public void testGetAllUsers() {

		Collection<User> userList = new ArrayList<User>();

		/**
		 * Create a User
		 */
		User user = modelService.createUser();

		String firstName = "William";
		String middleName = "John";
		String lastName = "Drew";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Add to the list
		 */
		userList.add(user);

		/*
		 * Create the second user
		 */
		user = modelService.createUser();

		firstName = "Amy";
		middleName = "Kay";
		lastName = "Drew";

		name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Add to the list
		 */
		userList.add(user);

		/*
		 * Get the users just added and check them against the internal list
		 */
		Collection<User> userListRetrieved = userService.getAllUsers();

		for (User nextUser : userList) {
			assertTrue(userListRetrieved.contains(nextUser));
		}

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#updateUser(com.atcloud.model.User)}
	 * .
	 */
	@Test
	public void testUpdateUser() {

		/**
		 * Create a User
		 */
		User user = modelService.createUser();

		String firstName = "Joe";
		String middleName = "E";
		String lastName = "Blow";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Update the user name
		 */
		String newFirstName = "Fred";
		name.setFirst(newFirstName);

		user.setName(name);

		em.getTransaction().begin();
		try {
			userService.updateUser(user);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}
		em.getTransaction().commit();

		/*
		 * Query and test that the User has been updated
		 */

		User queriedUpdatedUser = null;
		try {
			queriedUpdatedUser = userService.getUser(name);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(queriedUpdatedUser);
		assertTrue(queriedUpdatedUser.equals(user));
	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#deleteUser(com.atcloud.model.User)}
	 * .
	 */
	@Test
	public void testDeleteUser() {
		/**
		 * Create a User
		 */
		User user = modelService.createUser();

		String firstName = "Joe";
		String middleName = "E";
		String lastName = "Blow";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Delete the User just added
		 */

		em.getTransaction().begin();
		try {
			userService.deleteUser(user);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}
		em.getTransaction().commit();

		/*
		 * Query and test that the User has been deleted
		 */
		User deletedUser = null;
		try {
			deletedUser = userService.getUser(name);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/**
		 * Should be NULL since we deleted it
		 */
		assertNull(deletedUser);
	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#addRole(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddRole() {

		/**
		 * Add the admin role
		 */

		String roleName = "Admin";
		String roleDescription = "Administration";

		em.getTransaction().begin();

		try {
			userService.addRole(roleName, roleDescription);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Retrieve the Role just added
		 */
		em.getTransaction().begin();

		Role queriedRole = null;
		try {
			queriedRole = userService.getRole(roleName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		assertNotNull(queriedRole);

		assertTrue(queriedRole.getName().equals(roleName));

	}

	/**
	 * 
	 */
	@Test
	public void testAddOrganization() {

		/**
		 * Add the organization
		 */
		String orgName = "ACME SW Inc.";

		Organization organization = modelService.createOrganization();

		organization.setOrgName(orgName);

		try {
			em.getTransaction().begin();
			userService.addOrganization(organization);
			em.getTransaction().commit();
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Retrieve the Organization just added by name
		 */

		Organization queriedOrganization = null;
		try {
			queriedOrganization =
					userService.getOrganizationByName(organization.getOrgName());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(queriedOrganization);

		assertTrue(queriedOrganization.equals(organization));

		/*
		 * Retrieve the Organization just added by ID
		 */

		queriedOrganization = null;
		try {
			queriedOrganization =
					userService.getOrganizationByID(organization.getOrgID());
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertNotNull(queriedOrganization);

		assertTrue(queriedOrganization.equals(organization));

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#updateRole(com.atcloud.model.Role)}
	 * .
	 */
	@Test
	public void testUpdateRole() {

		/**
		 * Add the ExerciseAuthor role
		 */

		String roleName = "ExerciseAuthor";
		String roleDescription = "Author of exercises";

		em.getTransaction().begin();

		try {
			userService.addRole(roleName, roleDescription);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Retrieve the Role just added
		 */
		em.getTransaction().begin();

		Role queriedRole = null;

		try {
			queriedRole = userService.getRole(roleName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		assertNotNull(queriedRole);

		assertTrue(queriedRole.getName().equals(roleName));

		/**
		 * Change the name of the role
		 */
		String newRoleName = "ExerciseCreator";

		queriedRole.setName(newRoleName);

		em.getTransaction().begin();

		try {
			userService.updateRole(queriedRole);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Retrieve the Role just updated
		 */

		Role updatedRole = null;

		try {
			updatedRole = userService.getRole(newRoleName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Should be valid and equal to the queried Role that was modified with the
		 * new name.
		 */
		assertNotNull(updatedRole);

		assertTrue(updatedRole.equals(queriedRole));

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#assignUserToRole(com.atcloud.model.User, java.lang.String)}
	 * .
	 */
	@Test
	public void testAssignUserToRole() {

		Calendar calendar = Calendar.getInstance();

		/**
		 * Add the Student role
		 */
		Role studentRole = new Role();

		studentRole.setDate(calendar);
		studentRole.setName("Student");
		studentRole.setDescription("Student role");

		em.getTransaction().begin();

		persistenceService.add(studentRole);

		em.getTransaction().commit();

		/**
		 * Create a User
		 */

		String firstName = "Mike";
		String middleName = "A";
		String lastName = "Hunt";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		User user = new User();

		user.setUserId(UUID.randomUUID().toString());

		user.setName(name);

		user.setActive(true);

		calendar = Calendar.getInstance();

		user.setDateActivated(calendar);
		user.setDateCreated(calendar);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Assign this user to a role
		 */

		try {

			em.getTransaction().begin();

			userService.assignUserToRole(user, studentRole.getName());

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#removeUserFromRole(com.atcloud.model.User, java.lang.String)}
	 * .
	 */
	@Test
	public void testRemoveUserFromRole() {

		/**
		 * Add the Student role
		 */
		Role studentRole = modelService.createRole();

		studentRole.setName("Student");
		studentRole.setDescription("Student role");

		em.getTransaction().begin();

		persistenceService.add(studentRole);

		em.getTransaction().commit();

		/**
		 * Create a User
		 */

		User user = modelService.createUser();

		String firstName = "Mike";
		String middleName = "A";
		String lastName = "Hunt";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		/*
		 * Add the user
		 */

		try {

			em.getTransaction().begin();

			userService.addUser(user);

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Assign this user to a role
		 */

		try {

			em.getTransaction().begin();

			userService.assignUserToRole(user, studentRole.getName());

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		List<Role> rolesForUser = null;
		try {
			rolesForUser = userService.getUserRoles(user);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Check that the role was added to the user
		 */
		for (Role nextRole : rolesForUser) {
			assertTrue(nextRole.equals(studentRole));
		}

		/*
		 * Remove the user from the StudentRole
		 */
		try {

			em.getTransaction().begin();

			userService.removeUserFromRole(user, studentRole.getName());

			em.getTransaction().commit();

		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		/**
		 * Check that the user is no longer assigned to the StudentRole
		 */

		rolesForUser = null;
		try {
			rolesForUser = userService.getUserRoles(user);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		assertTrue(rolesForUser.size() == 0);

	}

	/**
	 * Test method for
	 * {@link com.atcloud.user.internal.UserServiceImpl#removeRole(java.lang.String)}
	 * .
	 */
	@Test
	public void testRemoveRole() {

		/*
		 * Add the admin role
		 */
		String roleName = "Admin";
		String roleDescription = "Administration";

		em.getTransaction().begin();

		try {
			userService.addRole(roleName, roleDescription);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Retrieve the Role just added
		 */
		em.getTransaction().begin();

		Role queriedRole = null;
		try {
			queriedRole = userService.getRole(roleName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		assertNotNull(queriedRole);

		assertTrue(queriedRole.getName().equals(roleName));

		/*
		 * Remove the Role just added
		 */
		em.getTransaction().begin();

		try {
			userService.removeRole(roleName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		/*
		 * Verify that the Role was deleted
		 */

		try {
			assertNull(userService.getRole(roleName));
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 */
	@Test
	public void testGroupCRUD() {

		String name = "Test Group";
		String description = "Description for the Test Group";
		String activeFEMName = "Test Federation Execution Model";

		em.getTransaction().begin();

		try {
			userService.addGroup(name, description, activeFEMName);
		} catch (ATCloudException e) {
			fail(e.getLocalizedMessage());
		}

		em.getTransaction().commit();

		List<Group> groups = userService.getAllGroups();

		for (Group nextGroup : groups) {
			assertTrue(nextGroup.getName().equals(name));
		}

		/*
		 * Retrieve group by name
		 */

		Group groupSelectedByName = userService.getGroupByName(name);

		assertNotNull(groupSelectedByName);

		assertTrue(groupSelectedByName.getName().equals(name));
		assertTrue(groupSelectedByName.getDescription().equals(description));
		assertTrue(groupSelectedByName.getActiveFEMName().equals(activeFEMName));
	}

	/**
	 * Custom schema locator for this unit test
	 * 
	 */
	private static class MySchemaFactorySourceLocator implements
			SchemaFactorySourceLocator {

		private String schemaDir = "";

		public MySchemaFactorySourceLocator() {
			schemaDir = System.getenv("ATCLOUD_SCHEMA_DIR");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.atcloud.model.SchemaFactorySourceLocator#getURL(java.lang.String)
		 */
		@Override
		public String getURL(String sourceName) throws ATCloudDataModelException {
			return schemaDir + "/" + sourceName;
		}

	}

}
