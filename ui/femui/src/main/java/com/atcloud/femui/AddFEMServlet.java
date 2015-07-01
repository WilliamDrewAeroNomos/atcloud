package com.atcloud.femui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.commons.ServiceEndPointArgumentNames;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.model.FEM;

/**
 * 
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class AddFEMServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory.getLogger(AddFEMServlet.class
			.getName());

	private static final long serialVersionUID = -8444651625768440903L;
	private FederationExecutionModelService femService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String femName = req.getParameter(ServiceEndPointArgumentNames.FEM_NAME
				.getArgName());
		String femDescription = req
				.getParameter(ServiceEndPointArgumentNames.FEM_DESCRIPTION.getArgName());

		try {

			FEM fem = femService.createFEM(femName);

			fem.setDescription(femDescription);

			femService.addFEM(fem);

			ServletOutputStream os = resp.getOutputStream();

			os.println(String.format("%s", fem.toString()));

		} catch (ATCloudException e) {
			setServletErrorResponse(e, resp);
		} catch (javax.persistence.PersistenceException pe) {
			setServletErrorResponse(pe, resp);
		} catch (IllegalArgumentException iae) {
			setServletErrorResponse(iae, resp);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
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
