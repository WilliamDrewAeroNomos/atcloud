package com.atcloud.femui;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.fem.FederationExecutionModelService;
import com.atcloud.model.FEM;
import com.atcloud.model.FEMs;

/**
 * 
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class GetAllFEMsServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(GetAllFEMsServlet.class.getName());

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

		ServletOutputStream os = resp.getOutputStream();

		try {

			List<FEM> fems = femService.getAllFEMs();

			FEMs femList = new FEMs();

			femList.setFems(fems);

			os.println(String.format("%s", femList.toString()));

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
