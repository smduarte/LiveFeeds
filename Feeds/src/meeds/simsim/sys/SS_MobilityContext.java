package meeds.simsim.sys;

import simsim.core.*;

import feeds.sys.core.*;
import feeds.sys.directory.*;
import feeds.sys.registry.NodeRegistry;

import meeds.sys.core.*;
import meeds.sys.directory.*;

public abstract class SS_MobilityContext extends MobilityContext {
    
    public SS_MobilityContext( ID v, NodeType t ) {
    	super(v) ;
        type = t ;
        rnd = simsim.core.Simulation.rg ;
        this.makeCurrent() ;
    }
    
	public void init() {
    		makeCurrent() ;    		
            tf = SS_Transports.factory() ;
            
    		plm = new SS_PipelineManager() ;
    		dir = new ChannelDirectory() ;
    		
    		MeedsDirectoryStorage.init() ;
	}        
    
	public void sleep( double s ) {
		new feeds.sys.tasks.Task( s ) {
			public void run() {
				this.release() ;
			}			
		}.block();
	}
	
	public double time() {
		return Simulation.currentTime() ;
	}
	
    public Transport soq() {
        return getTransport("soq://-/-/", "outgoing") ;
    }
    
    public Transport soq( ID channel ) {
        return getTransport("soq://" + channel + "/-/", "outgoing") ;
    }
    
    public Transport getTransport( String url, String mode ) {
        return tf.openTransport( url,  mode ).open() ;
    }
    
    public Thread newThread( boolean daemon, Runnable r ) {
    	Thread.dumpStack() ;
    	return null ;
	}
}

class SS_ProxyNodeContext extends SS_MobilityContext {

	public SS_ProxyNodeContext( ID v) {
    	super( v, NodeType.cNODE ) ;
        reg = new NodeRegistry().init() ;  
        isMnode = false ;
        isCnode = true ;
    }    	
}

class SS_hNodeContext extends SS_MobilityContext {

	public SS_hNodeContext( ID v) {
    	super( v, NodeType.cNODE ) ;
        reg = new NodeRegistry().init() ;     
        isMnode = false ;
        isCnode = true ;
    }    	
}