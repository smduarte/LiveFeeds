package feeds.sys.core;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.tasks.*;
import feeds.sys.binding.*;
import feeds.sys.pipeline.*;
import feeds.sys.registry.*;
import feeds.sys.backbone.*;
import feeds.sys.directory.*;
import feeds.sys.transports.*;
import feeds.sys.membership.*;
import static feeds.sys.core.NodeType.*;

public class ServerNodeContext extends NodeContext {
    
    public ServerNodeContext( NodeRegistry reg ) {
    	super( (ID)reg.get("/Local/System/Identity/ID") ) ;
        isCnode = false ;
        isServer = true ;
        this.reg = reg.init() ;
    }
    
    synchronized public void init() {
        
		plm = new PipelineManager() ;
		dir = new ChannelDirectory() ;
        
        name = FeedsRegistry.get("/Local/System/Identity/Name") ;
        isPnode = FeedsRegistry.get("/Local/System/Identity/Master").equals("true") ;
        
        isSnode = ! isPnode ;
        type = isPnode ? pNODE : sNODE ;

        Feeds.out.println("I am " + name + "/" + id + " [" + (isPnode ? "master" : "slave") + "].") ;

		scheduler = new TaskScheduler() ;
		scheduler.start() ;

        tf = new Transports().init() ;
        
        RegistryService.start() ;
        MembershipService.start() ;        
        Binding.start() ;        
		BackboneServices.start();
        BindingService.start() ;            
    }
        
	private String name ;
}
