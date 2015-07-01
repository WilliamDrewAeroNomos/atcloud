/**
 * 
 */
package com.atcloud.user;

import java.util.Collection;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.Path;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.dcache.DistributedCacheService;
import com.atcloud.model.Group;
import com.atcloud.model.Name;
import com.atcloud.model.Organization;
import com.atcloud.model.Role;
import com.atcloud.model.User;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@WebService
public interface UserService {

	/**
	 * @throws ATCloudException
	 * 
	 */
	void start() throws ATCloudException;

	/**
	 * 
	 */
	void stop();

	/**
	 * 
	 * @return
	 */
	@Path("/getAllUsers")
	@WebResult(name = "User", targetNamespace = "http://model.atcloud.com/user")
	Collection<User> getAllUsers();

	/**
	 * 
	 * @param name
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/getUser")
	@WebResult(name = "User", targetNamespace = "http://model.atcloud.com/user")
	User getUser(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final Name name)
			throws ATCloudException;

	/**
	 * 
	 * @param user
	 * @throws ATCloudException
	 */
	@Path("/deleteUser")
	@WebResult(name = "User", targetNamespace = "http://model.atcloud.com/user")
	void deleteUser(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user)
			throws ATCloudException;

	/**
	 * Adds a {@link User} to the system.
	 * 
	 * @param user
	 *          {@link User} to add to the system.
	 * @throws ATCloudException
	 */
	@Path("/addUser")
	void addUser(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user)
			throws ATCloudException;

	/**
	 * 
	 * @param roleName
	 * @param description
	 * @throws ATCloudException
	 */
	@Path("/addRole")
	void addRole(@WebParam(name = "roleName") final String roleName, @WebParam(
			name = "description") final String description) throws ATCloudException;

	/**
	 * 
	 * @param user
	 * @param roleName
	 * @throws ATCloudException
	 */
	@Path("/assignUserToRole")
	void assignUserToRole(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user,
			@WebParam(name = "roleName") final String roleName)
			throws ATCloudException;

	/**
	 * 
	 * @param roleName
	 * @throws ATCloudException
	 */
	@Path("/removeRole")
	void removeRole(@WebParam(name = "roleName") final String roleName)
			throws ATCloudException;

	/**
	 * 
	 * @param user
	 * @param roleName
	 * @throws ATCloudException
	 */
	@Path("/removeUserFromRole")
	void removeUserFromRole(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user,
			@WebParam(name = "roleName") final String roleName)
			throws ATCloudException;

	/**
	 * 
	 * @param roleName
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/getRole")
	@WebResult(name = "Role", targetNamespace = "http://model.atcloud.com/user")
	Role getRole(@WebParam(name = "roleName") final String roleName)
			throws ATCloudException;

	/**
	 * 
	 * @param user
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/getUserRoles")
	@WebResult(name = "Role", targetNamespace = "http://model.atcloud.com/user")
	public List<Role> getUserRoles(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user)
			throws ATCloudException;

	/**
	 * 
	 * @param user
	 * @throws ATCloudException
	 */
	@Path("/updateUser")
	void updateUser(@WebParam(name = "user",
			targetNamespace = "http://model.atcloud.com/user") final User user)
			throws ATCloudException;

	/**
	 * @param role
	 * @throws ATCloudException
	 */
	@Path("/updateRole")
	void updateRole(@WebParam(name = "role",
			targetNamespace = "http://model.atcloud.com/user") final Role role)
			throws ATCloudException;

	/**
	 * 
	 * @param group
	 * @throws ATCloudException
	 */
	@Path("/updateGroup")
	void updateGroup(@WebParam(name = "group",
			targetNamespace = "http://model.atcloud.com/user") final Group group)
			throws ATCloudException;

	/**
	 * 
	 * @return
	 */
	Organization createOrganization();

	/**
	 * 
	 * @param organization
	 * @throws ATCloudException
	 */
	@Path("/addOrganization")
			void
			addOrganization(
					@WebParam(name = "organization",
							targetNamespace = "http://model.atcloud.com/user") final Organization organization)
					throws ATCloudException;

	/**
	 * 
	 * @param name
	 * @param description
	 * @param activeFEMName
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/addGroup")
	@WebResult(name = "Group", targetNamespace = "http://model.atcloud.com/user")
	Group addGroup(@WebParam(name = "name") final String name, @WebParam(
			name = "description") final String description, @WebParam(
			name = "activeFEMName") final String activeFEMName)
			throws ATCloudException;

	/**
	 * 
	 * @return
	 */
	@Path("/getAllGroups")
	@WebResult(name = "Group", targetNamespace = "http://model.atcloud.com/user")
	List<Group> getAllGroups();

	/**
	 * 
	 * @param name
	 * @return
	 */
	@Path("/getGroupByName")
	@WebResult(name = "Group", targetNamespace = "http://model.atcloud.com/user")
	Group getGroupByName(@WebParam(name = "name") final String name);

	/**
	 * 
	 * @param orgID
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/getOrgByID")
	@WebResult(name = "Organization",
			targetNamespace = "http://model.atcloud.com/user")
	Organization
			getOrganizationByID(@WebParam(name = "orgID") final String orgID)
					throws ATCloudException;

	/**
	 * 
	 * @param orgName
	 * @return
	 * @throws ATCloudException
	 */
	@Path("/getOrgByName")
	@WebResult(name = "Organization",
			targetNamespace = "http://model.atcloud.com/user")
	Organization getOrganizationByName(
			@WebParam(name = "orgName") final String orgName) throws ATCloudException;

	/**
	 * 
	 * @param persistenceService
	 */
	void setPersistenceService(final PersistenceService persistenceService);

	/**
	 * 
	 * @param dcacheService
	 */
	void setDcacheService(final DistributedCacheService dcacheService);

	/**
	 * 
	 * @param commonsService
	 */
	void setCommonsService(final CommonsService commonsService);

}
