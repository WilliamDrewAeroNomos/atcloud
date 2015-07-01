/**
 * 
 */
package com.atcloud.persistence.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
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
import com.atcloud.commons.internal.CommonsServiceImpl;
import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.Group;
import com.atcloud.model.License;
import com.atcloud.model.ModelService;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.SchemaFactorySourceLocator;
import com.atcloud.model.USAddressType;
import com.atcloud.model.User;
import com.atcloud.model.internal.ModelServiceImpl;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class UserPersistenceServiceImplTest {

	private static PersistenceService persistenceService =
			new PersistenceServiceImpl();
	private static EntityManager em;

	private static final String groupDescription =
			"Description of the Test Group";
	private static final String groupName = "Test Group";

	private static ModelService modelService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		CommonsService commonsService = new CommonsServiceImpl();

		/**
		 * Setup the PersistenceServiceImpl
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
				.setSchemaFactorySourceLocator(new SchemaFactorySourceLocator() {

					public String getURL(String sourceName)
							throws ATCloudDataModelException {
						return System.getenv("ATCLOUD_SCHEMA_DIR") + "/" + sourceName;
					}
				});

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
		 * Remove all the users before each test starts
		 */
		em.getTransaction().begin();

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllLicenses();

		persistenceService.deleteAllOrganizations();

		em.getTransaction().commit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		/*
		 * Ensure that all users are removed at the end of each test
		 */

		em.getTransaction().begin();

		persistenceService.deleteAllUsers();

		persistenceService.deleteAllRoles();

		persistenceService.deleteAllGroups();

		persistenceService.deleteAllLicenses();

		persistenceService.deleteAllOrganizations();

		em.getTransaction().commit();

	}

	/**
	 * Test adding, finding and removing Users
	 */
	@Test
	public void testAddFindAndRemove() {

		Role defaultRole = createDefaultRole();

		/*
		 * Create a User
		 */

		String firstName = "Steve";
		String middleName = "S";
		String lastName = "Steverson";

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		User user = new User();

		user.setUserId(UUID.randomUUID().toString());

		user.setName(name);

		user.setActive(true);

		Calendar calendar = Calendar.getInstance();

		user.setDateActivated(calendar);
		user.setDateCreated(calendar);

		/*
		 * Test finding the User by name which should fail
		 */
		try {
			persistenceService.getUserByName(user.getName());
			fail("Should have thrown an exception "
					+ "since this user has not been added yet");
		} catch (Throwable t) {
		}

		/*
		 * Now add the new User
		 */

		em.getTransaction().begin();

		persistenceService.addUser(user, defaultRole);

		em.getTransaction().commit();

		try {
			persistenceService.getModelService().validateObject(defaultRole);
		} catch (ATCloudDataModelException e) {
			fail(e.getLocalizedMessage());
		}

		/*
		 * Ensure that the User was added by searching by ID
		 */
		User userFound = persistenceService.getUser(user.getHjid());

		assertNotNull(userFound);

		/*
		 * Find the User by name
		 */

		try {
			assertNotNull(persistenceService.getUserByName(user.getName()));
		} catch (Throwable t) {
			fail(t.getLocalizedMessage());
		}

		/*
		 * Remove the User
		 */
		em.getTransaction().begin();

		persistenceService.deleteUser(userFound);

		em.getTransaction().commit();

		/*
		 * Ensure that the User was removed by searching by ID
		 */
		User userRemoved = persistenceService.getUser(user.getHjid());

		assertNull(userRemoved);

		/*
		 * Test adding a role, a new user, assigning the user to the role and then
		 * removing the role itself. The role and roleassoc links should be removed
		 * and the user remains.
		 */

		/**
		 * Add the admin role
		 */
		Role adminRole = new Role();

		adminRole.setDate(calendar);
		adminRole.setName("Admin");
		adminRole.setDescription("Administration");

		em.getTransaction().begin();

		persistenceService.add(adminRole);

		em.getTransaction().commit();

		/*
		 * Create a User
		 */

		firstName = "Andy";
		middleName = "R";
		lastName = "McAdmin";

		name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user = new User();

		user.setUserId(UUID.randomUUID().toString());

		user.setName(name);

		user.setActive(true);

		calendar = Calendar.getInstance();

		user.setDateActivated(calendar);
		user.setDateCreated(calendar);

		em.getTransaction().begin();

		persistenceService.add(user);

		em.getTransaction().commit();

		/*
		 * Assign the user to the admin role
		 */

		em.getTransaction().begin();

		persistenceService.assignUserToRole(user, adminRole.getName());

		em.getTransaction().commit();

		/*
		 * Remove the admin role but maintain the user
		 */

		em.getTransaction().begin();

		persistenceService.deleteRole(adminRole.getName());

		em.getTransaction().commit();

		/**
		 * Ensure that the role is removed
		 */

		em.getTransaction().begin();

		assertFalse(persistenceService.roleExists(adminRole.getName()));

		em.getTransaction().commit();

		/*
		 * Ensure that the User remains by searching by ID
		 */

		em.getTransaction().begin();

		assertNotNull(persistenceService.getUser(user.getHjid()));

		em.getTransaction().commit();
	}

	/**
	 * 
	 */
	@Test
	public void testUserIndex() {
		/*
		 * Try and add a duplicate user
		 */
		Calendar calendar = Calendar.getInstance();

		Name name = new Name();

		String firstName = "bogusfirst";
		String middleName = "bogusmiddle";
		String lastName = "boguslast";

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		User user = new User();

		user.setUserId(UUID.randomUUID().toString());

		user.setName(name);

		user.setActive(true);

		user.setDateActivated(calendar);
		user.setDateCreated(calendar);

		em.getTransaction().begin();

		persistenceService.add(user);

		em.getTransaction().commit();

		Name dupName = new Name();

		dupName.setFirst(firstName);
		dupName.setMiddle(middleName);
		dupName.setLast(lastName);

		User dupUser = new User();

		dupUser.setUserId(UUID.randomUUID().toString());

		dupUser.setName(dupName);

		dupUser.setActive(true);

		dupUser.setDateActivated(calendar);
		dupUser.setDateCreated(calendar);

		em.getTransaction().begin();

		try {
			persistenceService.add(dupUser);
			fail("This should have been a unique index violation");
		} catch (EntityExistsException e) {
			em.getTransaction().rollback();
		}

	}

	/**
	 * Test Role assignment and removal
	 */
	@Test
	public void testRoleAssignmentAndRemoval() {

		/*
		 * Test adding a role, a new user, assigning the user to the role and then
		 * removing the role itself. The role and roleassoc links should be removed
		 * and the user remains.
		 */

		Calendar calendar = Calendar.getInstance();

		/*
		 * Add the Student role
		 */
		Role studentRole = new Role();

		studentRole.setDate(calendar);
		studentRole.setName("Student");
		studentRole.setDescription("Student role");

		em.getTransaction().begin();

		persistenceService.add(studentRole);

		em.getTransaction().commit();

		/*
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

		em.getTransaction().begin();

		persistenceService.add(user);

		em.getTransaction().commit();

		/*
		 * Add user to this role
		 */
		em.getTransaction().begin();

		persistenceService.assignUserToRole(user, studentRole.getName());

		em.getTransaction().commit();

		/*
		 * Get all roles for this user.
		 */

		List<Role> userRoles = persistenceService.getUserRoles(user);

		Iterator<Role> rolesIter = userRoles.iterator();

		/*
		 * It should contain the role that was just assigned
		 */
		while (rolesIter.hasNext()) {

			Role nextRole = rolesIter.next();
			assertNotNull(nextRole);
			/*
			 * This should be equal to the Student role that was assigned earlier
			 */
			assertTrue(nextRole.equals(studentRole));
		}

		/*
		 * Create a group
		 */

		Group group = new Group();

		group.setDate(calendar);
		group.setDescription(groupDescription);
		group.setName(groupName);

		/*
		 * Check that the group we're about to add doesn't already exist
		 */

		assertFalse(persistenceService.groupExists(groupName));

		/*
		 * Add the group
		 */
		em.getTransaction().begin();

		persistenceService.add(group);

		em.getTransaction().commit();

		/**
		 * Check that the group was really added
		 */

		assertTrue(persistenceService.groupExists(groupName));

		/*
		 * Actually get the group was added and check it's attributes
		 */

		Group groupRetrieved = persistenceService.getGroup(groupName);

		assertNotNull(groupRetrieved);

		assertTrue(groupRetrieved.equals(group));

		assertTrue(groupRetrieved.getName().equals(groupName));

		/*
		 * Add user to a group
		 */
		em.getTransaction().begin();

		persistenceService.assignUserToGroup(user, group.getName());

		em.getTransaction().commit();

		/*
		 * Get all the groups for this User
		 */

		List<Group> userGroups = persistenceService.getUserGroups(user);

		Iterator<Group> groupsIter = userGroups.iterator();

		/*
		 * It should contain the group that was just assigned
		 */
		while (groupsIter.hasNext()) {

			Group nextGroup = groupsIter.next();
			assertNotNull(nextGroup);

			/*
			 * This should be equal to the Group to which this user was assigned
			 * earlier
			 */
			assertTrue(nextGroup.equals(group));
		}

		/*
		 * Remove user from the Group
		 */

		em.getTransaction().begin();

		persistenceService.removeUserFromGroup(user, group.getName());

		em.getTransaction().commit();

		/*
		 * Check that the user no longer belongs to any groups
		 */
		userGroups = persistenceService.getUserGroups(user);

		assertTrue(userGroups.size() == 0);

		/*
		 * Remove user from the Student role
		 */

		em.getTransaction().begin();

		persistenceService.removeUserFromRole(user, studentRole.getName());

		em.getTransaction().commit();

		/*
		 * Get all roles for this user.
		 */

		userRoles = persistenceService.getUserRoles(user);

		/*
		 * It should not contain any roles
		 */

		assertTrue(userRoles.size() == 0);

	}

	/**
	 * Tests creating, assigning and validating the existence of licenses
	 */
	@Test
	public void testLicenseValidityAndOwnership() {

		/*
		 * Create the Organization
		 */

		Organization org = createOrganization();

		/*
		 * Add the Organization
		 */
		em.getTransaction().begin();

		persistenceService.add(org);

		em.getTransaction().commit();

		/*
		 * Create a license
		 */

		License license = createLicense();

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

		/*
		 * Create a new organization to whom we'll reassign the license
		 */

		Organization newOrg = createOrganization();

		newOrg.setCellPhoneNumber("301-440-8485");
		newOrg.setDateActivated(Calendar.getInstance());
		newOrg.setDateCreated(Calendar.getInstance());
		newOrg.setEmail("me@coyote.com");
		newOrg.setOfficePhoneNumber("301-731-6774");
		newOrg.setOrgID(UUID.randomUUID().toString());
		newOrg.setOrgName("Wily Coyote, Inc.");

		/*
		 * Add the Organization
		 */
		em.getTransaction().begin();

		persistenceService.add(newOrg);

		em.getTransaction().commit();

		/*
		 * Reassign the license to the new organization
		 */
		em.getTransaction().begin();

		persistenceService.reassignLicenseToOrganization(license.getLicenseID(),
				newOrg.getOrgID());

		em.getTransaction().commit();

		/*
		 * Get the licenses for the previous organization which should contain 0
		 * license count since we transferred them to another organization
		 */

		licenses = persistenceService.getLicenses(org);

		assertNotNull(licenses);

		assertTrue(licenses.size() == 0);

		/*
		 * Get the licenses for the new organization which should contain 1 license
		 * count since we transferred it to this organization
		 */

		licenses = persistenceService.getLicenses(newOrg);

		assertNotNull(licenses);

		assertTrue(licenses.size() == 1);
	}

	/**
	 * Tests creating and assigning multiple licenses
	 */
	@Test
	public void testAddingMultipleLicensesToAnOrganization() {

		/*
		 * Create the Organization
		 */
		Organization org = createOrganization();

		/*
		 * Add the Organization
		 */
		em.getTransaction().begin();

		persistenceService.add(org);

		em.getTransaction().commit();

		int licenseNumber = 100;

		for (int x = 0; x < licenseNumber; x++) {

			License license = createLicense();

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

		}

		/*
		 * Get all licenses for the organization and confirm count
		 */

		List<License> licenses = persistenceService.getLicenses(org);

		assertNotNull(licenses);

		assertTrue(licenses.size() == licenseNumber);

	}

	/**
	 * Tests adding multiple licenses and then deactivating one of them.
	 */
	@Test
	public void testAddingandDeactivatingALicenseToAnOrganization() {

		/*
		 * Create the Organization
		 */
		Organization org = createOrganization();

		/*
		 * Add the Organization
		 */
		em.getTransaction().begin();

		persistenceService.add(org);

		em.getTransaction().commit();

		int licenseNumber = 2;

		License license = null;

		for (int x = 0; x < licenseNumber; x++) {

			license = createLicense();

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

		}

		/*
		 * Get all licenses for the organization and confirm count
		 */

		List<License> licenses = persistenceService.getLicenses(org);

		assertNotNull(licenses);

		assertTrue(licenses.size() == licenseNumber);

		/*
		 * Get the first one that was created and deactivate it
		 */

		em.getTransaction().begin();

		persistenceService.deactiveLicense(license);

		em.getTransaction().commit();

		List<License> updatedLicenses = persistenceService.getLicenses();

		Iterator<License> licenseIter = updatedLicenses.iterator();

		assertNotNull(licenseIter);

		/*
		 * Retrieve the license and ensure that it was deactivated
		 */
		License retrievedDeactivedLicense =
				persistenceService.getLicense(license.getLicenseID());

		assertNotNull(retrievedDeactivedLicense);

		assertFalse(retrievedDeactivedLicense.isActive());

		/*
		 * Now activate the previously deactivated license
		 */
		em.getTransaction().begin();

		persistenceService.activeLicense(license);

		em.getTransaction().commit();

		/*
		 * Retrieve the license and ensure that it was deactivated
		 */
		License reactivedLicense =
				persistenceService.getLicense(license.getLicenseID());

		assertNotNull(reactivedLicense);

		assertTrue(reactivedLicense.isActive());

	}

	/**
	 * 
	 * @return
	 */
	private Organization createOrganization() {

		Organization org = new Organization();

		org.setActive(true);

		USAddressType usAddr = new USAddressType();
		usAddr.setCity("Greenbelt");
		usAddr.setCountry("USA");
		usAddr.setHouseNumber("123");
		usAddr.setState("MD");
		usAddr.setStreet1("Olivewood Ct.");
		usAddr.setUnit("4");
		usAddr.setZip(new BigDecimal(20770));

		org.setAddress(usAddr);
		org.setCellPhoneNumber("301-440-8485");
		org.setDateActivated(Calendar.getInstance());
		org.setDateCreated(Calendar.getInstance());
		org.setEmail("wdrew@csc.com");
		org.setOfficePhoneNumber("301-731-6774");
		org.setOrgID(UUID.randomUUID().toString());
		org.setOrgName("ACME, Inc.");
		return org;
	}

	/**
	 * 
	 */
	@Test
	public void testIndexes() {

		/*
		 * Add the admin role
		 */

		Calendar calendar = Calendar.getInstance();

		Role instructorRole = new Role();

		instructorRole.setDate(calendar);
		instructorRole.setName("Instructor");
		instructorRole.setDescription("Instructor Role");

		em.getTransaction().begin();

		persistenceService.add(instructorRole);

		em.getTransaction().commit();

		/*
		 * Create a duplicate role which throw exception
		 */
		Role duplicateInstructorRole = new Role();

		duplicateInstructorRole.setDate(Calendar.getInstance());
		duplicateInstructorRole.setName("Instructor");
		duplicateInstructorRole.setDescription("Instructor Role");

		em.getTransaction().begin();

		try {
			persistenceService.add(duplicateInstructorRole);
			fail("Should have thrown an exception for violating unique index on name_ column.");
			em.getTransaction().commit();
		} catch (EntityExistsException t) {

			em.getTransaction().rollback();
		}

	}

	/**
	 * @return
	 */
	private Role createDefaultRole() {
		/*
		 * Create the default role
		 */
		Role defaultRole;

		defaultRole = new Role();

		defaultRole.setDate(Calendar.getInstance());
		defaultRole.setDescription("Default role");
		defaultRole.setName("Default");

		em.getTransaction().begin();

		persistenceService.add(defaultRole);

		em.getTransaction().commit();
		return defaultRole;
	}

	/**
	 * 
	 * @return
	 */
	private License createLicense() {
		License license = new License();

		Calendar cal = Calendar.getInstance();

		license.setLicenseID(UUID.randomUUID().toString());
		license.setActive(true);
		license.setDateActivated(cal);
		license.setDateCreated(cal);
		license.setLastRenewalDate(cal);
		return license;
	}

}
