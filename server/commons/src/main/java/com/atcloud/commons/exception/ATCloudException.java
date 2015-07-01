/**
 * 
 */
package com.atcloud.commons.exception;

/**
 *
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class ATCloudException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5050246398302099134L;

	/**
	 * 
	 */
	public ATCloudException() {
	
	}

	/**
	 * @param message
	 */
	public ATCloudException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ATCloudException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ATCloudException(String message, Throwable cause) {
		super(message, cause);
	}

}
