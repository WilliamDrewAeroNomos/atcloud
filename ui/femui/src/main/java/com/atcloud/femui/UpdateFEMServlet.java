package com.atcloud.femui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.model.FEM;
import com.atcloud.model.ModelService;

/**
 * 
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class UpdateFEMServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(UpdateFEMServlet.class.getName());

	private static final long serialVersionUID = -8444651625768440903L;
	private FederationExecutionModelService femService;
	private ModelService modelService;

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

		try {

			FEM fem = (FEM) modelService.getObjectFromXML(getRequestContent(req));

			femService.updateFEM(fem);

			ServletOutputStream os = resp.getOutputStream();

			os.println(String.format("%s", fem.toString()));

		} catch (JAXBException e) {
			setServletErrorResponse(e.getLinkedException(), resp);
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

	/**
	 * @return the modelService
	 */
	public ModelService getModelService() {
		return modelService;
	}

	/**
	 * @param modelService
	 *          the modelService to set
	 */
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}
