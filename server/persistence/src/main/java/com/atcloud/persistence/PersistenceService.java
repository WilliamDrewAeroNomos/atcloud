/**
 * 
 */
package com.atcloud.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.model.AirFlightPosition;
import com.atcloud.model.FEM;
import com.atcloud.model.Federate;
import com.atcloud.model.Group;
import com.atcloud.model.License;
import com.atcloud.model.LicenseOrgAssoc;
import com.atcloud.model.ModelService;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.Scenario;
import com.atcloud.model.Simulation;
import com.atcloud.model.SimulationMetrics;
import com.atcloud.model.User;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public interface PersistenceService {

	/**
	 * 
	 * @param obj
	 */
	Object add(Object obj);

	/**
	 * 
	 * @param query
	 * @return
	 */
	Boolean queryResultEmpty(Query query);

/**
	 * Find a {@link User) using the unique ID attribute.
	 * 
	 * @param id Unique user id
	 * @return User or null if user ID does not exist
	 */
	User getUser(final Long id);

	/**
	 * Deletes all the (@link User)s from the persistent store.
	 * 
	 */
	void deleteAllUsers();

	/**
	 * Deletes all the {@link Organization} objects from the peristent store.
	 * 
	 */
	void deleteAllOrganizations();

	/**
	 * Deletes all the {@link License} objects from the peristent store.
	 */
	void deleteAllLicenses();

	/**
	 * Gets a list of all the (@link User}s from the persistent store and places
	 * them in the dcache.
	 * 
	 * @return List of all the current {@link User}s in the system.
	 */
	List<User> getAllUsers();

	/**
	 * Returns the {@link EntityManager} reference that's supplied via
	 * {@link #setEntityManager(EntityManager)}.
	 * 
	 * @return
	 */
	EntityManager getEntityManager();

	/**
	 * Performs a lookup in the EntityManagerFactory OSGi service that is suitable
	 * for the persistence unit "com.atcloud.model" and calls this method passing
	 * in an EnityManager. The following snippet is found in
	 * persistence-blueprint.xml under src/main/resources/OSGI-INF/blueprint.
	 * 
	 * <bean id="persistenceService"
	 * class="com.atcloud.persistence.internal.PersistenceServiceImpl">
	 * <jpa:context unitname="com.atcloud.model" property="entityManager" />
	 * <tx:transaction method="*" value="Required" /> </bean>
	 * 
	 */
	void setEntityManager(final EntityManager em);

	/**
	 * Removes the {@link User} from the persistent store and dcache. This also
	 * removes any links to any {@link Role}s for this User.
	 * 
	 * @param User
	 *          to more remove from the system.
	 */
	void deleteUser(final User user);

	/**
	 * Delete the {@link License} and the {@link LicenseOrgAssoc} entries.
	 * 
	 * @param license
	 * @see LicenseOrgAssoc
	 */
	void deleteLicense(final License license);

	/**
	 * 
	 * @param name
	 * @return
	 * @throws ATCloudException
	 */
	User getUserByName(final Name name);

	/**
	 * @param obj
	 */
	void addUser(final User user, final Role role);

	/**
	 * 
	 * @param entityObject
	 */
	void remove(final Object entityObject);

	/**
	 * 
	 * @param name
	 * @return
	 */
	Role getRole(final String name);

	/**
	 * 
	 * @param licenseKey
	 * @return
	 */
	License getLicense(final String licenseKey);

	/**
	 * 
	 * @param name
	 * @return
	 */
	boolean roleExists(final String name);

	/**
	 * 
	 * @param user
	 * @param roleName
	 */
	void assignUserToRole(final User user, final String roleName);

	/**
	 * 
	 * @param roleName
	 */
	void deleteRole(final String roleName);

	/**
	 * Removes all rows from the Role table
	 */
	public void deleteAllRoles();

	/**
	 * 
	 * @param role
	 * @throws ATCloudException
	 */
	void updateRole(final Role role) throws ATCloudException;

	/**
	 * @return the modelService
	 */
	ModelService getModelService();

	/**
	 * @param modelService
	 *          the modelService to set
	 */
	void setModelService(final ModelService modelService);

	/**
	 * @param user
	 * @param roleName
	 */
	void removeUserFromRole(final User user, final String roleName);

	/**
	 * 
	 * @param user
	 * @return
	 */
	List<Role> getUserRoles(final User user);

	/**
	 * 
	 * @param user
	 * @throws ATCloudException
	 */
	void updateUser(final User user) throws ATCloudException;

	/**
	 * 
	 * @param name
	 */
	Group getGroup(final String name);

	/**
	 * 
	 * @param name
	 * @return
	 */
	boolean groupExists(final String name);

	/**
	 * 
	 * @param user
	 * @param groupName
	 */
	void assignUserToGroup(final User user, final String groupName);

	/**
	 * 
	 * @param user
	 * @return
	 */
	List<Group> getUserGroups(final User user);

	/**
	 * Removes all rows from the Group table
	 */
	void deleteAllGroups();

	/**
	 * 
	 * @param user
	 * @param groupName
	 */
	void removeUserFromGroup(final User user, final String groupName);

	/**
	 * Creates a {@link License} object
	 * 
	 * @return
	 */
	License createLicense();

	/**
	 * Returns a list of {@License}s for an {@link Organization}
	 * 
	 * @param organization
	 * @return
	 */
	List<License> getLicenses(final Organization organization);

	/**
	 * 
	 * @param licenseID
	 * @param organizationID
	 */
	void assignLicenseToOrganization(final String licenseID,
			final String organizationID);

	/**
	 * Reassign a current license from one organization to another organization.
	 * 
	 * @param licenseID
	 * @param newOrganizationID
	 */
	void reassignLicenseToOrganization(final String licenseID,
			final String newOrganizationID);

	/**
	 * Deactivates the {@link License} license.
	 * 
	 * @param licenseID
	 */
	void deactiveLicense(final License license);

	/**
	 * Retrieves all the {@link License}s in the persistent store.
	 * 
	 * @return
	 */
	List<License> getLicenses();

	/**
	 * Activates the {@link License} specified by the license parm.
	 * 
	 * @param license
	 */
	void activeLicense(final License license);

	/**
	 * 
	 * @param license
	 */
	void updateLicense(final License license);

	/**
	 * 
	 * @param organization
	 */
	void updateOrganization(final Organization organization);

	/**
	 * 
	 * @param organizationName
	 * @return
	 */
	boolean organizationExists(final String organizationName);

	/**
	 * 
	 * @return
	 */
	List<Group> getAllGroups();

	/**
	 * @param group
	 */
	void updateGroup(Group group);

	/**
	 * @param orgName
	 * @return
	 */
	Organization getOrganizationByName(String orgName);

	/**
	 * @param orgID
	 * @return
	 */
	Organization getOrganizationByID(String orgID);

	/**
	 * Update the matching {@link FEM} in the persistent store. Note that the FEM
	 * name can not be updated or changed.
	 * 
	 * @param fem
	 * @throws ATCloudException
	 */
	void update(final FEM fem) throws ATCloudException;

	/**
	 * Deletes all the (@link FEM)s from the persistent store.
	 * 
	 */
	void deleteAllFEMs();

	/**
	 * Gets a list of all the (@link FEM}s from the persistent store.
	 * 
	 * @return List of all the {@link FEM}s from the persistent store.
	 */
	List<FEM> getAllFEMs();

	/**
	 * Removes the {@link FEM} from the persistent store.
	 * 
	 * @param fem
	 *          to more remove from the system.
	 */
	void deleteFEM(FEM fem);

/**
	 * 
	 * Find a {@link FEM) by name
	 * @param name unique name for a FEM
	 */
	FEM getFEM(final String name);

/**
	 * 
	 * Find a {@link Federate) by name
	 * @param name unique name for a Federate
	 */
	Federate getFederate(final String name);

	/**
	 * Update a {@link Federate} in the persistent store.
	 * 
	 * @param federate
	 * @throws ATCloudException
	 */
	void update(final Federate federate) throws ATCloudException;

	/**
	 * Delete the {@link Federate} by name from the persistent store.
	 */
	void deleteFederate(final String name);

	/**
	 * Deletes all the (@link Federate)s from the persistent store.
	 */
	void deleteAllFederates();

	/**
	 * Add the {@link Federate} federate to the {@link FEM} fem
	 * 
	 * @param federate
	 * @param fem
	 */
	void addFederateToFEM(final Federate federate, final FEM fem);

	/**
	 * Add a federate to a FEM with federate name and FEM name
	 * 
	 * @param federateName
	 * @param femName
	 */
	void addFederateToFEM(final String federateName, final String femName);

	/**
	 * Remove the {@link Federate} federate from the {@link FEM} fem using the
	 * federate name and FEM name.
	 * 
	 * @param federateName
	 * @param femName
	 */
	void removeFederateFromFEM(final String federateName, final String femName);

	/**
	 * Return the list of {@link Federate}s that are assigned to a {@link FEM}.
	 * 
	 * @param femName
	 * @return
	 * @throws ATCloudException
	 */
	List<Federate> getFederatesInFEM(final String femName)
			throws ATCloudException;

	/**
	 * 
	 * @param federateName
	 * @param groupName
	 */
	void addFederateToGroup(final String federateName, final String groupName);

	/**
	 * 
	 * @param femName
	 * @param groupName
	 */
	void addFEMToGroup(final String femName, final String groupName);

	/**
	 * 
	 * Removes a {@link Federate} from a {@link Group}.
	 * 
	 * @param federateName
	 *          The name of the federate to remove.
	 * @param groupName
	 *          The name of the group from which the federate will be removed.
	 * 
	 */
			void
			removeFederateFromGroup(final String federateName, final String groupName);

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	FEM getActiveFEMForGroup(final String groupName);

	/**
	 * 
	 * @param federateName
	 * @return
	 */
	Group getFederateGroup(final String federateName);

	/**
	 * 
	 * @param federateName
	 * @return
	 */
	FEM getFederateActiveFEM(final String federateName);

	/**
	 * Get a list of all {@link Federate}s in the system.
	 * 
	 * @return
	 */
	List<Federate> getFederates();

	/**
	 * @param name
	 * @throws ATCloudException
	 */
	Federate addFederate(final String name, final String description)
			throws ATCloudException;

	/**
	 * 
	 * @param groupName
	 * @param femName
	 * @throws ATCloudException
	 */
	void setGroupActiveFEM(final String groupName, final String femName)
			throws ATCloudException;

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	List<Federate> getFederatesInGroup(final String groupName);

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	List<FEM> getFEMsInGroup(final String groupName);

	/**
	 * 
	 * @param femName
	 * @param groupName
	 * @throws ATCloudException
	 */
	void removeFEMFromGroup(String femName, String groupName)
			throws ATCloudException;

	/**
	 * 
	 * @param group
	 * @param femName
	 * @throws ATCloudException
	 */
	void setActiveFEMForGroup(final Group group, final String femName)
			throws ATCloudException;

	/**
	 * 
	 * @param groupName
	 * @return
	 * @throws ATCloudException
	 */
	boolean removeActiveFEMForGroup(final String groupName)
			throws ATCloudException;

	/**
	 * @param id
	 * @return
	 */
	FEM getFEMbyID(String id);

/**
	 * Find a {@link Scenario) by name
	 * @param name unique name for the Scenario
	 */
	Scenario getScenario(final String name);

	/**
	 * 
	 * @param simulation
	 * @param simulationMetrics
	 */
	void recordSimulationExecutionMetrics(final Simulation simulation,
			final SimulationMetrics simulationMetrics);

	/**
	 * 
	 * @param simulationID
	 * @return
	 */
	Simulation getSimulation(final String simulationID);

	/**
	 * 
	 */
	void deleteAllSimulations();

	/**
	 * 
	 * @param simulation
	 * @param airFlightPosition
	 * @throws ATCloudException
	 */
	void recordSimulationData(final Simulation simulation,
			final AirFlightPosition airFlightPosition) throws ATCloudException;

	/**
	 * 
	 * @param simulation
	 * @throws ATCloudException
	 */
	void addSimulation(final Simulation simulation) throws ATCloudException;

	/**
	 * 
	 */
	void stop();

	/**
	 *
	 */
	void start();
}