/**
 * 
 */
package com.atcloud.metrics;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.atcloud.commons.exception.ATCloudException;
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
@WebService
public interface MetricsService {

	/**
	 * 
	 */
	void start();

	/**
	 * 
	 */
	void stop();

	/**
	 * 
	 * @param simulation
	 * @param simulationMetrics
	 */
	@POST
	@Path("/recordSimulationMetrics")
			void
			recordSimulationMetrics(
					@WebParam(name = "simulation",
							targetNamespace = "http://model.atcloud.com/scenario") final Simulation simulation,
					@WebParam(name = "simulationMetrics",
							targetNamespace = "http://model.atcloud.com/scenario") final SimulationMetrics simulationMetrics);

	@POST
	@Path("/recordSimulationAirFlightPositionData")
			void
			recordSimulationData(
					@WebParam(name = "simulation",
							targetNamespace = "http://model.atcloud.com/scenario") final Simulation simulation,
					@WebParam(name = "airFlightPosition",
							targetNamespace = "http://model.atcloud.com/flight") final AirFlightPosition airFlightPosition)
					throws ATCloudException;

	@POST
	@Path("/addSimulation")
			void
			addSimulation(
					@WebParam(name = "simulation",
							targetNamespace = "http://model.atcloud.com/scenario") final Simulation simulation)
					throws ATCloudException;

	/**
	 * 
	 * @param persistenceService
	 */
	void setPersistenceService(PersistenceService persistenceService);

	/**
	 * 
	 * @return
	 */
	PersistenceService getPersistenceService();

	/**
	 * 
	 * @param modelService
	 */
	void setModelService(ModelService modelService);

}