/**
 * 
 */
package com.atcloud.dcache.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.dcache.DistributedCacheService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class DistributedCacheServiceImpl implements DistributedCacheService {

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(DistributedCacheServiceImpl.class.getName());

	public CommonsService commonsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.dcache.DistributedCacheService#start()
	 */
	@Override
	public void start() throws ATCloudException {

		LOG.debug("Starting distributed cache service...");

		LOG.info("Started distributed cache service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.dcache.DistributedCacheService#stop()
	 */
	@Override
	public void stop() throws ATCloudException {

		LOG.debug("Stopping distributed cache service...");

		LOG.info("Stopped distributed cache service.");
	}

	/**
	 * @param commonsService
	 *          the commonsService to set
	 */
	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/**
	 * @return the commonsService
	 */
	public CommonsService getCommonsService() {
		return commonsService;
	}

}
