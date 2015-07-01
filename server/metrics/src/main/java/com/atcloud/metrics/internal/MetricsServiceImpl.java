/**
 * 
 */
package com.atcloud.metrics.internal;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atcloud.commons.exception.ATCloudException;
import com.atcloud.metrics.MetricsService;
import com.atcloud.model.AirFlightPosition;
import com.atcloud.model.ModelService;
import com.atcloud.model.Simulation;
import com.atcloud.model.SimulationMetrics;
import com.atcloud.persistence.PersistenceService;

/**
 * 
 * @author <a href=mailto:support@atcloud.com>support</a>
 * @version $Revision: $
 */
@Produces(MediaType.APPLICATION_XML)
@WebService
public class MetricsServiceImpl implements MetricsService {

	@SuppressWarnings("unused")
	private ModelService modelService;
	private PersistenceService persistenceService;

	/**
	 * 
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(MetricsServiceImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#start()
	 */
	@Override
	@WebMethod(exclude = true)
	public void start() {
		LOG.debug("Starting metrics service...");

		LOG.info("Started metrics service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.license.LicenseService#stop()
	 */
	@WebMethod(exclude = true)
	@Override
	public void stop() {
		LOG.debug("Stopping metrics service...");

		LOG.info("Stopped metrics service.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.metrics.MetricsService#setModelService(com.atcloud.model.
	 * ModelService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.metrics.MetricsService#add(com.atcloud.model.ModelBase)
	 */

	@Override
	public void recordSimulationMetrics(final Simulation simulation,
			final SimulationMetrics simulationMetrics) {

		LOG.debug("Recording simulation [" + simulation + "] with ["
				+ simulationMetrics + "] metrics.");

		if (simulation == null) {
			throw new IllegalArgumentException("Simulation parameter was null.");
		}

		if (simulationMetrics == null) {
			throw new IllegalArgumentException(
					"Simulation metrics parameter was null.");
		}

		persistenceService.recordSimulationExecutionMetrics(simulation,
				simulationMetrics);
	}

	@Override
	public void recordSimulationData(final Simulation simulation,
			final AirFlightPosition airFlightPosition) throws ATCloudException {

		if (simulation == null) {
			throw new ATCloudException("Simulation was null.");
		}

		if (airFlightPosition == null) {
			throw new ATCloudException("AirFlightPosition was null.");
		}

		persistenceService.recordSimulationData(simulation, airFlightPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.atcloud.metrics.MetricsService#addSimulation(com.atcloud.model.Simulation
	 * )
	 */
	@Override
	public void addSimulation(Simulation simulation) throws ATCloudException {

		LOG.debug("Adding simulation [" + simulation + "]...");

		if (simulation == null) {
			throw new IllegalArgumentException("Simulation parameter was null.");
		}

		persistenceService.addSimulation(simulation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.metrics.Service#getPersistenceService()
	 */
	@WebMethod(exclude = true)
	@Override
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.atcloud.metrics.MetricsService#setPersistenceService(
	 * PersistenceService)
	 */
	@WebMethod(exclude = true)
	@Override
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
}
