package feeds.sys.transports.containers.impl;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.transports.*;
import feeds.sys.transports.containers.*;

public class OutgoingTransports_Impl extends Container<OutgoingTransports> implements OutgoingTransports, OutgoingTransports.Updater {
    
    public OutgoingTransports_Impl() {
    	super.notifyUpdate() ;
    }
    
    public Transport get( ID node ) {
        return transports.get( node ) ;
    }
    
	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	
    public Transport put( ID dst, String url ) {
        Transport t = FeedsNode.openTransport( url, "outgoing" ) ;
        Transport o = transports.get( dst ) ;
        
        if( o == null || ! t.url().equals( o.url() ) ) {
            transports.put( dst, t ) ;
            
            try { t.open() ; } catch( Exception x ) {}
            try { o.dispose() ; } catch( Exception x ) {}
            return t ;
        }
        else return o ;    
    }
    

    public Transport put( String url ) {
       return this.put( new Url( url).fid(), url ) ;
    }
    
    
    public void remove( ID node ) {
        Transport t = (Transport)transports.get( node ) ;
        if( t != null ) {
            transports.remove( node ) ;
            try { t.dispose() ; } catch( Exception x ) {}
        }
    }
     
    private Hashtable<ID, Transport> transports = new Hashtable<ID, Transport>() ;

}