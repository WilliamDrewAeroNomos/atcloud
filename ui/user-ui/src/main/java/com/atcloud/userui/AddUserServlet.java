package com.atcloud.userui;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.AbstractHttpServlet;
import com.atcloud.commons.ServiceEndPointArgumentNames;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.model.Name;
import com.atcloud.model.User;
import com.atcloud.user.UserService;

/**
 * 
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public class AddUserServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory.getLogger(AddUserServlet.class
			.getName());

	private static final long serialVersionUID = -8444651625768440903L;
	private UserService userService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String firstName = req.getParameter(ServiceEndPointArgumentNames.FIRST_NAME
				.getArgName());
		String middleName = req
				.getParameter(ServiceEndPointArgumentNames.MIDDLE_NAME.getArgName());
		String lastName = req.getParameter(ServiceEndPointArgumentNames.LAST_NAME
				.getArgName());

		User user = new User();

		user.setUserId(UUID.randomUUID().toString());

		user.setActive(true);

		Calendar calendar = Calendar.getInstance();

		user.setDateActivated(calendar);
		user.setDateCreated(calendar);

		Name name = new Name();

		name.setFirst(firstName);
		name.setMiddle(middleName);
		name.setLast(lastName);

		user.setName(name);

		try {
			userService.addUser(user);
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
	 * @param userService
	 *          the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
