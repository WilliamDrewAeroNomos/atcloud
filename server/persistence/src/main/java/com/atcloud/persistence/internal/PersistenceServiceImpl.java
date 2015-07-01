package com.atcloud.persistence.internal;

import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.model.ATCloudDataModelException;
import com.atcloud.model.AirFlightPosition;
import com.atcloud.model.FEM;
import com.atcloud.model.Federate;
import com.atcloud.model.FederateFEMAssoc;
import com.atcloud.model.FederateGroupAssoc;
import com.atcloud.model.Group;
import com.atcloud.model.GroupFEMAssoc;
import com.atcloud.model.License;
import com.atcloud.model.LicenseOrgAssoc;
import com.atcloud.model.ModelService;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.Scenario;
import com.atcloud.model.Simulation;
import com.atcloud.model.SimulationMetrics;
import com.atcloud.model.SimulationSimMetricsAssoc;
import com.atcloud.model.SimulationSimulationDataAssoc;
import com.atcloud.model.User;
import com.atcloud.model.UserGroupAssoc;
import com.atcloud.model.UserRoleAssoc;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class PersistenceServiceImpl implements PersistenceService {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(PersistenceServiceImpl.class.getName());

	public ModelService modelService;

	public EntityManager entityManager;

	/**
	 * 
	 */
	@Override
	public void start() {

		LOG.debug("Starting user persistence service...");

		LOG.info("Started user persistence service.");
	}

	/**
	 * 
	 */
	@Override
	public void stop() {

		LOG.debug("Stopping user persistence service...");

		LOG.info("Stopped user persistence service...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#setEntityManager(javax.
	 * persistence.EntityManager)
	 */
	@Override
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.PersistenceService#getEntityManager()
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.PersistenceService#add(java.lang.Object)
	 */
	public Object add(Object obj) {

		LOG.debug("Adding [" + obj + "]...");

		entityManager.persist(obj);
		entityManager.flush();

		LOG.debug("[" + obj + "] added.");

		return obj;

	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	public Boolean queryResultEmpty(Query query) {
		return query.getResultList().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#getAllUsers()
	 */
	@Override
	public List<User> getAllUsers() {
		return getEntityManager().createQuery("select u from User u", User.class)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#addUser(com.atcloud.model
	 * .data .User)
	 */
	@Override
	public void addUser(User user, Role role) {

		LOG.debug("Adding user [" + user + "] to the role [" + role + "]");

		add(user);

		UserRoleAssoc userRoleAssoc = new UserRoleAssoc();
		userRoleAssoc.setName(role.getName());
		userRoleAssoc.setUserId(user.getUserId());

		add(userRoleAssoc);

		LOG.debug("User [" + user + "] added.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#updateUser(com.atcloud
	 * .model .data.User)
	 */
	public void updateUser(final User user) throws ATCloudException {

		LOG.debug("Updating user [" + user + "]...");

		if (user == null) {
			throw new IllegalArgumentException(
					"User parameter was null when attempting to update a user.");
		}

		User userToUpdate = getEntityManager().find(User.class, user.getHjid());

		if (userToUpdate == null) {
			throw new ATCloudException(
					"Unable to find user in persistence store to update.");
		}

		Query q =
				entityManager.createQuery("update User u set u.active =: active "
						+ ", u.dateActivated =: dateActivated"
						+ ", u.dateCreated =: dateCreated"
						+ ", u.dateDeactivated =: dateDeactivated"
						+ " where u.userId =: userId");

		q.setParameter("active", user.isActive());
		q.setParameter("dateActivated", user.getDateActivated());
		q.setParameter("dateCreated", user.getDateCreated());
		q.setParameter("dateDeactivated", user.getDateDeactivated());

		q.setParameter("userId", user.getUserId());

		getEntityManager().flush();

		LOG.info("Updated user [" + user + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#deleteUser(com.atcloud
	 * .model .data.User)
	 */
	@Override
	public void deleteUser(User user) {

		LOG.debug("Deleting user [" + user + "] and associated roles.");

		if (user == null) {
			throw new IllegalArgumentException(
					"User parameter was null when attempting to remove a user.");
		}

		LOG.debug("Removing user [" + user + "]...");

		getEntityManager().remove(user);

		LOG.info("User [" + user + "] removed from the system.");

		Query query =
				getEntityManager().createQuery(
						"delete from UserRoleAssoc u where" + " u.userId = :userid");

		query.setParameter("userid", user.getUserId());

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed all roles for user [" + user + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#deleteAllUsers()
	 */
	public void deleteAllUsers() {

		getEntityManager().createQuery("delete from User").executeUpdate();

		LOG.info("All users removed");

		getEntityManager().createQuery("delete from UserRoleAssoc").executeUpdate();

		LOG.info("All UserRoleAssoc entries removed");

		getEntityManager().createQuery("delete from UserGroupAssoc")
				.executeUpdate();

		LOG.info("All UserGroupAssoc entries removed");

		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getRole(java.lang.String)
	 */
	public Role getRole(final String name) {

		Role role = null;

		if ((name != null) && (!name.isEmpty())) {

			Query query = createRoleSelectByNameQuery(name);

			if (!queryResultEmpty(query)) {
				role = (Role) query.getSingleResult();
			}

		}

		return role;
	}

	/**
	 * 
	 * @param licenseID
	 * @return
	 */
	public License getLicense(final String licenseKey) {

		License license = null;

		if ((licenseKey != null) && (!licenseKey.isEmpty())) {

			Query query = createLicenseSelectByNameQuery(licenseKey);

			if (!queryResultEmpty(query)) {
				license = (License) query.getSingleResult();
			}

		}

		return license;
	}

	/**
	 * 
	 * @param licenseID
	 * @return
	 */
	private Query createLicenseSelectByNameQuery(final String licenseID) {

		Query query = null;
		try {
			query =
					getEntityManager().createQuery(
							"select l from License l " + "where l.licenseID =:licenseID");

			query.setParameter("licenseID", licenseID);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getOrganizationByName(java
	 * .lang.String)
	 */
	public Organization getOrganizationByName(final String orgName) {

		Organization organization = null;

		if ((orgName != null) && (!orgName.isEmpty())) {

			Query query = createOrganizationSelectByNameQuery(orgName);

			if (!queryResultEmpty(query)) {
				organization = (Organization) query.getSingleResult();
			}

		}

		return organization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getOrganizationByID(java
	 * .lang.String)
	 */
	@Override
	public Organization getOrganizationByID(String orgID) {

		Organization organization = null;

		if ((orgID != null) && (!orgID.isEmpty())) {

			Query query = createOrganizationSelectByIDQuery(orgID);

			if (!queryResultEmpty(query)) {
				organization = (Organization) query.getSingleResult();
			}

		}

		return organization;
	}

	/**
	 * 
	 * @param organizationID
	 * @return
	 */
	private Query createOrganizationSelectByIDQuery(final String organizationID) {

		Query query = null;

		try {
			query =
					getEntityManager().createQuery(
							"select o from Organization o "
									+ "where o.orgID =:organizationID");

			query.setParameter("organizationID", organizationID);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#roleExist(java.lang.String )
	 */
	public boolean roleExists(final String name) {

		boolean roleExists = false;

		if ((name != null) && (!name.isEmpty())) {

			Query query = createRoleSelectByNameQuery(name);

			roleExists = (!queryResultEmpty(query));
		}

		return roleExists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getUserRoles(com.atcloud
	 * .model.data.User)
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getUserRoles(final User user) {

		List<Role> roles = null;

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select r from Role r, UserRoleAssoc ura, User u "
										+ "where r.name = ura.name and ura.userId = u.userId and u.userId =:userid");
		query.setParameter("userid", user.getUserId());

		roles = (List<Role>) query.getResultList();

		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#assignRoleToUser(com.atcloud
	 * .model.data.Name, java.lang.String)
	 */
	public void assignUserToRole(final User user, final String roleName) {

		LOG.debug("Assigning User [" + user + "] to the [" + roleName + "] role.");

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new IllegalArgumentException(
					"Role name argument was null or empty.");
		}

		if (getUserByName(user.getName()) == null) {
			throw new IllegalArgumentException("Attempted to assign the user ["
					+ user + "] to a role but the user does not exist.");
		}

		if (!roleExists(roleName)) {
			throw new IllegalArgumentException("Attempted to assign the role ["
					+ roleName + "] to the user [" + user
					+ "] but the role does not exist.");
		}

		/*
		 * Create the UserRoleAssoc
		 */
		UserRoleAssoc userRoleAssoc = new UserRoleAssoc();

		userRoleAssoc.setName(roleName);
		userRoleAssoc.setUserId(user.getUserId());

		add(userRoleAssoc);

		LOG.info("User [" + user + "] assigned to [" + roleName + "] role.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#assignUserToGroup(com.
	 * atcloud.model.User, java.lang.String)
	 */
	public void assignUserToGroup(final User user, final String groupName) {

		LOG.debug("Assigning User [" + user + "] to the [" + groupName + "] group.");

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		if (getUserByName(user.getName()) == null) {
			throw new IllegalArgumentException("Attempted to assign the user ["
					+ user + "] to a group but the user does not exist.");
		}

		if (!groupExists(groupName)) {
			throw new IllegalArgumentException("Attempted to assign the user ["
					+ user + "] to the group [" + groupName
					+ "] but the group does not exist.");
		}

		UserGroupAssoc userGroupAssoc = new UserGroupAssoc();

		userGroupAssoc.setName(groupName);
		userGroupAssoc.setUserId(user.getUserId());

		getEntityManager().persist(userGroupAssoc);

		getEntityManager().flush();

		LOG.info("User [" + user + "] assigned to [" + groupName + "] group.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#groupExist(java.lang.String
	 * )
	 */
	public boolean groupExists(final String name) {

		boolean groupExists = false;

		if ((name != null) && (!name.isEmpty())) {

			Query query = createGroupSelectByNameQuery(name);

			groupExists = (!queryResultEmpty(query));
		}

		return groupExists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#removeRoleFromUser(com
	 * .atcloud .model.data.User, java.lang.String)
	 */
	@Override
	public void removeUserFromRole(User user, String roleName) {

		LOG.debug("Removing User [" + user + "] from the [" + roleName + "] role.");

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new IllegalArgumentException(
					"Role name argument was null or empty.");
		}

		if (getUserByName(user.getName()) == null) {
			throw new IllegalArgumentException("Attempted to remove the user ["
					+ user + "] from a role but the user does not exist.");
		}

		if (!roleExists(roleName)) {
			throw new IllegalArgumentException("Attempted to remove the role ["
					+ roleName + "] from the user [" + user
					+ "] but the role does not exist.");
		}

		Query query =
				getEntityManager().createQuery(
						"delete from UserRoleAssoc ura "
								+ "where ura.name =:rolename and ura.userId =:userid");

		query.setParameter("rolename", roleName);
		query.setParameter("userid", user.getUserId());

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed user [" + user + "] from role [" + roleName + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#deleteAllRoles()
	 */
	public void deleteAllRoles() {

		getEntityManager().createQuery("delete from Role").executeUpdate();

		LOG.info("All Roles deleted...");

		getEntityManager().createQuery("delete from UserRoleAssoc").executeUpdate();

		LOG.info("All UserRoleAssoc entries deleted...");

		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#deleteAllGroups()
	 */
	public void deleteAllGroups() {

		getEntityManager().createQuery("delete from Group").executeUpdate();

		LOG.info("All Groups deleted.");

		getEntityManager().createQuery("delete from UserGroupAssoc")
				.executeUpdate();

		LOG.info("All UserRoleAssoc entries deleted.");

		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#organizationExists(java.
	 * lang.String)
	 */
	@Override
	public boolean organizationExists(final String organizationName) {

		boolean organizationExist = false;

		if ((organizationName != null) && (!organizationName.isEmpty())) {

			Query query = createOrganizationSelectByNameQuery(organizationName);

			organizationExist = (!queryResultEmpty(query));
		}

		return organizationExist;

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private Query createOrganizationSelectByNameQuery(final String name) {
		Query query = null;
		try {
			query =
					getEntityManager().createQuery(
							"select o from Organization o " + "where o.orgName =:name");

			query.setParameter("name", name);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private Query createRoleSelectByNameQuery(final String name) {
		Query query = null;
		try {
			query =
					getEntityManager().createQuery(
							"select r from Role r " + "where r.name =:name");

			query.setParameter("name", name);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private Query createGroupSelectByNameQuery(final String name) {
		Query query = null;
		try {
			query =
					getEntityManager().createQuery(
							"select g from Group g " + "where g.name =:name");

			query.setParameter("name", name);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#deleteRole(java.lang.String)
	 */
	public void deleteRole(final String roleName) {

		LOG.debug("Deleting role [" + roleName + "]...");

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new IllegalArgumentException(
					"Name parameter was null or empty when deleting a role");
		}

		Query query =
				getEntityManager().createQuery(
						"delete from Role r where" + " r.name = :roleName");

		query.setParameter("roleName", roleName);

		query.executeUpdate();

		LOG.info("Role [" + roleName + "] removed.");

		LOG.debug("Deleting all UserRoleAssoc entries for [" + roleName + "] ...");

		query =
				getEntityManager().createQuery(
						"delete from UserRoleAssoc u where" + " u.name = :name");

		query.setParameter("name", roleName);

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("All UserRoleAssoc entries deleted for [" + roleName + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#updateRole(com.atcloud
	 * .model .data.Role)
	 */
	public void updateRole(final Role role) throws ATCloudException {

		LOG.debug("Updating role [" + role + "]...");

		if (role == null) {
			throw new IllegalArgumentException(
					"Role parameter was null when attempting to update a role.");
		}

		try {
			modelService.validateObject(role);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException(e);
		}

		Role roleToUpdate = getEntityManager().find(Role.class, role.getHjid());

		if (roleToUpdate == null) {
			throw new ATCloudException(
					"Unable to find role in persistence store to update.");
		}

		roleToUpdate.setDate(role.getDate());
		roleToUpdate.setDescription(role.getDescription());
		roleToUpdate.setName(role.getName());

		Query q =
				entityManager.createQuery("update Role r set r.date =: date, "
						+ "r.description =: description where r.name =: name");

		q.setParameter("date", role.getDate());
		q.setParameter("description", role.getDescription());
		q.setParameter("name", role.getName());

		q.executeUpdate();

		getEntityManager().flush();

		LOG.info("Updated role [" + role + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#findUser(java.lang.Long)
	 */
	public User getUser(final Long id) {
		return getEntityManager().find(User.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getUserByName(com.atcloud
	 * .model.Name)
	 */
	public User getUserByName(final Name name) {

		User user = null;

		if (name == null) {
			throw new IllegalArgumentException("Name parameter null.");
		}

		Query query =
				getEntityManager().createQuery(
						"select u from User u "
								+ "where u.name.first=:namefirst and u.name.last =:namelast");
		query.setParameter("namefirst", name.getFirst());
		query.setParameter("namelast", name.getLast());

		if (!queryResultEmpty(query)) {

			user = (User) query.getSingleResult();
		}

		return user;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#remove(java.lang.Object)
	 */
	public void remove(final Object entityObject) {
		if (entityObject != null) {

			LOG.debug("Deleting [" + entityObject + "]...");

			getEntityManager().remove(entityObject);
			getEntityManager().flush();

			LOG.debug("Deleted [" + entityObject + "].");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getGroup(java.lang.String)
	 */
	@Override
	public Group getGroup(String name) {

		if (name == null) {
			throw new IllegalArgumentException("Group name argument was null.");
		}

		Group group = null;

		if ((name != null) && (!name.isEmpty())) {

			Query query = createGroupSelectByNameQuery(name);

			if (!queryResultEmpty(query)) {
				group = (Group) query.getSingleResult();
			}

		}

		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getUserGroups(com.atcloud
	 * .model.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Group> getUserGroups(User user) {

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select g from Group g, UserGroupAssoc uga, User u "
										+ "where g.name = uga.name and uga.userId = u.userId and u.userId =:userid");
		query.setParameter("userid", user.getUserId());

		return (List<Group>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#removeUserFromGroup(com
	 * .atcloud.model.User, java.lang.String)
	 */
	@Override
	public void removeUserFromGroup(User user, String groupName) {

		LOG.debug("Removing user [" + user + "] from group [" + groupName + "]...");

		if (user == null) {
			throw new IllegalArgumentException("User argument was null.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		if (getUserByName(user.getName()) == null) {
			throw new IllegalArgumentException("Attempted to remove the user ["
					+ user + "] from a group but the user does not exist.");
		}

		if (!groupExists(groupName)) {
			throw new IllegalArgumentException("Attempted to remove the user ["
					+ user + "] from the group [" + groupName
					+ "] but the group does not exist.");
		}

		Query query =
				getEntityManager().createQuery(
						"delete from UserGroupAssoc uga "
								+ "where uga.name =:groupname and uga.userId =:userid");

		query.setParameter("groupname", groupName);
		query.setParameter("userid", user.getUserId());

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed user [" + user + "] from group [" + groupName + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#deleteAllOrganizations()
	 */
	@Override
	public void deleteAllOrganizations() {

		getEntityManager().createQuery("delete from Organization").executeUpdate();

		LOG.info("All Organizations deleted.");

		getEntityManager().createQuery("delete from LicenseOrgAssoc")
				.executeUpdate();

		LOG.info("All LicenseOrgAssoc entries removed.");

		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#deleteAllLicenses()
	 */
	@Override
	public void deleteAllLicenses() {

		getEntityManager().createQuery("delete from License").executeUpdate();

		LOG.info("All Licenses deleted.");

		getEntityManager().createQuery("delete from LicenseOrgAssoc")
				.executeUpdate();

		LOG.info("All LicenseOrgAssocs deleted.");

		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#assignLicenseToOrganization
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void assignLicenseToOrganization(String licenseID,
			String organizationID) {

		LOG.debug("Assigning license ID [" + licenseID
				+ "] to the organization ID [" + organizationID + "]...");

		if ((licenseID == null) || (licenseID.length() == 0)) {
			throw new IllegalArgumentException(
					"license ID argument was null or empty.");
		}

		if ((organizationID == null) || (organizationID.length() == 0)) {
			throw new IllegalArgumentException(
					"organization ID argument was null or empty.");
		}

		if (getLicense(licenseID) == null) {
			throw new IllegalArgumentException(
					"Attempted to assign a license but the license does not exist.");
		}

		if (getOrganizationByID(organizationID) == null) {
			throw new IllegalArgumentException(
					"Attempted to assign a license but the organization does not exist.");
		}

		LicenseOrgAssoc licenseOrgAssoc = new LicenseOrgAssoc();

		licenseOrgAssoc.setLicenseID(licenseID);
		licenseOrgAssoc.setOrgID(organizationID);

		/**
		 * Add the License to the Organization
		 */

		add(licenseOrgAssoc);

		LOG.info("Assigned license ID [" + licenseID + "] to the organization ID ["
				+ organizationID + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#deleteLicense(com.atcloud
	 * .model.License)
	 */
	@Override
	public void deleteLicense(License license) {

		LOG.debug("Deleting license [" + license + "]...");

		if (license == null) {
			throw new IllegalArgumentException("License parameter was null.");
		}

		if ((license.getLicenseID() == null)
				|| (license.getLicenseID().length() == 0)) {
			throw new IllegalArgumentException(
					"License ID parameter was null or empty when deleting a license");
		}

		remove(license);

		LOG.debug("Deleting all LicenseOrgAssoc entries for [" + license + "] ...");

		Query query =
				getEntityManager()
						.createQuery(
								"delete from LicenseOrgAssoc l where"
										+ " l.licenseID = :licenseID");

		query.setParameter("licenseID", license.getLicenseID());

		query.executeUpdate();

		getEntityManager().flush();

		LOG.debug("All LicenseOrgAssoc entries deleted for [" + license + "].");

		LOG.info("License [" + license + "] has been deleted.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#reassignLicenseToOrganization
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void reassignLicenseToOrganization(final String licenseID,
			final String newOrganizationID) {

		LOG.debug("Reassigning licenseID [" + licenseID + "] to organization ID ["
				+ newOrganizationID + "].");

		final int changes =
				entityManager
						.createQuery(
								"update LicenseOrgAssoc l set l.orgID = :newOrganizationID where l.licenseID =: licenseID")
						.setParameter("newOrganizationID", newOrganizationID)
						.setParameter("licenseID", licenseID).executeUpdate();

		getEntityManager().flush();

		LOG.info("Reassigned [" + changes + "] licenses to organization ID ["
				+ newOrganizationID + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#getLicenses(com.atcloud.
	 * model.Organization)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<License> getLicenses(final Organization organization) {

		List<License> licenses = null;

		if (organization == null) {
			throw new IllegalArgumentException("Organization argument was null.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select l from License l, LicenseOrgAssoc loa, Organization o "
										+ "where l.licenseID = loa.licenseID and loa.orgID = o.orgID and o.orgID =:organizationID");
		query.setParameter("organizationID", organization.getOrgID());

		licenses = (List<License>) query.getResultList();

		return licenses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#getLicenses()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<License> getLicenses() {

		List<License> licenses = null;

		Query query = getEntityManager().createQuery("select l from License l");

		licenses = (List<License>) query.getResultList();

		return licenses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#deactiveLicense(com.atcloud
	 * .model.License)
	 */
	@Override
	public void deactiveLicense(final License license) {

		LOG.debug("Deactivating license [" + license + "]...");

		if (license == null) {
			throw new IllegalArgumentException("License argument was null.");
		}

		Calendar dateDeactivated = Calendar.getInstance();
		Boolean status = false;

		License licenseX = entityManager.find(License.class, license.getHjid());
		licenseX.setActive(status);
		licenseX.setDateActivated(null);
		licenseX.setDateDeactivated(dateDeactivated);

		getEntityManager().flush();

		LOG.info("Deactivated license [" + license + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#activeLicense(com.atcloud
	 * .model.License)
	 */
	@Override
	public void activeLicense(final License license) {

		LOG.debug("Activating license [" + license + "]...");

		if (license == null) {
			throw new IllegalArgumentException("License argument was null.");
		}

		Calendar dateActivated = Calendar.getInstance();
		Boolean status = true;

		License licenseX = entityManager.find(License.class, license.getHjid());
		licenseX.setActive(status);
		licenseX.setDateActivated(dateActivated);
		licenseX.setDateDeactivated(null);

		getEntityManager().flush();

		LOG.info("Activated license [" + license + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#updateLicense(com.atcloud
	 * .model.License)
	 */
	@Override
	public void updateLicense(final License license) {

		LOG.debug("Updating license [" + license + "]...");

		if (license == null) {
			throw new IllegalArgumentException("License argument was null.");
		}

		Query q =
				entityManager.createQuery("update License l set "
						+ "l.active =: active " + ", l.inUse =: inUse"
						+ ", l.dateActivated =: dateActivated"
						+ ", l.dateDeactivated =: dateDeactivated"
						+ ", l.dateCreated =: dateCreated"
						+ ", l.lastRenewalDate =: lastRenewalDate"
						+ " where l.licenseID =: licenseID");

		q.setParameter("active", license.isActive());
		q.setParameter("inUse", license.isInUse());
		q.setParameter("dateActivated", license.getDateActivated());
		q.setParameter("dateDeactivated", license.getDateDeactivated());
		q.setParameter("dateCreated", license.getDateCreated());
		q.setParameter("lastRenewalDate", license.getLastRenewalDate());

		q.setParameter("licenseID", license.getLicenseID());

		q.executeUpdate();

		getEntityManager().flush();

		LOG.debug("Updated license [" + license + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#updateOrganization(com.atcloud
	 * .model.Organization)
	 */
	@Override
	public void updateOrganization(final Organization organization) {

		if (organization == null) {
			throw new IllegalArgumentException("Organization argument was null.");
		}

		LOG.debug("Updating organization [" + organization + "]...");

		Query q =
				entityManager.createQuery("update Organization o set "
						+ "o.active =: active" + ", o.cellPhoneNumber =: cellPhoneNumber"
						+ ", o.dateActivated =: dateActivated"
						+ ", o.dateCreated =: dateCreated"
						+ ", o.dateDeactivated =: dateDeactivated" + ", o.email =: email"
						+ ", o.officePhoneNumber =: officePhoneNumber"
						+ ", o.orgName =: orgName" + " where o.orgID =: orgID");

		q.setParameter("active", organization.isActive());
		q.setParameter("cellPhoneNumber", organization.getCellPhoneNumber());
		q.setParameter("dateActivated", organization.getDateActivated());
		q.setParameter("dateCreated", organization.getDateCreated());
		q.setParameter("dateDeactivated", organization.getDateDeactivated());
		q.setParameter("email", organization.getEmail());
		q.setParameter("officePhoneNumber", organization.getOfficePhoneNumber());
		q.setParameter("orgName", organization.getOrgName());

		q.setParameter("orgID", organization.getOrgID());

		q.executeUpdate();

		getEntityManager().flush();

		LOG.debug("Updated organization [" + organization + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#createLicense()
	 */
	@Override
	public License createLicense() {
		return modelService.createLicense();
	}

	/**
	 * @return the modelService
	 */
	@WebMethod(exclude = true)
	public ModelService getModelService() {
		return modelService;
	}

	/**
	 * @param modelService
	 *          the modelService to set
	 */
	@WebMethod(exclude = true)
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.UserPersistenceService#getAllGroups()
	 */
	@Override
	public List<Group> getAllGroups() {
		return getEntityManager().createQuery("select g from Group g", Group.class)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.UserPersistenceService#updateGroup(com.atcloud.
	 * model.Group)
	 */
	@Override
	public void updateGroup(final Group group) {

		LOG.debug("Updating group [" + group + "]...");

		if (group == null) {
			throw new IllegalArgumentException("Group argument was null.");
		}

		Group groupX = entityManager.find(Group.class, group.getHjid());
		groupX.setName(group.getName());
		groupX.setActiveFEMName(group.getActiveFEMName());
		groupX.setDate(group.getDate());
		groupX.setDescription(group.getDescription());

		getEntityManager().flush();

		LOG.debug("Updated group [" + groupX + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.MetricsPersistenceService#addSimulation(com.atcloud
	 * .model.Simulation)
	 */
	@Override
	public void addSimulation(final Simulation simulation)
			throws ATCloudException {

		if (simulation == null) {
			throw new ATCloudException("Simulation was null.");
		}

		try {
			modelService.validateObject(simulation);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException("Invalid Simulation object encountered.", e);
		}

		add(simulation);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.MetricsPersistenceService#recordSimulationData(
	 * Simulation, AirFlightPosition)
	 */
	@Override
	public void recordSimulationData(final Simulation simulation,
			final AirFlightPosition airFlightPosition) throws ATCloudException {

		if (simulation == null) {
			throw new ATCloudException("Simulation was null.");
		}

		if (airFlightPosition == null) {
			throw new ATCloudException("AirFlightPosition was null.");
		}

		try {
			modelService.validateObject(airFlightPosition);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException(
					"Invalid AirFlightPosition object encountered.", e);
		}

		add(airFlightPosition);

		SimulationSimulationDataAssoc ssDataAssoc =
				new SimulationSimulationDataAssoc();
		ssDataAssoc.setSimulationID(simulation.getSimulationID());
		ssDataAssoc.setDataID(airFlightPosition.getFlightPositionID());

		add(ssDataAssoc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.MetricsPersistenceService#
	 * recordSimulationExecutionMetrics(com.atcloud.model.Simulation,
	 * com.atcloud.model.SimulationMetrics)
	 */
	@Override
	public void recordSimulationExecutionMetrics(final Simulation simulation,
			final SimulationMetrics simulationMetrics) {

		LOG.debug("Recording simulation [" + simulation + "] with ["
				+ simulationMetrics + "] metrics.");

		if (simulation == null) {
			throw new IllegalArgumentException(
					"Simulation parameter was null when attempting to record simulation metrics.");
		}

		if (simulationMetrics == null) {
			throw new IllegalArgumentException(
					"Simulation metrics parameter was null when attempting to record simulation metrics.");
		}

		add(simulationMetrics);

		SimulationSimMetricsAssoc simulationSimMetricsAssoc =
				new SimulationSimMetricsAssoc();
		simulationSimMetricsAssoc.setSimulationID(simulation.getSimulationID());
		simulationSimMetricsAssoc.setSimulationMetricsID(simulationMetrics
				.getSimulationMetricsID());

		add(simulationSimMetricsAssoc);

		LOG.info("Recorded metrics [" + simulationMetrics + "] for simulation ["
				+ simulation + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.MetricsPersistenceService#getSimulation(java.lang
	 * .String)
	 */
	@Override
	public Simulation getSimulation(final String simulationID) {

		Simulation simulation = null;

		Query query = createSimulationSelectByIDQuery(simulationID);

		if (!queryResultEmpty(query)) {
			simulation = (Simulation) query.getSingleResult();
		}

		return simulation;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private Query createSimulationSelectByIDQuery(final String simulationID) {
		Query query = null;

		try {
			query =
					getEntityManager().createQuery(
							"select s from Simulation s "
									+ "where s.simulationID =:simulationID");
			query.setParameter("simulationID", simulationID);
		} catch (Throwable t) {
			throw t;
		}
		return query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.MetricsPersistenceService#deleteAllSimulations()
	 */
	@Override
	public void deleteAllSimulations() {

		getEntityManager().createQuery("delete from Simulation").executeUpdate();

		LOG.info("All Simulations removed");

		getEntityManager().createQuery("delete from SimulationSimMetricsAssoc")
				.executeUpdate();

		LOG.info("All SimulationSimMetricsAssocs entries removed");

		getEntityManager().createQuery("delete from SimulationMetrics")
				.executeUpdate();

		LOG.info("All SimulationMetrics entries removed");

		getEntityManager().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#deleteAllFEMs()
	 */
	@Override
	public void deleteAllFEMs() {

		getEntityManager().createQuery("delete from FEM").executeUpdate();

		LOG.info("All FEMs removed.");

		getEntityManager().createQuery("delete from GroupFEMAssoc").executeUpdate();

		LOG.info("All GroupFEMAssoc entries removed.");

		getEntityManager().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#getAllFEMs()
	 */
	@Override
	public List<FEM> getAllFEMs() {
		return getEntityManager().createQuery("select f from FEM f", FEM.class)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#deleteFEM(com.atcloud.model
	 * .FEM)
	 */
	@Override
	public void deleteFEM(FEM fem) {

		LOG.debug("Deleting FEM [" + fem + "]...");

		if (fem == null) {
			throw new IllegalArgumentException(
					"FEM parameter was null or empty when attempting to remove a FEM");
		}

		LOG.debug("Deleting FEM [" + fem + "].");

		Query query =
				getEntityManager().createQuery(
						"delete from FEM f where" + " fem.femID = :femID");

		query.setParameter("femID", fem.getFemID());

		query.executeUpdate();

		LOG.debug("Removing FederateFEMAssoc entries for FEM [" + fem + "]...");

		query =
				getEntityManager().createQuery(
						"delete from FederateFEMAssoc f where" + " f.femName = :name");

		query.setParameter("name", fem.getName());

		query.executeUpdate();

		LOG.info("Removed FederateFEMAssoc entries for FEM [" + fem + "].");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#getFEM(java.lang.String)
	 */
	@Override
	public FEM getFEM(final String name) {

		FEM fem = null;

		if (name == null) {
			throw new IllegalArgumentException("Name parameter null.");
		}

		Query query =
				getEntityManager().createQuery(
						"select f from FEM f " + "where f.name=:name");
		query.setParameter("name", name);

		if (!queryResultEmpty(query)) {

			fem = (FEM) query.getSingleResult();
		}

		return fem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#updateFEM(com.atcloud.model
	 * .FEM)
	 */
	@Override
	public void update(FEM fem) throws ATCloudException {

		LOG.debug("Updating FEM [" + fem + "]...");

		if (fem == null) {
			throw new IllegalArgumentException(
					"FEM parameter was null when attempting to update a FEM.");
		}

		Query query = createSelectFEMByIDQuery(fem.getFemID());

		FEM femToUpdate = null;

		if (!queryResultEmpty(query)) {
			femToUpdate = (FEM) query.getSingleResult();
		}

		if (femToUpdate == null) {
			throw new ATCloudException(
					"Unable to find FEM in persistence store to update.");
		}

		/**
		 * Update each attribute
		 */

		femToUpdate.setDescription(fem.getDescription());
		femToUpdate.setAutoStart(fem.isAutoStart());
		femToUpdate.setDefDurStrtupPrtclMSecs(fem.getDefDurStrtupPrtclMSecs());
		femToUpdate.setFederationExecutionMSecs(fem.getFederationExecutionMSecs());
		femToUpdate.setFemID(fem.getFemID());
		femToUpdate.setJoinFederationMSecs(fem.getJoinFederationMSecs());
		femToUpdate.setLogicalStrtTimeMSecs(fem.getLogicalStrtTimeMSecs());
		femToUpdate.setPlannedStartTimeMSecs(fem.getPlannedStartTimeMSecs());
		femToUpdate.setRegisterPublicationMSecs(fem.getRegisterPublicationMSecs());
		femToUpdate
				.setRegisterSubscriptionMSecs(fem.getRegisterSubscriptionMSecs());
		femToUpdate.setRegisterToRunMSecs(fem.getRegisterToRunMSecs());
		femToUpdate.setWaitForStartMSecs(fem.getWaitForStartMSecs());
		femToUpdate.setWaitTimeAfterTermMSecs(fem.getWaitTimeAfterTermMSecs());

		getEntityManager().flush();

		LOG.info("Updated FEM [" + fem + "] to [" + femToUpdate + "]");

	}

	/**
	 * 
	 * @param femID
	 * @return
	 */
	private Query createSelectFEMByIDQuery(final String femID) {

		Query query = null;
		try {
			query =
					getEntityManager().createQuery(
							"select f from FEM f " + "where f.femID =:femID");

			query.setParameter("femID", femID);

		} catch (Throwable t) {
			throw t;
		}
		return query;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#setActiveFEMForGroup(com.
	 * atcloud.model.Group, java.lang.String)
	 */
	@Override
	public void setActiveFEMForGroup(final Group group, final String femName)
			throws ATCloudException {

		LOG.debug("Setting active FEM for group [" + group + "] to [" + femName
				+ "]...");

		if (group == null) {
			throw new IllegalArgumentException(
					"Group parameter was null when attempting to set the group's active FEM.");
		}

		Group groupToUpdate = getEntityManager().find(Group.class, group.getHjid());

		if (groupToUpdate == null) {
			throw new ATCloudException("Unable to find Group to update active FEM.");
		}

		groupToUpdate.setActiveFEMName(femName);

		getEntityManager().flush();

		LOG.info("Set active FEM of group [" + group + "] to [" + femName + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFederate(java.lang.String
	 * )
	 */
	@Override
	public Federate getFederate(String name) {

		Federate federate = null;

		if (name == null) {
			throw new IllegalArgumentException("Name parameter null.");
		}

		Query query =
				getEntityManager().createQuery(
						"select f from Federate f " + "where f.name=:name");
		query.setParameter("name", name);

		if (!queryResultEmpty(query)) {

			federate = (Federate) query.getSingleResult();
		}

		return federate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#update(com.atcloud.model.
	 * Federate)
	 */
	@Override
	public void update(Federate federate) throws ATCloudException {

		LOG.debug("Updating federate [" + federate + "]...");

		if (federate == null) {
			throw new IllegalArgumentException(
					"Federate parameter was null when attempting to update a federate.");
		}

		Federate federateToUpdate =
				getEntityManager().find(Federate.class, federate.getHjid());

		if (federateToUpdate == null) {
			throw new ATCloudException(
					"Unable to find Federate in persistence store to update.");
		}

		/*
		 * Update each attribute
		 */
		federateToUpdate.setName(federate.getName());
		federateToUpdate.setDescription(federate.getDescription());

		getEntityManager().flush();

		LOG.info("Updated federate [" + federate + "] to [" + federateToUpdate
				+ "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#deleteFederate(java.lang
	 * .String)
	 */
	@Override
	public void deleteFederate(String name) {

		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException(
					"Name parameter was null or empty when removing a federate");
		}

		LOG.debug("Deleting federate [" + name + "]...");

		Query query =
				getEntityManager().createQuery(
						"delete from Federate f where" + " f.name = :name");

		query.setParameter("name", name);

		query.executeUpdate();

		LOG.info("Deleted federate [" + name + "].");

		LOG.debug("Deleting FederateFEMAssoc entries for federate [" + name
				+ "]...");

		query =
				getEntityManager().createQuery(
						"delete from FederateFEMAssoc f where" + " f.federateName = :name");

		query.setParameter("name", name);

		query.executeUpdate();

		LOG.info("Deleted FederateFEMAssoc entries for federate [" + name + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#deleteAllFederates()
	 */
	@Override
	public void deleteAllFederates() {

		getEntityManager().createQuery("delete from Federate").executeUpdate();

		LOG.info("All federates removed");
		getEntityManager().createQuery("delete from FederateFEMAssoc")
				.executeUpdate();

		LOG.info("All FederateFEMAssoc entries removed");
		getEntityManager().createQuery("delete from FederateGroupAssoc")
				.executeUpdate();

		LOG.info("All FederateGroupAssoc entries removed");
		getEntityManager().flush();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#addFederateToFEM(com.atcloud
	 * .model.Federate, com.atcloud.model.FEM)
	 */
	@Override
	public void addFederateToFEM(final Federate federate, final FEM fem) {

		if (federate == null) {
			throw new IllegalArgumentException("Federate argument was null.");
		}

		if (fem == null) {
			throw new IllegalArgumentException("FEM argument was null.");
		}

		addFederateToFEM(federate.getName(), fem.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#addFederateToFEM(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void addFederateToFEM(final String federateName, final String femName) {

		LOG.debug("Adding federate [" + federateName + "] to the FEM [" + femName
				+ "].");

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new IllegalArgumentException(
					"Federate name argument was null or empty.");
		}

		if (getFederate(federateName) == null) {
			throw new IllegalArgumentException("Attempted to assign a federate ["
					+ federateName + "] to a FEM but the federate does not exist.");
		}

		if ((femName == null) || (femName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		if (getFEM(femName) == null) {
			throw new IllegalArgumentException("Attempted to assign a federate ["
					+ federateName + "] but the FEM [" + femName + "] does not exist.");
		}

		FederateFEMAssoc federateFEMAssoc = new FederateFEMAssoc();

		federateFEMAssoc.setFederateName(federateName);
		federateFEMAssoc.setFemName(femName);

		add(federateFEMAssoc);

		LOG.info("Federate [" + federateName + "] has been added to the FEM ["
				+ femName + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#removeFederateFromFEM(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void removeFederateFromFEM(final String federateName,
			final String femName) {

		LOG.debug("Removing federate [" + federateName + "] from the FEM ["
				+ femName + "]...");

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new IllegalArgumentException(
					"Federate name argument was null or empty.");
		}

		if ((femName == null) || (femName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"delete from FederateFEMAssoc ffa "
										+ "where ffa.federateName =:federateName and ffa.femName =:femName");

		query.setParameter("federateName", federateName);
		query.setParameter("femName", femName);

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed federate [" + federateName + "] from FEM [" + femName
				+ "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#removeFederateFromGroup(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void removeFederateFromGroup(final String federateName,
			final String groupName) {

		LOG.debug("Removing federate [" + federateName + "] from the [" + groupName
				+ "] group...");

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new IllegalArgumentException(
					"Federate name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"delete from FederateGroupAssoc fga "
										+ "where fga.federateName =:federateName and fga.groupName =:groupName");

		query.setParameter("federateName", federateName);
		query.setParameter("groupName", groupName);

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed federate [" + federateName + "] from the [" + groupName
				+ "] group.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#getFederatesInAFEM(java
	 * .lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Federate> getFederatesInFEM(String femName)
			throws ATCloudException {

		List<Federate> federates = null;

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select f from Federate f, FederateFEMAssoc ffa, FEM fem "
										+ "where f.name = ffa.federateName and ffa.femName = fem.name and fem.name =:femName");
		query.setParameter("femName", femName);

		federates = (List<Federate>) query.getResultList();

		return federates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#assignFederateToGroup(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void addFederateToGroup(String federateName, String groupName) {

		LOG.debug("Adding federate [" + federateName + "] to the group ["
				+ groupName + "]...");

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new IllegalArgumentException(
					"Federate name argument was null or empty.");
		}

		if (getFederate(federateName) == null) {
			throw new IllegalArgumentException("Attempted to assign a federate ["
					+ federateName + "] to a FEM but the federate does not exist.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		if (getGroup(groupName) == null) {
			throw new IllegalArgumentException("Attempted to assign the federate ["
					+ federateName + "] to the Group [" + groupName
					+ "] but the group does not exist.");
		}

		FederateGroupAssoc federateGroupAssoc = new FederateGroupAssoc();

		federateGroupAssoc.setFederateName(federateName);
		federateGroupAssoc.setGroupName(groupName);

		add(federateGroupAssoc);

		LOG.info("Federate [" + federateName + "] has been added to the Group ["
				+ groupName + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getGroupFEM(java.lang.String)
	 */
	@Override
	public FEM getActiveFEMForGroup(String groupName) {

		FEM fem = null;

		if (groupName == null) {
			throw new IllegalArgumentException("Name parameter null.");
		}

		Query query =
				getEntityManager().createQuery(
						"select f from FEM f, Group g  "
								+ "where f.name = g.activeFEMName and g.name =:groupName");
		query.setParameter("groupName", groupName);

		if (!queryResultEmpty(query)) {

			fem = (FEM) query.getSingleResult();
		}

		return fem;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFederateGroup(java.lang
	 * .String)
	 */
	@Override
	public Group getFederateGroup(final String federateName) {

		Group group = null;

		if (federateName == null) {
			throw new IllegalArgumentException("Name parameter null.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select g from Group g, FederateGroupAssoc fga, Federate f  "
										+ "where g.name = fga.groupName and fga.federateName = f.name and f.name =: federateName");

		query.setParameter("federateName", federateName);

		if (!queryResultEmpty(query)) {

			group = (Group) query.getSingleResult();
		}

		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFederateActiveFEM(java
	 * .lang.String)
	 */
	@Override
	public FEM getFederateActiveFEM(final String federateName) {

		FEM fem = null;

		Group group = getFederateGroup(federateName);

		if (group != null) {
			fem = getActiveFEMForGroup(group.getName());
		}

		return fem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.persistence.FEMPersistenceService#getFederates()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Federate> getFederates() {

		Query query = getEntityManager().createQuery("select f from Federate f");

		return (List<Federate>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#addFederate(java.lang.String)
	 */
	@Override
	public Federate addFederate(final String name, final String description)
			throws ATCloudException {
		Federate federate = getModelService().createFederate(name, description);

		try {
			getModelService().validateObject(federate);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException(e);
		}

		add(federate);

		return federate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#setGroupActiveFEM(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void setGroupActiveFEM(String groupName, String femName)
			throws ATCloudException {

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		if ((femName == null) || (femName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		LOG.debug("Setting active FEM of group [" + groupName + "] to [" + femName
				+ "]...");

		Group groupToUpdate = getGroup(groupName);

		groupToUpdate.setActiveFEMName(femName);

		getEntityManager().flush();

		LOG.info("Updated active FEM of group [" + groupName + "] to [" + femName
				+ "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFederatesInGroup(java.
	 * lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Federate> getFederatesInGroup(String groupName) {

		List<Federate> federates = null;

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		Query query =
				getEntityManager()
						.createQuery(
								"select f from Federate f, FederateGroupAssoc fga "
										+ "where f.name = fga.federateName and fga.groupName =:groupName");
		query.setParameter("groupName", groupName);

		federates = (List<Federate>) query.getResultList();

		return federates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFEMsInGroup(java.lang.
	 * String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FEM> getFEMsInGroup(final String groupName) {

		List<FEM> fems = null;

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException(
					"Group name argument was null or empty.");
		}

		Query query =
				getEntityManager().createQuery(
						"select f from FEM f, GroupFEMAssoc gfa "
								+ "where f.name = gfa.femName and gfa.groupName =:groupName");
		query.setParameter("groupName", groupName);

		fems = (List<FEM>) query.getResultList();

		return fems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#addFEMToGroup(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public void addFEMToGroup(final String femName, final String groupName) {

		LOG.debug("Adding FEM [" + femName + "] to the group [" + groupName
				+ "]...");

		if ((femName == null) || (femName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		if (getFEM(femName) == null) {
			throw new IllegalArgumentException("Attempted to assign a FEM ["
					+ femName + "] to a group but the FEM does not exist.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		if (getGroup(groupName) == null) {
			throw new IllegalArgumentException("Attempted to assign the FEM ["
					+ femName + "] to the group [" + groupName
					+ "] but the group does not exist.");
		}

		GroupFEMAssoc groupFEMAssoc = new GroupFEMAssoc();

		groupFEMAssoc.setFemName(femName);
		groupFEMAssoc.setGroupName(groupName);

		add(groupFEMAssoc);

		LOG.info("FEM [" + femName + "] has been assigned to the [" + groupName
				+ "] group.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#removeFEMFromGroup(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void removeFEMFromGroup(String femName, String groupName)
			throws ATCloudException {

		LOG.debug("Removing FEM [" + femName + "] from the [" + groupName
				+ "] group...");

		if ((femName == null) || (femName.length() == 0)) {
			throw new IllegalArgumentException("FEM name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		Query query =
				getEntityManager().createQuery(
						"delete from GroupFEMAssoc gfa "
								+ "where gfa.femName =:femName and gfa.groupName =:groupName");

		query.setParameter("femName", femName);
		query.setParameter("groupName", groupName);

		query.executeUpdate();

		getEntityManager().flush();

		LOG.info("Removed FEM [" + femName + "] from the group [" + groupName
				+ "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#removeActiveFEMForGroup(java
	 * .lang.String)
	 */
	@Override
	public boolean removeActiveFEMForGroup(String groupName)
			throws ATCloudException {

		boolean status = false;

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		Group groupToUpdate = getGroup(groupName);

		if (groupToUpdate != null) {

			groupToUpdate.setActiveFEMName("");

			getEntityManager().flush();

			status = true;
		}

		return status;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getFEMbyID(java.lang.String)
	 */
	@Override
	public FEM getFEMbyID(String id) {

		FEM fem = null;

		if (id == null) {
			throw new IllegalArgumentException("ID parameter null.");
		}

		Query query =
				getEntityManager().createQuery(
						"select f from FEM f " + "where f.femID =: id");
		query.setParameter("id", id);

		if (!queryResultEmpty(query)) {

			fem = (FEM) query.getSingleResult();

		}

		return fem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.persistence.FEMPersistenceService#getScenario(java.lang.String)
	 */
	@Override
	public Scenario getScenario(final String name) {

		Scenario scenario = null;

		if (name == null) {
			throw new IllegalArgumentException("Name parameter is null");
		}

		Query query =
				getEntityManager().createQuery(
						"select s from Scenario s " + "where s.name =: name");
		query.setParameter("name", name);

		if (!queryResultEmpty(query)) {

			scenario = (Scenario) query.getSingleResult();

		}

		return scenario;
	}

}