package com.atcloud.userui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.commons.ServiceEndPointArgumentNames;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.user.UserService;

/**
 * 
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class AddGroupServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(AddGroupServlet.class.getName());

	private static final long serialVersionUID = -8444651625768440903L;
	private UserService userService;

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

		String groupName = req.getParameter(ServiceEndPointArgumentNames.GROUP_NAME
				.getArgName());
		String groupDescription = req
				.getParameter(ServiceEndPointArgumentNames.GROUP_DESCRIPTION
						.getArgName());
		String activeFEMName = req
				.getParameter(ServiceEndPointArgumentNames.FEM_NAME.getArgName());

		try {

			userService.addGroup(groupName, groupDescription, activeFEMName);

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
	 * @param userService
	 *          the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
