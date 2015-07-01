package com.atcloud.femui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.commons.ServiceEndPointArgumentNames;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.fem.FederationExecutionModelService;

/**
 * HTTP servlet for adding a federate to the system.
 * 
 * URL is http://{hostname}:8181//addfederateui
 * 
 * doPost() method takes a federateName
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class AddFederateServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(AddFederateServlet.class.getName());

	private static final long serialVersionUID = -8444651625768440903L;
	private FederationExecutionModelService femService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String federateName = req
				.getParameter(ServiceEndPointArgumentNames.FEDERATE_NAME.getArgName());
		String femDescription = req
				.getParameter(ServiceEndPointArgumentNames.FEDERATE_DESCRIPTION
						.getArgName());

		try {

			femService.addFederate(federateName, femDescription);

		} catch (ATCloudException e) {
			setServletErrorResponse(e, resp);
		} catch (javax.persistence.PersistenceException pe) {
			setServletErrorResponse(pe, resp);
		} catch (IllegalArgumentException iae) {
			setServletErrorResponse(iae, resp);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req, resp);
	}

	/**
	 * @return the femService
	 */
	public FederationExecutionModelService getFemService() {
		return femService;
	}

	/**
	 * @param femService
	 *          the femService to set
	 */
	public void setFemService(FederationExecutionModelService femService) {
		this.femService = femService;
	}

}
