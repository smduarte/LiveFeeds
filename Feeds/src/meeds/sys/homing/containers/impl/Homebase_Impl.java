package meeds.sys.homing.containers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import meeds.sys.homing.HomingReply;
import meeds.sys.homing.containers.Homebase;
import feeds.sys.FeedsNode;
import feeds.sys.core.Container;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;

public class Homebase_Impl extends Container<Homebase> implements Homebase, Homebase.Updater {
    
    
    public Homebase_Impl() {
    	super.notifyUpdate() ;
    }
    
	public List<Transport> sortedTransports() {
		return Collections.unmodifiableList( sortedServers() ) ;
	}

	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	
    public void put( HomingReply s ) {
        updateTransport( s ) ;
    }
    
    private Transport updateTransport( HomingReply r ) {
    	HomingReply o = servers.get( r.src() ) ;
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
                notifyUpdateNow() ;            
            }
            servers.put( r.src(), r ) ;  
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
        return t ;
    }

    
    private List<Transport> sortedServers() {
        List<Transport> v = new ArrayList<Transport>() ;

        for( HomingReply i : new TreeSet<HomingReply>( servers.values() ) ) {
            v.add( transports.get( i.src() ) ) ;
        }     
        return v ;
    }
        
    private Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
    private Map<ID, HomingReply> servers = new HashMap<ID, HomingReply>() ;
}