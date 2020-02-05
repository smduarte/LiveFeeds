package sensing.core;
import sensing.core.query.*;
import sensing.core.network.NetworkService;
import sensing.core.scheduler.SchedulerService
import sensing.core.sensors.SensorService;
import sensing.core.vtable.VTableService;
import sensing.core.monitoring.MonitoringService;
import sensing.core.logging.*;

public class ServiceManager {
	final ServicesConfig config;

	NetworkService		network;
	QueryService		query;
	VTableService		vtable;
	SchedulerService	scheduler;
	SensorService		sensor;
	Random				random;
	LoggingProvider		logging;
	QueryInterface		queryInterface;
	MonitoringService	monitoring;
	
	List services;
	

	public ServiceManager(ServicesConfig config) {
		this.config = config;

		// Network service
		network = new NetworkService(this);
		// Query service
		query = QueryService.newQueryService(this, config.queryImplPolicy);	
		// VTable service
		vtable = new VTableService(this);
		// Scheduler service
		scheduler  = new SchedulerService(this);
		// Sensor service
		sensor = new SensorService(this);
		
		services = [network, query, vtable, scheduler, sensor];
		
		// Random generator
		random = config.rand;
		// Logging service
		logging = config.loggingImpl;
		
		// Querying interface
		queryInterface = new QueryInterface(this);

		// Monitoring service
		if(config.monitoring) {
			monitoring	= new MonitoringService(this);
			services << monitoring;
		}
		
	}
	
	public void init() {
		services*.init()
	}
	
	public void start() {
		services*.start();
	}


}
