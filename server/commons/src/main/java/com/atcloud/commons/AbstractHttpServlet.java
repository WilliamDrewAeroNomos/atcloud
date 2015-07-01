/**
 * 
 */
package com.atcloud.commons;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public abstract class AbstractHttpServlet extends HttpServlet {

	/**
	 * 
	 */
	public static final Logger LOG = LoggerFactory
			.getLogger(AbstractHttpServlet.class.getName());

	public static final String UTF8_BOM = "\uFEFF";

	private static final long serialVersionUID = 7927950209863831499L;

	protected CommonsService commonsService;

	/**
	 * @return the commonsService
	 */
	public CommonsService getCommonsService() {
		return commonsService;
	}

	/**
	 * @param commonsService
	 *          the commonsService to set
	 */
	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/**
	 * @param pe
	 * @param resp
	 */
	protected void setServletErrorResponse(final Throwable e,
			HttpServletResponse resp) {

		if (resp != null) {

			if (e != null) {

				LOG.error(e.getLocalizedMessage(), e);

				if (!resp.containsHeader(commonsService.getHttpErrorMessageHeader())) {
					resp.addHeader(commonsService.getHttpErrorMessageHeader(),
							e.getLocalizedMessage());
				}

			}

		}

	}

	/**
	 * Returns the contents of the HTTPServletRequest as a String
	 * 
	 * @param req
	 *          {@link HttpServletRequest} received in a doGet() or doPut()
	 * @return Contents of the request body
	 * @throws IOException
	 */
	protected String getRequestContent(final HttpServletRequest req)
			throws IOException {

		StringBuffer result = new StringBuffer();

		if (req != null) {

			BufferedReader in = req.getReader();

			String line = new String();
			boolean firstLine = true;

			while ((line = in.readLine()) != null) {

				if (firstLine) {
					line = removeUTF8BOM(line);
					firstLine = false;
				}

				if ((line != null) && (line.length() > 0)) {
					result.append(line);
				}

			}
		}

		return result.toString();
	}

	private static String removeUTF8BOM(String s) {
		if (s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}
}
