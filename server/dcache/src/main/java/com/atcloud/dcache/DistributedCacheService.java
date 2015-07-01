/**
 * 
 */
package com.atcloud.dcache;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public interface DistributedCacheService {

	/**
	 * Called by the container when the service is started or refreshed.
	 * 
	 * @throws ATCloudException
	 */
	void start() throws ATCloudException;

	/**
	 * Called by the container when the service is stopped or restarted.
	 * 
	 * @throws ATCloudException
	 */
	void stop() throws ATCloudException;

	/**
	 * @param commonsService
	 *          the commonsService to set
	 */
	public void setCommonsService(CommonsService commonsService);

	/**
	 * @return the commonsService
	 */
	public CommonsService getCommonsService();

}
