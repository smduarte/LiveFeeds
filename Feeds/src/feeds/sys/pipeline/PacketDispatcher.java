package feeds.sys.pipeline;

import java.util.Hashtable;

import feeds.sys.FeedsNode;
import feeds.sys.core.Dispatcher;
import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;
import feeds.sys.tasks.Task;

public class PacketDispatcher implements Dispatcher {
    
    public PacketDispatcher() {
    }
    
    public void setDefaultHandler( Dispatcher dh ) {
        this.defaultHandler = dh ;
    }
    
    synchronized public void unregisterQueue( ID channel ) {
    	queues.remove( channel ) ;
    }
    
    synchronized public void registerQueue( ID channel, PacketQueue queue ) {
        queues.put( channel, queue ) ;
    }
    
    
    public void dispatch( cPacket p ) {
    	try {
    		if( ! p.isExpired() ) {  			
    			PacketQueue q = queues.get( p.channel ) ;
    		    if( q != null ) { q.enqueue(p) ; }
    		    else defaultHandler.dispatch( p ) ;
    		}
    	}        
    	catch( Exception x ) { 
    		x.printStackTrace();
    	}
    }
    
    private Hashtable<ID, PacketQueue> queues = new Hashtable<ID, PacketQueue>() ;
    
    private Dispatcher defaultHandler = new Dispatcher() {
        public void dispatch( final cPacket p ) {
        	new Task(0) {
        		public void run() {
        			try {
//        	        	feeds.api.Feeds.err.println("Null dispatcher...") ;
        				FeedsNode.plm().pipeline(p.channel) ;
        			} catch( Exception x ) {
        				x.printStackTrace() ;
        			}
        		}
        	} ;
        }
    };    
}

