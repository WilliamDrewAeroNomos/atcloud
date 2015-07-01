/**
 * 
 */
package com.atcloud.user.internal;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.dcache.DistributedCacheService;
import com.atcloud.model.Group;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.User;
import com.atcloud.persistence.PersistenceService;
import com.atcloud.user.UserService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@Produces(MediaType.APPLICATION_XML)
@WebService
public class UserServiceImpl implements UserService {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(UserServiceImpl.class.getName());

	private CommonsService commonsService = null;

	private DistributedCacheService dcacheService = null;

	public PersistenceService persistenceService = null;

	private Role role = new Role();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#start()
	 */
	@WebMethod(exclude = true)
	@Override
	public void start() throws ATCloudException {

		LOG.debug("Starting user service...");

		if (!persistenceService.roleExists("Default")) {

			role = new Role();

			Calendar calendar = Calendar.getInstance();

			role.setDate(calendar);
			role.setDescription("Default role");
			role.setName("Default");

			persistenceService.add(role);

		} else {
			role = persistenceService.getRole("Default");
		}

		LOG.info("Started user service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#stop()
	 */
	@WebMethod(exclude = true)
	@Override
	public void stop() {

		LOG.debug("Stopping user service...");

		LOG.info("Stopped user service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#addUser(com.atcloud.model.User)
	 */
	@POST
	public void addUser(User user) throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User parameter to add new user was null");
		}

		/**
		 * Persist new User
		 */
		persistenceService.addUser(user, role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getAllUsers()
	 */
	@GET
	@Path("/")
	@Override
	public Collection<User> getAllUsers() {

		return persistenceService.getAllUsers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#updateUser(com.atcloud.model.data.User)
	 */
	@POST
	@Path("/")
	@Override
	public void updateUser(final User user) throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User parameter to update a user was null");
		}

		persistenceService.updateUser(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#deleteUser(com.atcloud.model.data.User)
	 */
	@POST
	@Path("/")
	@Override
	public void deleteUser(final User user) throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User parameter to add new user was null");
		}

		/*
		 * Remove User
		 */
		persistenceService.deleteUser(user);

		LOG.debug("Deleted user [" + user + "]");

	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#addRole(java.lang.String,
	 * java.lang.String)
	 */
	@POST
	@Path("/")
	@Override
	public void addRole(final String roleName, final String description)
			throws ATCloudException {

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new ATCloudException("Role name argument was null or empty "
					+ "when attempting to add a new role.");
		}

		Role role = new Role();

		role.setDate(Calendar.getInstance());
		role.setName(roleName);
		role.setDescription(description);

		persistenceService.add(role);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#updateRole(com.atcloud.model.data.Role)
	 */
	@POST
	@Path("/")
	@Override
	public void updateRole(final Role role) throws ATCloudException {

		if (role == null) {
			throw new ATCloudException(
					"Role parameter was null when attempting to update a role.");
		}

		persistenceService.updateRole(role);
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#assignRoleToUser(com.atcloud.model.data.
	 * User, java.lang.String)
	 */
	@POST
	@Path("/")
	@Override
	public void assignUserToRole(final User user, final String roleName)
			throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User argument was null.");
		}

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new ATCloudException("Role name argument was null or empty.");
		}

		persistenceService.assignUserToRole(user, roleName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.user.UserService#removeRoleFromUser(com.atcloud.model.data.
	 * User, java.lang.String)
	 */
	@POST
	@Path("/")
	@Override
	public void removeUserFromRole(final User user, final String roleName)
			throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User argument was null.");
		}

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new ATCloudException("Role name argument was null or empty.");
		}

		persistenceService.removeUserFromRole(user, roleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#removeRole(java.lang.String)
	 */
	@POST
	@Path("/")
	@Override
	public void removeRole(final String roleName) throws ATCloudException {

		if ((roleName == null) || (roleName.length() == 0)) {
			throw new ATCloudException(
					"Name parameter was null or empty when removing a role");
		}

		persistenceService.deleteRole(roleName);
	}

	/**
	 * @param commonsService
	 *          the commonsService to set
	 */
	@WebMethod(exclude = true)
	@Override
	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/*
	 * 
	 */
	@WebMethod(exclude = true)
	public CommonsService getCommonsService() {
		return commonsService;
	}

	/**
	 * @return the dcacheService
	 */
	@WebMethod(exclude = true)
	public DistributedCacheService getDcacheService() {
		return dcacheService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#setDcacheService(com.atcloud.dcache.
	 * DistributedCacheService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setDcacheService(DistributedCacheService dcacheService) {
		this.dcacheService = dcacheService;
	}

	/**
	 * @return the persistenceService
	 */
	@WebMethod(exclude = true)
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.user.UserService#setPersistenceService(com.atcloud.persistence
	 * .PersistenceService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getRole(java.lang.String)
	 */
	@Override
	public Role getRole(final String roleName) throws ATCloudException {
		if ((roleName == null) || (roleName.length() == 0)) {
			throw new ATCloudException("Role name was null or empty.");
		}
		return persistenceService.getRole(roleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getUser(com.atcloud.model.Name)
	 */
	@Override
	public User getUser(final Name name) throws ATCloudException {

		if (name == null) {
			throw new ATCloudException("Name was null.");
		}

		return persistenceService.getUserByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getUserRoles(com.atcloud.model.User)
	 */
	@Override
	public List<Role> getUserRoles(User user) throws ATCloudException {

		if (user == null) {
			throw new ATCloudException("User was null.");
		}

		return persistenceService.getUserRoles(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#createOrganization()
	 */
	@WebMethod(exclude = true)
	@Override
	public Organization createOrganization() {

		LOG.debug("Created organization.");

		return persistenceService.getModelService().createOrganization();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.user.UserService#addOrganization(com.atcloud.model.Organization
	 * )
	 */
	@Override
	public void addOrganization(final Organization organization)
			throws ATCloudException {

		if (organization == null) {
			throw new ATCloudException("Organization was null.");
		}

		LOG.debug("Adding organization [" + organization + "]...");

		persistenceService.add(organization);

		LOG.info("Added organization [" + organization + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#addGroup(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Group addGroup(final String name, final String description,
			final String activeFEMName) throws ATCloudException {

		if ((name == null) || (name.length() == 0)) {
			throw new ATCloudException("Group name was null or empty.");
		}

		if ((description == null) || (description.length() == 0)) {
			throw new ATCloudException("Description was null or empty.");
		}

		Group group = new Group();

		group.setName(name);
		group.setDescription(description);
		group.setDate(Calendar.getInstance());
		if ((activeFEMName != null) && (activeFEMName.length() > 0)) {
			group.setActiveFEMName(activeFEMName);
		}

		LOG.debug("Adding group [" + group + "]...");

		persistenceService.add(group);

		LOG.info("Added group [" + group + "].");

		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getAllGroups()
	 */
	@Override
	public List<Group> getAllGroups() {
		return persistenceService.getAllGroups();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getGroupByName(java.lang.String)
	 */
	@Override
	public Group getGroupByName(String name) {
		return persistenceService.getGroup(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#updateGroup(com.atcloud.model.Group)
	 */
	@Override
	public void updateGroup(Group group) throws ATCloudException {

		if (group == null) {
			throw new ATCloudException(
					"Group parameter was null when attempting to update a group.");
		}

		persistenceService.updateGroup(group);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getOrganization(java.lang.String)
	 */
	@Override
	public Organization getOrganizationByName(String orgName)
			throws ATCloudException {
		if (orgName == null) {
			throw new ATCloudException("Organization name parameter was null "
					+ "when attempting to retrieve an organization by name.");
		}
		return persistenceService.getOrganizationByName(orgName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getOrganizationID(java.lang.String)
	 */
	@Override
	public Organization getOrganizationByID(String orgID) throws ATCloudException {
		if (orgID == null) {
			throw new ATCloudException("Organization ID parameter was null "
					+ "when attempting to retrieve an organization by ID.");
		}
		return persistenceService.getOrganizationByID(orgID);
	}
}
