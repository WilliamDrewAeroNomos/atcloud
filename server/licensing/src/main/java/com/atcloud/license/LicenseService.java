/**
 * 
 */
package com.atcloud.license;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.model.License;
import com.atcloud.model.ModelService;
import com.atcloud.model.Organization;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@WebService
public interface LicenseService {

	/**
	 * 
	 */
	void start();

	/**
	 * 
	 */
	void stop();

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
	 * Checks for the validity of a {@link License} using a license key
	 * 
	 * @param licenseKey
	 *          Key of the license to be validiated
	 * @return True if the license is valid, else false if the license key does
	 *         not match a {@link License} in the system.
	 * 
	 * @throws ATCloudException
	 */
	@POST
	@Path("/isValidLicense")
	boolean
			isValidLicense(@WebParam(name = "licenseKey") final String licenseKey)
					throws ATCloudException;

	/**
	 * Set a {@link License} identified by <code>licenseKey</code> as available or
	 * unavailable depending on the value of <code>licenseAvailable</code>. If
	 * licenseAvailable is true, the license is set as available while false makes
	 * the license unavailable.
	 * 
	 * @param licenseKey
	 * @param licenseAvailable
	 * @throws ATCloudException
	 */
	@POST
	@Path("/setLicenseAvailability")
	void setLicenseAvailability(
			@WebParam(name = "licenseKey") final String licenseKey, @WebParam(
					name = "licenseAvailable") final boolean licenseAvailable)
			throws ATCloudException;

	/**
	 * Set the {@link License} identified by <code>licenseKey</code> as
	 * unavailable.
	 * 
	 * @param licenseKey
	 * @throws ATCloudException
	 */
	@POST
	@Path("/setLicenseUnavailable")
	void setLicenseUnavailable(
			@WebParam(name = "licenseKey") final String licenseKey)
			throws ATCloudException;

	/**
	 * Set the {@link License} identified by <code>licenseKey</code> as available.
	 * 
	 * @param licenseKey
	 * @throws ATCloudException
	 */
	@POST
	@Path("/setLicenseAvailable")
	void setLicenseAvailable(
			@WebParam(name = "licenseKey") final String licenseKey)
			throws ATCloudException;

	/**
	 * 
	 * @param organization
	 *          {@link Organization} for which to create and add the
	 *          {@link License}
	 * @return {@link License} License created and persisted for the Organization
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addLicenseToOrganization")
	@WebResult(name = "License",
			targetNamespace = "http://model.atcloud.com/user")
			License
			addLicenseToOrganization(
					@WebParam(name = "organization",
							targetNamespace = "http://model.atcloud.com/user") final Organization organization)
					throws ATCloudException;

	/**
	 * 
	 * @return {@link License} License created and persisted.
	 * @throws ATCloudException
	 */
	@POST
	@Path("/addLicense")
	@WebResult(name = "License",
			targetNamespace = "http://model.atcloud.com/user")
	License addLicense(@WebParam(name = "activate") final Boolean activate)
			throws ATCloudException;

	/**
	 * @param licenseKey
	 * @return
	 */
	@POST
	@Path("/getLicense")
	@WebResult(name = "License",
			targetNamespace = "http://model.atcloud.com/user")
	License getLicense(@WebParam(name = "licenseKey") final String licenseKey);

	/**
	 * @param license
	 * @throws ATCloudException
	 */
	@POST
	@Path("/updateLicense")
	void updateLicense(@WebParam(name = "license",
			targetNamespace = "http://model.atcloud.com/user") final License license)
			throws ATCloudException;

	/**
	 * 
	 * @param licenseKey
	 * @throws ATCloudException
	 */
	@POST
	@Path("/activeLicense")
	boolean
			activateLicense(@WebParam(name = "licenseKey") final String licenseKey)
					throws ATCloudException;

	/**
	 */
	@POST
	@Path("/isLicenseActive")
	boolean
			isLicenseActive(@WebParam(name = "licenseKey") final String licenseKey)
					throws ATCloudException;

	/**
	 * 
	 * @param licenseKey
	 * @throws ATCloudException
	 */
	@POST
	@Path("/deactiveLicense")
	boolean
			deactiveLicense(@WebParam(name = "licenseKey") final String licenseKey)
					throws ATCloudException;

	/**
	 * Checks if a {@link License} with the license key parameter is currently in
	 * use. "In Use" means that a user has logged onto the system with a valid
	 * License. The License will remain in use until the user logs off or the
	 * communication link with the user is broken.
	 * 
	 * @param licenseKey
	 *          Key of the license to be checked for availibility.
	 * @return True if the license is currently in use or false if the License is
	 *         available.
	 */
	@POST
	@Path("/isLicenseInUse")
	boolean
			isLicenseInUse(@WebParam(name = "licenseKey") final String licenseKey);

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

}