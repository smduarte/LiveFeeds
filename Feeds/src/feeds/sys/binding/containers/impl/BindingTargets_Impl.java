package feeds.sys.binding.containers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.binding.containers.BindingTargets;
import feeds.sys.core.Container;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;

public class BindingTargets_Impl extends Container<BindingTargets_Impl> implements BindingTargets, BindingTargets.Updater {
    
    public BindingTargets_Impl() {
    	init() ;
    }
    
    private void init() {
        if( ! FeedsNode.isPnode() ) {
            String bindingTargets = (String) FeedsRegistry.get("BindingURLs") ;
            if( bindingTargets == null ) bindingTargets = "tcp://-:39999;" ;

            Scanner s = new Scanner( bindingTargets ).useDelimiter(";") ;
            while( s.hasNext() )
            	addTransport( s.next() ) ;      
        }
        super.notifyUpdateNow() ;
    }
    
    private void addTransport( String url ) {
        if( ! servers.containsKey( url ) ) {
            Transport t = FeedsNode.openTransport( url, "outgoing") ;
            servers.put( url, t ) ;
            //Feeds.out.println("Added tentative server at: [" + t.url() + "]" ) ;
        }
    }
    
    public void put( String url ) {
        addTransport( url ) ;
        super.notifyUpdate() ;
    }
    
	public Map<String, Transport> tmap() {
		return Collections.unmodifiableMap( servers ) ;
	}

	
	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	public Collection<Transport> servers() {
		return Collections.unmodifiableCollection( servers.values() ) ;
	}

	private final Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
	private final Map<String, Transport> servers = new HashMap<String, Transport>() ;
}