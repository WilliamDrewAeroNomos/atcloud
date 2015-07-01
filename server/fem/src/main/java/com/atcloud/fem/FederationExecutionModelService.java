/**
 * 
 */
package com.atcloud.fem;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.dcache.DistributedCacheService;
import com.atcloud.model.FEM;
import com.atcloud.model.Federate;
import com.atcloud.model.Group;
import com.atcloud.model.ModelService;
import com.atcloud.model.Scenario;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@WebService
public interface FederationExecutionModelService {

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
	 * Retrieves all of the {@link FEM}s in the system.
	 * 
	 * @return {@link List} of FEMs
	 */
	@POST
	@Path("/getAllFEMs")
	@WebResult(name = "FEM",
			targetNamespace = "http://model.atcloud.com/scenario")
	List<FEM> getAllFEMs();

	/**
	 * Adds a {@link FEM} to the system uniquely identified by
	 * {@link FEM#getName()}.
	 * <p>
	 * 
	 * @param fem
	 *          FEM to be added to the system.
	 * @return
	 * @throws ATCloudException
	 *           The most likely non-system related cause will be an attempt to
	 *           add a duplicate FEM.
	 * 
	 */
	@POST
	@Path("/addFEM")
	@WebResult(name = "FEM",
			targetNamespace = "http://model.atcloud.com/scenario")
	FEM addFEM(@WebParam(name = "fem",
			targetNamespace = "http://model.atcloud.com/scenario") final FEM fem)
			throws ATCloudException;

	/**
	 * 
	 * @param modelService
	 */
	void setModelService(ModelService modelService);

	/**
	 * 
	 * @return
	 */
	ModelService getModelService();

	/**
	 * 
	 * @param persistenceService
	 */
	void setPersistenceService(PersistenceService persistenceService);

	/**
	 * 
	 * @return
	 */
	PersistenceService getPersistenceService();

	/**
	 * 
	 * @param dcacheService
	 */
	void setDcacheService(DistributedCacheService dcacheService);

	/**
	 * 
	 * @return
	 */
	DistributedCacheService getDcacheService();

	/**
	 * 
	 * @param commonsService
	 */
	void setCommonsService(final CommonsService commonsService);

	/**
	 * 
	 * @return
	 */
	CommonsService getCommonsService();

	/**
	 * Updates a {@link FEM} using the provided <code>fem</code>.
	 * 
	 * @param fem
	 * @throws ATCloudException
	 */
	@POST
	@Path("/updateFEM")
	void updateFEM(@WebParam(name = "fem",
			targetNamespace = "http://model.atcloud.com/scenario") FEM fem)
			throws ATCloudException;

	/**
	 * Returns the {@link FEM} matching the name parameter.
	 * 
	 * @param name
	 *          Name of the FEM.
	 * @return FEM matching the name parameter.
	 */
	@POST
	@Path("/getFEMByName")
	@WebResult(name = "FEM",
			targetNamespace = "http://model.atcloud.com/scenario")
	FEM getFEM(@WebParam(name = "name") final String name);

	/**
	 * Returns the {@link FEM} matching the <code>id</code> parameter.
	 * 
	 * @param id
	 *          Id of the FEM.
	 * @return FEM matching the id parameter.
	 * @throws ATCloudException
	 */
	@POST
	@Path("/getFEMByID")
	@WebResult(name = "FEM",
			targetNamespace = "http://model.atcloud.com/scenario")
	FEM getFEMByID(@WebParam(name = "id") final String id)
			throws ATCloudException;

	/**
	 * Constructs a new {@link FEM} with defaults and required values.
	 * 
	 * @param name
	 *          Name of the FEM.
	 * @return Properly constructed FEM with appropriate default values.
	 */
	FEM createFEM(final String name);

	/**
	 * Adds a {@link Federate} to the system with {@code name} and
	 * {@code description}.
	 * 
	 * @param name
	 *          Federate name
	 * @param description
	 *          Long description of the federate
	 * @return Persisted {@link Federate}
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addFederate")
	Federate addFederate(@WebParam(name = "name") final String name, @WebParam(
			name = "description") final String description) throws ATCloudException;

	/**
	 * Returns all the {@link Federate}s currently in the system.
	 * 
	 * @return {@link List} of {@link Federate}s
	 */
	@POST
	@Path("/getAllFederates")
	@WebResult(name = "Federate",
			targetNamespace = "http://model.atcloud.com/scenario")
	List<Federate> getAllFederates();

	/**
	 * Returns all the {@link Federate}s associated with the {@link Group}
	 * identified by {@code groupName}.
	 * 
	 * @param groupName
	 *          Name of the {@link Group}
	 * @return {@link List} of {@link Federate}s
	 * @throws ATCloudException
	 * @see {@link Group}
	 */
	@POST
	@Path("/getFederatesInGroup")
	@WebResult(name = "Federate",
			targetNamespace = "http://model.atcloud.com/scenario")
	List<Federate> getFederatesInGroup(
			@WebParam(name = "groupName") final String groupName)
			throws ATCloudException;

