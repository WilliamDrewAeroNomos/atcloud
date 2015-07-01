/**
 * 
 */
package com.atcloud.fem.internal;

import java.util.List;
import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.dcache.DistributedCacheService;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.model.ATCloudDataModelException;
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
@Produces(MediaType.APPLICATION_XML)
@WebService
public class FederationExecutionModelServiceImpl implements
		FederationExecutionModelService {

	private CommonsService commonsService = null;

	private DistributedCacheService dcacheService = null;

	private PersistenceService persistenceService = null;

	private ModelService modelService = null;

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(FederationExecutionModelServiceImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#start()
	 */
	@WebMethod(exclude = true)
	@Override
	public void start() throws ATCloudException {
		LOG.debug("Starting FEM service...");

		LOG.info("Started FEM service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#stop()
	 */
	@WebMethod(exclude = true)
	@Override
	public void stop() {
		LOG.debug("Stopping FEM service...");

		LOG.info("Stopped FEM service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#addFem(com.atcloud.model
	 * .FEM)
	 */
	@Override
	public FEM addFEM(final FEM fem) throws ATCloudException {

		LOG.info("Adding FEM [" + fem + "]...");

		if (fem == null) {
			throw new ATCloudException("FEM argument was null.");
		}

		try {
			modelService.validateObject(fem);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException(e);
		}

		FEM femAdded = (FEM) persistenceService.add(fem);

		LOG.info("Added FEM [" + fem + "].");

		return femAdded;
	}

	@Override
	public Scenario addScenario(final String name, final String description)
			throws ATCloudException {

		LOG.info("Adding scenario [" + name + "].");

		if (name == null) {
			throw new ATCloudException("FEM argument was null.");
		}

		Scenario scenario = new Scenario();

		scenario.setScenarioID(UUID.randomUUID().toString());
		scenario.setName(name);
		scenario.setDescription(description);

		Scenario scenarioX = (Scenario) persistenceService.add(scenario);

		LOG.info("Added scenario [" + scenarioX + "].");

		return scenarioX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#getAllFEMs()
	 */
	@Override
	public List<FEM> getAllFEMs() {
		return persistenceService.getAllFEMs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#updateFEM(com.atcloud.model
	 * .FEM)
	 */
	@Override
	public void updateFEM(final FEM fem) throws ATCloudException {

		try {
			modelService.validateObject(fem);
		} catch (ATCloudDataModelException e) {
			throw new ATCloudException(e);
		}

		persistenceService.update(fem);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#addFederate(java.lang.String
	 * )
	 */
	@Override
	public Federate addFederate(final String name, final String description)
			throws ATCloudException {

		return persistenceService.addFederate(name, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFEM(java.lang.String)
	 */
	@Override
	public FEM getFEM(final String name) {
		return persistenceService.getFEM(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#getCommonsService()
	 */
	@WebMethod(exclude = true)
	@Override
	public CommonsService getCommonsService() {
		return commonsService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#setCommonsService(com.atcloud
	 * .commons.CommonsService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#getDcacheService()
	 */
	@WebMethod(exclude = true)
	@Override
	public DistributedCacheService getDcacheService() {
		return dcacheService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#setDcacheService(com.atcloud
	 * .dcache.DistributedCacheService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setDcacheService(DistributedCacheService dcacheService) {
		this.dcacheService = dcacheService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#getModelService()
	 */
	@WebMethod(exclude = true)
	@Override
	public ModelService getModelService() {
		return modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#setModelService(com.atcloud
	 * .model.ModelService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFemPersistenceService()
	 */
	@WebMethod(exclude = true)
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#setPersistenceService(com
	 * .atcloud.persistence.PersistenceService)
	 */
	@WebMethod(exclude = true)
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#createFEM(java.lang.String)
	 */
	@WebMethod(exclude = true)
	@Override
	public FEM createFEM(String name) {
		return modelService.createFEM(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.fem.FederationExecutionModelService#getAllFederates()
	 */
	@Override
	public List<Federate> getAllFederates() {
		return persistenceService.getFederates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFederatesInGroup(java
	 * .lang.String)
	 */
	@Override
	public List<Federate> getFederatesInGroup(final String groupName)
			throws ATCloudException {

		if (groupName == null) {
			throw new ATCloudException("Group name argument was null.");
		}

		return persistenceService.getFederatesInGroup(groupName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#addFederateToFEM(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void addFederateToFEM(String federateName, String femName)
			throws ATCloudException {

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new ATCloudException("Federate name argument was null or empty.");
		}

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		persistenceService.addFederateToFEM(federateName, femName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#removeFederateFromGroup
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void removeFederateFromGroup(String federateName, String groupName)
			throws ATCloudException {

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new ATCloudException("Federate name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		persistenceService.removeFederateFromGroup(federateName, groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFEMsInGroup(java.lang
	 * .String)
	 */
	@Override
	public List<FEM> getFEMsInGroup(String groupName) throws ATCloudException {

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		return persistenceService.getFEMsInGroup(groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#addFEMToGroup(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void addFEMToGroup(String femName, String groupName)
			throws ATCloudException {

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		persistenceService.addFEMToGroup(femName, groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#removeFEMFromGroup(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void removeFEMFromGroup(String femName, String groupName)
			throws ATCloudException {

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		persistenceService.removeFEMFromGroup(femName, groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#removeFederateFromFEM(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void removeFederateFromFEM(String federateName, String femName)
			throws ATCloudException {

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new ATCloudException("Federate name argument was null or empty.");
		}

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		persistenceService.removeFederateFromFEM(federateName, femName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getGroupFEM(java.lang.String
	 * )
	 */
	@Override
	public FEM getActiveFEMForGroup(final String groupName)
			throws ATCloudException {

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		return persistenceService.getActiveFEMForGroup(groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#setActiveFEMForGroup(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void
			setActiveFEMForGroup(final String groupName, final String femName)
					throws ATCloudException {

		if ((femName == null) || (femName.length() == 0)) {
			throw new ATCloudException("FEM name argument was null or empty.");
		}

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name argument was null or empty.");
		}

		FEM femToUpdate = persistenceService.getFEM(femName);

		if (femToUpdate == null) {
			throw new ATCloudException("FEM [" + femName + "] does not exist.");
		}

		Group groupToUpdate = persistenceService.getGroup(groupName);

		if (groupToUpdate == null) {
			throw new ATCloudException("Group [" + groupName + "] does not exist.");
		}

		persistenceService.setActiveFEMForGroup(groupToUpdate, femName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#addFederateToGroup(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void addFederateToGroup(String federateName, String groupName)
			throws ATCloudException {

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name was null or empty");
		}

		if ((federateName == null) || (federateName.length() == 0)) {
			throw new ATCloudException("Federate name was null or empty");
		}

		persistenceService.addFederateToGroup(federateName, groupName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFederatesInFEM(java.
	 * lang.String)
	 */
	@Override
	public List<Federate> getFederatesInFEM(final String femName)
			throws ATCloudException {

		if (femName == null) {
			throw new ATCloudException("FEM name argument was null.");
		}

		return persistenceService.getFederatesInFEM(femName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#removeActiveFEMForGroup
	 * (java.lang.String)
	 */
	@Override
	public boolean removeActiveFEMForGroup(String groupName)
			throws ATCloudException {

		if ((groupName == null) || (groupName.length() == 0)) {
			throw new ATCloudException("Group name was null or empty");
		}

		return persistenceService.removeActiveFEMForGroup(groupName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getFEMByID(java.lang.String
	 * )
	 */
	@Override
	public FEM getFEMByID(String id) throws ATCloudException {

		if ((id == null) || (id.length() == 0)) {
			throw new ATCloudException("ID was null or empty");
		}

		return persistenceService.getFEMbyID(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.fem.FederationExecutionModelService#getScenario(java.lang.String
	 * )
	 */
	@Override
	public Scenario getScenario(final String name) throws ATCloudException {

		if ((name == null) || (name.length() == 0)) {
			throw new ATCloudException("Scenario name was null or empty");
		}

		return persistenceService.getScenario(name);
	}

}
