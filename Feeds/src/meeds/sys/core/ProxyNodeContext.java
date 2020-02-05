package meeds.sys.core;

import meeds.sys.MeedsNode;
import meeds.sys.directory.MeedsDirectoryStorage;
import meeds.sys.homing.HomingService;
import meeds.sys.homing.Position;
import meeds.sys.homing.containers.Location;
import meeds.sys.pipeline.PipelineManager;
import meeds.sys.proxying.ProxyBindingService;
import meeds.sys.proxying.ProxyDiscoveryService;
import meeds.sys.registry.MeedsRegistryService;
import meeds.sys.transports.MeedsTransports;
import meeds.sys.tunnel.TunnelService;
import feeds.api.Feeds;
import feeds.sys.FeedsRegistry;
import feeds.sys.binding.Binding;
import feeds.sys.core.Container;
import feeds.sys.core.NodeType;
import feeds.sys.directory.ChannelDirectory;
import feeds.sys.membership.MembershipService;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.registry.RegistryService;
import feeds.sys.tasks.TaskScheduler;

public class ProxyNodeContext extends MobilityContext {
	 
	public ProxyNodeContext() {
		type = NodeType.cNODE ;
		isCnode = true ;
		isMnode = false ;
	}
	
	public void init() {
		tf = new MeedsTransports() ;
		reg = new NodeRegistry().init() ;
		plm = new PipelineManager() ;
		dir = new ChannelDirectory() ;

		Feeds.err.println( MeedsNode.isMnode() ) ;
		
		scheduler = new TaskScheduler() ;
		scheduler.start() ;

		tf.init();
       
        Binding.start() ;
        RegistryService.start() ;
		MembershipService.start();   

		MeedsDirectoryStorage.init() ;
		MeedsRegistryService.start();
        
      	FeedsRegistry.put("/Local/System/Meeds/Homebase/Acceptors", "tcp:/-:20000/-" ) ;            	        	
		FeedsRegistry.put("/Local/System/Meeds/Proxying/Acceptors", "tcp:/-:20000/-");

        HomingService.start() ;            
        
		Location.Updater locu = Container.byClass(Location.class);
		locu.set(new Position(new meeds.sys.util.XY(0, 0)));

		ProxyDiscoveryService.start();
		ProxyBindingService.start();
		TunnelService.start();
	}	
}
