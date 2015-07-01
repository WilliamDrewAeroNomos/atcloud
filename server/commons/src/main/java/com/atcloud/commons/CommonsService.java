/**
 * 
 */
package com.atcloud.commons;

import javax.naming.spi.InitialContextFactoryBuilder;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
public interface CommonsService {

	/**
	 * 
	 */
	void start();

	/**
	 * 
	 */
	void stop();

	/**
	 * Checks if a particular port is available
	 * 
	 * @param port
	 * @return true if the port is available
	 */
	boolean portAvailable(int port);

	/**
	 * Finds an available port between the minimum and maximum port numbers
	 * provided. -1 is returned if a port is not available in the given range.
	 * 
	 * @param minPort
	 * @param maxPort
	 * @return
	 */
	int findAvailablePort(final int minPort, final int maxPort);

	/**
	 * Return the {@link InitialContextFactoryBuilder} used by the numerous
	 * presistence services
	 * 
	 * @return
	 */
	InitialContextFactoryBuilder getInitialContextFactoryBuilder();

	/**
	 * Returns the HTTP header name for error description
	 * 
	 * @return
	 */
	String getHttpErrorMessageHeader();

}