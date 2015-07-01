/**
 * 
 */
package com.atcloud.commons.internal;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.CommonsService;
import com.atcloud.commons.MyInitialContextFactory;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */

public class CommonsServiceImpl implements CommonsService {

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(CommonsServiceImpl.class.getName());

	private final String httpErrorMessageHeader = "com.atcloud.http.error.message";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.commons.CommonsService#start()
	 */
	public void start() {

		LOG.debug("Starting commons service...");

		LOG.info("Started commons service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.commons.CommonsService#stop()
	 */
	public void stop() {

		LOG.debug("Stopping commons service...");

		LOG.info("Stopped commons service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.commons.CommonsService#findAvailablePort(int, int)
	 */
	public int findAvailablePort(final int minPort, final int maxPort) {

		if (maxPort < minPort) {
			throw new IllegalArgumentException(
					"Invalid port range provided. Minimum port was greater than the maximum port.");
		}

		int availablePort = -1;

		for (int x = minPort; x <= maxPort; x++) {
			if (portAvailable(x)) {
				availablePort = x;
				LOG.debug("Found port [" + availablePort + "] available.");
				break;
			}
		}

		return availablePort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.commons.CommonsService#portAvailable(int)
	 */
	public boolean portAvailable(int port) {

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.commons.CommonsService#getInitContextFactoryBuilder()
	 */
	@Override
	public InitialContextFactoryBuilder getInitialContextFactoryBuilder() {
		return new MyInitialcontextFactoryBuilder();
	}

	/**
	 * 
	 * @author <a href=mailto:support@atcloud.com>support</a>
	 * @version $Revision: $
	 */
	public class MyInitialcontextFactoryBuilder implements
			InitialContextFactoryBuilder {
		@Override
		public InitialContextFactory createInitialContextFactory(
				Hashtable<?, ?> environment) throws NamingException {
			return new MyInitialContextFactory();
		}
	}

	/**
	 * @return the httpErrorMessageHeader
	 */
	@Override
	public String getHttpErrorMessageHeader() {
		return httpErrorMessageHeader;
	}

}
