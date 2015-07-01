/**
 * 
 */
package com.atcloud.license.internal;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.license.LicenseService;
import com.atcloud.model.License;
import com.atcloud.model.ModelService;
import com.atcloud.model.Organization;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@Produces(MediaType.APPLICATION_XML)
@WebService
public class LicenseServiceImpl implements LicenseService {

	private ModelService modelService;
	public PersistenceService persistenceService = null;

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(LicenseServiceImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#start()
	 */
	@Override
	@WebMethod(exclude = true)
	public void start() {
		LOG.debug("Starting license service...");

		LOG.info("Started license service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#stop()
	 */
	@WebMethod(exclude = true)
	@Override
	public void stop() {
		LOG.debug("Stopping license service...");

		LOG.info("Stopped license service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#isValidateLicense(java.lang.String)
	 */
	@Override
	public boolean isValidLicense(final String licenseKey)
			throws ATCloudException {

		boolean isValid = false;

		License license = getLicense(licenseKey);

		if (license == null) {
			LOG.warn("Unable to validate license using key [" + licenseKey + "]");
		} else {
			isValid = isLicenseActive(license.getLicenseID());
		}

		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.license.LicenseService#setLicenseAvailability(java.lang.String,
	 * boolean)
	 */
	@Override
	public void setLicenseAvailability(final String licenseKey,
			final boolean licenseAvailable) throws ATCloudException {

		LOG.debug("Setting license key [" + licenseKey + "] as "
				+ (licenseAvailable ? " available" : " unavailable"));

		License license = getLicense(licenseKey);

		if (license != null) {
			license.setInUse(licenseAvailable);
			updateLicense(license);

			LOG.info("License [" + license + "] has been made"
					+ (license.isInUse() ? " unavailable" : " available"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.license.LicenseService#setLicenseUnavailable(java.lang.String)
	 */
	@Override
	public void setLicenseUnavailable(String licenseKey) throws ATCloudException {
		setLicenseAvailability(licenseKey, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.license.LicenseService#setLicenseAvailable(java.lang.String)
	 */
	@Override
	public void setLicenseAvailable(String licenseKey) throws ATCloudException {
		setLicenseAvailability(licenseKey, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#isLicenseInUse(java.lang.String)
	 */
	@Override
	public boolean isLicenseInUse(final String licenseKey) {

		boolean licenseInUse = true;

		License license = getLicense(licenseKey);

		if (license != null) {
			licenseInUse = license.isInUse();
		}

		return licenseInUse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.user.UserService#addLicense(com.atcloud.model.Organization)
	 */
	@Override
	public License addLicenseToOrganization(final Organization organization)
			throws ATCloudException {

		if (organization == null) {
			throw new ATCloudException("Organization was null.");
		}

		if ((organization.getOrgName() == null)
				|| (organization.getOrgName().length() == 0)) {
			throw new ATCloudException("Organization name was null or empty.");
		}

		if (!persistenceService.organizationExists(organization.getOrgName())) {
			throw new ATCloudException(
					"Attempted to add a license for the organization ["
							+ organization.getOrgName() + "] which does not exist");
		}

		License license = persistenceService.createLicense();

		persistenceService.add(license);

		persistenceService.assignLicenseToOrganization(license.getLicenseID(),
				organization.getOrgID());

		return license;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#getLicense(java.lang.String)
	 */
	@Override
	public License getLicense(String licenseKey) {
		return persistenceService.getLicense(licenseKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#updateLicense(com.atcloud.model.License)
	 */
	@Override
	public void updateLicense(License license) throws ATCloudException {

		if (license == null) {
			throw new ATCloudException(
					"License parameter was null when attempting to update a license.");
		}

		persistenceService.updateLicense(license);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#getModelService()
	 */
	@WebMethod(exclude = true)
	@Override
	public ModelService getModelService() {
		return modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#setModelService(com.atcloud.model.
	 * ModelService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	/**
	 * @return the persistenceService
	 */
	@WebMethod(exclude = true)
	@Override
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	/**
	 * @param persistenceService
	 *          the persistenceService to set
	 */
	@WebMethod(exclude = true)
	@Override
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#activeLicense(java.lang.String)
	 */
	@Override
	public boolean activateLicense(final String licenseKey)
			throws ATCloudException {

		boolean activateStatus = false;

		if (licenseKey == null) {
			throw new ATCloudException(
					"License key parameter was null when attempting to activate a license.");
		}

		License licenseToUpdate = persistenceService.getLicense(licenseKey);

		if (licenseToUpdate == null) {
			LOG.warn("Unable to active license using key [" + licenseKey + "]");
		} else {

			licenseToUpdate.setActive(true);

			persistenceService.updateLicense(licenseToUpdate);

			activateStatus = true;

			LOG.info("License [" + licenseToUpdate + "] has been activated.");
		}

		return activateStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.user.UserService#isLicenseValid(java.lang.String)
	 */
	@Override
	public boolean isLicenseActive(final String licenseKey)
			throws ATCloudException {

		boolean activeStatus = false;

		if ((licenseKey == null) || (licenseKey.length() == 0)) {
			throw new ATCloudException(
					"License key was null or empty when attempting to determine if license was active.");
		}

		License license = persistenceService.getLicense(licenseKey);

		if (license == null) {
			LOG.warn("Unable to find license using key [" + licenseKey + "]");
		} else {

			activeStatus = license.isActive();
		}

		return activeStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#deactiveLicense(java.lang.String)
	 */
	@Override
	public boolean deactiveLicense(String licenseKey) throws ATCloudException {

		boolean deactivateStatus = false;

		if (licenseKey == null) {
			throw new ATCloudException(
					"License key parameter was null when attempting to deactivate a license.");
		}

		License licenseToUpdate = persistenceService.getLicense(licenseKey);

		if (licenseToUpdate == null) {
			LOG.warn("Unable to deactive license using key [" + licenseKey + "]");
		} else {

			licenseToUpdate.setActive(false);

			persistenceService.updateLicense(licenseToUpdate);

			deactivateStatus = true;

			LOG.info("License [" + licenseToUpdate + "] has been deactivated.");
		}

		return deactivateStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#addLicense()
	 */
	@Override
	public License addLicense(final Boolean activate) throws ATCloudException {

		License license = persistenceService.createLicense();

		license.setActive(activate);

		persistenceService.add(license);

		return license;
	}

}
