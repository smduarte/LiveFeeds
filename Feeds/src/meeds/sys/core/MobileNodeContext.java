package meeds.sys.core;

import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.directory.ChannelDirectory;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.tasks.*;

import meeds.sys.homing.*;
import meeds.sys.pipeline.PipelineManager;
import meeds.sys.registry.MeedsRegistryService;
import meeds.sys.transports.MeedsTransports;
import meeds.sys.directory.*;

public class MobileNodeContext extends MobilityContext {
	 
	public MobileNodeContext() {
		this( new ID() ) ;
	}

	protected MobileNodeContext( ID id ) {
		super(id) ;
		type = NodeType.mNODE ;
		isMnode = true ;
		isCnode = false ;
	}

	
	public void init() {
		tf = new MeedsTransports() ;
		reg = new NodeRegistry() ;
		plm = new PipelineManager() ;
		dir = new ChannelDirectory() ;

		scheduler = new TaskScheduler() ;
		scheduler.start() ;

		tf.init();       
        MeedsDirectoryStorage.init() ;
		MeedsRegistryService.start();
        
        FeedsRegistry.put("DataURL", "nat://-/-") ;
        FeedsRegistry.put("HomebaseURLs", "tcp://-:20000") ;
        Homing.start() ;
	}	
}
