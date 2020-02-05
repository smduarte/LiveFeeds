package feeds.simsim.sys;

import simsim.core.*;

import feeds.sys.core.*;
import feeds.sys.directory.*;
import feeds.sys.registry.NodeRegistry;

public abstract class SS_NodeContext extends NodeContext {
    
    public SS_NodeContext( ID v, NodeType t ) {
    	super(v) ;
        type = t ;
        this.makeCurrent() ;
    }
    
	public void init() {
    		this.makeCurrent() ;    		
            tf = SS_Transports.factory() ;
            
    		plm = new SS_PipelineManager() ;
    		dir = new ChannelDirectory() ;
    		
    		DirectoryStorage.init() ;
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
    
    static {
    	NodeContext.rnd = Simulation.rg ;
    }
}

class SS_cNodeContext extends SS_NodeContext {

	public SS_cNodeContext( ID v) {
    	super( v, NodeType.cNODE ) ;
        isServer = isPnode = isSnode = false ;
        isCnode = true ;
        reg = new NodeRegistry().init() ;            
    }    	
}

class SS_sNodeContext extends SS_NodeContext {    
    public SS_sNodeContext( ID v) {
    	super( v, NodeType.sNODE ) ;
        isSnode = isServer = true ;
        isCnode = isPnode = false ;
        reg = new NodeRegistry(true).init() ;            
    }
}

class SS_pNodeContext extends SS_NodeContext {
    
    public SS_pNodeContext(ID v) {
    	super(v , NodeType.pNODE ) ;
        isPnode = isServer = true ;
        isCnode = isSnode = false ;
        reg = new NodeRegistry(true).init() ;            
    }
    	
    public Transport soq() {
        return null ;
    }
    
    public Transport soq( ID channel ) {
        return null ;
    }    
}