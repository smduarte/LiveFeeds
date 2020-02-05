package meeds.sys.proxying.containers.impl;

import java.util.* ;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.core.* ;
import meeds.api.Meeds;
import meeds.sys.homing.*;
import meeds.sys.proxying.*;
import meeds.sys.homing.containers.*;
import meeds.sys.proxying.containers.*;

public class ProxyTargets_Impl extends Container<ProxyTargets_Impl> implements ProxyTargets, ProxyTargets.Updater {
    
    public ProxyTargets_Impl() {
    	init() ;
    }
    
    private void init() {
     
    	Container.monitor( Location.class, new ContainerListener<Location> () {
			public void handleContainerUpdate(Location c) {
				currentPos = c.pos() ;
				Transport x = closestProxy() ;
		        if( x != closestProxy ) {
		        	closestProxy = x ;
		        	notifyUpdate() ;
		        }
			}
		}) ;
    }

	public Transport closestProxy() {
		SortedSet<CmpProxyInfo> x = new TreeSet<CmpProxyInfo>( proxies.values() ) ;
		return x.isEmpty() ? null : x.first().transport ;
	}

	
    private void addTransport( Position pos, String url ) {
        if( ! proxies.containsKey( url ) ) {
            Transport t = FeedsNode.openTransport( url, "outgoing") ;
            proxies.put( url, new CmpProxyInfo( t.url(), pos, t) ) ;
            transports.put( t.dst(), t) ;
            Feeds.out.println("Adding tentative proxy at: [" + t.url() + "]" ) ;
        }
        Transport x = closestProxy() ;
        if( x != closestProxy ) {
        	closestProxy = x ;
        	super.notifyUpdate() ;
        }
    }
    
    public void put( ProxyInfo proxy) {
    	Scanner s = new Scanner( proxy.url ).useDelimiter(";") ;                
    	while( s.hasNext() ) 
    		addTransport( proxy.pos, s.next() ) ;
        
    	super.notifyUpdate() ;
    }
    
	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	private Transport closestProxy = null ;
	private Position currentPos = new Position() ;
	private final Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
	private final Map<String, CmpProxyInfo> proxies = new HashMap<String, CmpProxyInfo>() ;

	
	class CmpProxyInfo extends ProxyInfo implements Comparable<CmpProxyInfo>{
		
		Transport transport ;

		CmpProxyInfo( String url, Position pos, Transport t ) {
			super( pos, 0, url ) ;
			this.transport = t ;
		}

		public int compareTo( CmpProxyInfo other) {
			return currentPos.compare( this.pos, other.pos) ;
		}
		
		public int hashCode() {
			return url.hashCode() ;
		}

		public boolean equals( CmpProxyInfo other ) {
			return url.equals( other.url ) ;
		}

		public boolean equals( Object other ) {
			return other != null && equals( (CmpProxyInfo) other ) ;
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	
}

