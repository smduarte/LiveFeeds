package feeds.sys;

import java.util.*;
import feeds.api.* ;

import feeds.sys.core.* ;
import feeds.sys.binding.*;
import feeds.sys.pipeline.* ;
import feeds.sys.registry.* ;
import feeds.sys.catadupa.*;
import static feeds.sys.core.NodeContext.context; ;

public class FeedsNode {

	public static NodeType type() {
		return context.type() ;
	}
		
	public static boolean isCnode() {
        return context.isCnode ;
    }

    public static boolean isSnode() {
        return context.isSnode ;
    }

    public static boolean isPnode() {
        return context.isPnode ;
    }

    public static boolean isServer() {
        return context.isServer ;
    }
    
	public static void exit() {
    	context.exit() ;
    }
    
    public static double time() {
    	return context.time() ;
    }
    
    public static void sleep( double s ) {
    	context.sleep( s ) ;
    }
    
    public static String ipAddress() {
    	return context.ipAddress() ;
    }
    
    public static Directory dir() {
    	return context.dir ;
    }
    
    public static NodeRegistry reg() {
    	return context.reg ;
    }
    
    public static PipelineManager plm() {
    	return context.plm ;
    }
    
    public static Thread newThread( boolean daemon, Runnable r ) {
    	return context.newThread( daemon, r ) ;
    }
    
    public static Transport openTransport(String url, String mode ) {
    	return context.tf.openTransport( url, mode ) ;
    }
    
	static public ID id() {
		return context.id ;
	}
	
	static public boolean done() {
		return false ;
	}
	
	static public Random rnd() {
		return NodeContext.rnd ;
	}
	
	synchronized static public Channel<Void, BindingRequest, Void, BindingReply> bc() {
		if( context.bc == null ) {
	        context.bc = Feeds.lookup("/System/BindingChannel") ;        
		}
		return context.bc;
	}
	
	synchronized static public Channel<String, Void, Void, RegistryItem> rqc() {
		if( context.rqc == null ) {
	        context.rqc = Feeds.lookup("/System/RegistryQueryChannel") ;        			
		}
		return context.rqc;
	}
	
	synchronized static public Channel<String, RegistryItem, Void, Void> rrc() {
		if( context.rrc == null ) {
	        context.rrc = Feeds.lookup("/System/RegistryReplicationChannel") ;        			
		}
		return context.rrc;
	}
	
	synchronized static public Channel<Character, SubscriptionData, Void, Void> mc() {
		if( context.mc == null ) {
	        context.mc = Feeds.lookup("/System/MembershipChannel") ;        						
		}
		return context.mc;
	}	
}