	/**
	 * 
	 * @param federateName
	 * @param femName
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addFederateToFEM")
	void addFederateToFEM(
			@WebParam(name = "federateName") final String federateName, @WebParam(
					name = "femName") final String femName) throws ATCloudException;

	/**
	 * Removes a {@link Federate} from a {@link Group} identified by
	 * {@code groupName}
	 * 
	 * @param federateName
	 *          Name of the federate to remove from the group
	 * @param groupName
	 *          Name of the group from which the federate will be removed.
	 * @throws ATCloudException
	 */
	@POST
	@Path("/removeFederateFromGroup")
	void removeFederateFromGroup(
			@WebParam(name = "federateName") final String federateName, @WebParam(
					name = "groupName") final String groupName) throws ATCloudException;

	/**
	 * Retrieve all the {@link FEM} for a specified {@link Group} designatied by
	 * {@code groupname}.
	 * 
	 * @param groupName
	 *          Name of the {@link Group}
	 * @return {@link List} of {@link FEM}s
	 * @throws ATCloudException
	 */
	@POST
	@Path("/getFEMsInGroup")
	List<FEM>
			getFEMsInGroup(@WebParam(name = "groupName") final String groupName)
					throws ATCloudException;

	/**
	 * Adds a {@link FEM} to a {@link Group}.
	 * 
	 * @param femName
	 *          Name of the {@link FEM}
	 * @param groupName
	 *          Name of the {@link Group} to which it will be added.
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addFEMToGroup")
	void addFEMToGroup(@WebParam(name = "femName") final String femName,
			@WebParam(name = "groupName") final String groupName)
			throws ATCloudException;

	/**
	 * Removes a {@link FEM} from a {@link Group}
	 * 
	 * @param femName
	 *          Name of the FEM to remove
	 * @param groupName
	 *          Name of the Group from which the FEM will be removed.
	 * @throws ATCloudException
	 */
	@POST
	@Path("/removeFEMFromGroup")
	void removeFEMFromGroup(@WebParam(name = "femName") final String femName,
			@WebParam(name = "groupName") final String groupName)
			throws ATCloudException;

	/**
	 * Removes a {@link Federate} from a {@link FEM}
	 * 
	 * @param federateName
	 * @param femName
	 * @throws ATCloudException
	 */
	@POST
	@Path("/removeFederateFromFEM")
	void removeFederateFromFEM(
			@WebParam(name = "federateName") final String federateName, @WebParam(
					name = "femName") final String femName) throws ATCloudException;

	/**
	 * Gets the {@link FEM} which has been set as the active or current FEM for
	 * the {@link Group}.
	 * 
	 * @param groupName
	 *          Name of the Group
	 * @return Current or active FEM for the Group
	 * @throws ATCloudException
	 */
	@POST
	@Path("/getActiveFEMForGroup")
	@WebResult(name = "FEM",
			targetNamespace = "http://model.atcloud.com/scenario")
			FEM
			getActiveFEMForGroup(@WebParam(name = "groupName") final String groupName)
					throws ATCloudException;

	/**
	 * Removes the active or current {@link FEM} for the {@link Group} identified
	 * by <code>groupName</code>
	 * 
	 * @param groupName
	 *          Name of the Group
	 * @throws ATCloudException
	 */
	@POST
	@Path("/removeActiveFEMForGroup")
	boolean removeActiveFEMForGroup(
			@WebParam(name = "groupName") final String groupName)
			throws ATCloudException;

	/**
	 * Sets the active or current {@link FEM} for the {@link Group}
	 * 
	 * @param groupName
	 *          Name of the Group
	 * @param femName
	 *          Name of the FEM
	 * @throws ATCloudException
	 */
	@POST
	@Path("/setActiveFEMForGroup")
	void setActiveFEMForGroup(
			@WebParam(name = "groupName") final String groupName, @WebParam(
					name = "femName") final String femName) throws ATCloudException;

	/**
	 * Add a {@link Federate} to a {@link Group}.
	 * 
	 * @param federateName
	 *          Name of the Federate to add to the Group
	 * @param groupName
	 *          Name of the Group to which the Federate will be added
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addFederateToGroup")
	void addFederateToGroup(
			@WebParam(name = "federateName") final String federateName, @WebParam(
					name = "groupName") final String groupName) throws ATCloudException;

	/**
	 * Returns the list of {@link Federate}s that are assigned to a {@link FEM}
	 * identified by <code>femName</code>.
	 * 
	 * @param femName
	 * @return
	 * @throws ATCloudException
	 */
	@POST
	@Path("/getFederatesInFEM")
	@WebResult(name = "Federate",
			targetNamespace = "http://model.atcloud.com/scenario")
	List<Federate> getFederatesInFEM(
			@WebParam(name = "femName") final String femName) throws ATCloudException;

	/**
	 * Adds a {@link Scenario} using the name and description values.
	 * 
	 * @param name
	 * @param description
	 * @return
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addScenario")
	@WebResult(name = "Scenario",
			targetNamespace = "http://model.atcloud.com/scenario")
	Scenario addScenario(@WebParam(name = "name") final String name, @WebParam(
			name = "description") final String description) throws ATCloudException;

	/**
	 * Get the {@link Scenario} that matches the <code>name</code> parameter.
	 * 
	 * @param name
	 * @return
	 * @throws ATCloudException
	 */
	@POST
	@Path("/getScenario")
	@WebResult(name = "Scenario",
			targetNamespace = "http://model.atcloud.com/scenario")
	public Scenario getScenario(@WebParam(name = "name") final String name)
			throws ATCloudException;
}
