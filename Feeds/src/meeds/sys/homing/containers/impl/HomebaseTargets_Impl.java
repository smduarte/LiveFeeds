package meeds.sys.homing.containers.impl;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import meeds.sys.homing.containers.*;

public class HomebaseTargets_Impl extends Container<HomebaseTargets_Impl> implements HomebaseTargets, HomebaseTargets.Updater {
    
    public HomebaseTargets_Impl() {
    	init() ;
    }
    
    private void init() {
        if( ! FeedsNode.isPnode() ) {
            String bindingTargets = (String) FeedsRegistry.get("HomebaseURLs") ;
            if( bindingTargets == null ) bindingTargets = "tcp://192.168.158.1:20000;" ;

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