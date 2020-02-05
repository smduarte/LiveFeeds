package feeds.sys.binding.containers.impl;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.binding.* ;
import feeds.sys.binding.containers.*;

public class BoundServers_Impl extends Container<BoundServers> implements BoundServers, BoundServers.Updater {
    
    
    public BoundServers_Impl() {
    	super(10) ;
    	super.notifyUpdate() ;
    }
    
	public List<Transport> sortedTransports() {
		return Collections.unmodifiableList( sortedServers() ) ;
	}

	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	
    public void put( BindingReply s ) {
        updateTransport( s ) ;
        servers.put( s.src(), s ) ;  
        super.notifyUpdateNow() ;
    }
    
    private Transport updateTransport( BindingReply r ) {
        BindingReply o = servers.get( r.src() ) ;
        try {
            if( o != null && ! r.urls().equals( o.urls() ) ) {
                Transport t = transports.get( r.src() ) ;
                t.dispose() ;
            }
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
        
        Transport t = transports.get( r.src() ) ;
        try {
            if( t == null ) {
            	Scanner s = new Scanner( r.urls() ).useDelimiter(";") ;
            	t = FeedsNode.openTransport( s.next(), "outgoing") ;
                transports.put( r.src(), t ) ;
            }
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
        return t ;
    }

    
    private List<Transport> sortedServers() {
        List<Transport> v = new ArrayList<Transport>() ;

        for( BindingReply i : new TreeSet<BindingReply>( servers.values() ) ) {
            v.add( transports.get( i.src() ) ) ;
        }     
        return v ;
    }
        
    private Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
    private Map<ID, BindingReply> servers = new HashMap<ID, BindingReply>() ;
}