package feeds.sys.core;

import static feeds.sys.core.NodeType.cNODE;
import feeds.sys.binding.Binding;
import feeds.sys.directory.ChannelDirectory;
import feeds.sys.membership.MembershipService;
import feeds.sys.pipeline.PipelineManager;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.registry.RegistryService;
import feeds.sys.tasks.TaskScheduler;
import feeds.sys.transports.Transports;

public class ClientNodeContext extends NodeContext {
	 
	public ClientNodeContext() {
		super( new ID() ) ;
		type = cNODE ;
		isCnode = true ;

		tf = new Transports() ;
		reg = new NodeRegistry() ;
		plm = new PipelineManager() ;
		dir = new ChannelDirectory() ;

		scheduler = new TaskScheduler() ;
		scheduler.start() ;

		isServer = isSnode = isPnode = false ;		
	}
	
	synchronized public void init() {
		reg.init();
		tf.init();

		Binding.start() ;

		RegistryService.start() ;
		MembershipService.start();
	}
	

}
