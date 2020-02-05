package sensing.persistence.core.scheduler;

import groovy.lang.Closure;
import sensing.persistence.core.*;

/*
 * Not currently used, SchedulerProvider implementation is directly used by ServiceManager
 */


public class SchedulerService extends Service {
	@Delegate SchedulerProvider schedulerImpl;
	
	public SchedulerService(ServiceManager services) {
		super(services);
		this.schedulerImpl = services.config.schedulerImpl;
	}

}
