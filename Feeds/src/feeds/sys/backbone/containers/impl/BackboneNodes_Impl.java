package feeds.sys.backbone.containers.impl;

import java.util.*;

import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.backbone.containers.*;

public class BackboneNodes_Impl extends Container<BackboneNodes> implements BackboneNodes, BackboneNodes.Updater {

	public BackboneNodes_Impl() {
		super.notifyUpdate() ;
	}

	synchronized public Set<String> putAll( List<String> urls) {
		for (String i : urls)
			put(i, false);		
		
		return null;
	}

	synchronized public Set<ID> putAll( String urls, boolean notifyImmediately) {
		newNodes.clear();
		if (urls != null) {
			StringTokenizer st = new StringTokenizer(urls, ";");
			while (st.hasMoreTokens()) {
				String url = st.nextToken();
				Transport transport = FeedsNode.openTransport(url, "outgoing");
				ID dst = transport.dst();
				if (!transports.containsKey(dst))
					newNodes.add(dst);
				
				transports.put(dst, transport);

			}
			
			if(! newNodes.isEmpty() ) {
				
				rendezVous = null ;
				reComputeUrls() ;
				
				if (notifyImmediately)
					super.notifyUpdateNow();
				else
					super.notifyUpdate();
			}
		}
		return newNodes;
	}

	synchronized public Set<ID> put( String url, boolean notifyImmediately) {
		newNodes.clear();
		if (url != null) {

			Transport transport = FeedsNode.openTransport(url, "outgoing");

			ID dst = transport.dst();
			if (! transports.containsKey(dst))
				newNodes.add(dst);
			
			transports.put(dst, transport); // expires in 15 minutes


			if(! newNodes.isEmpty() ) {

				rendezVous = null ;

				reComputeUrls() ;
				
				if (notifyImmediately)
					super.notifyUpdateNow();
				else
					super.notifyUpdate();
			}
		}
		return newNodes;
	}

	private void reComputeUrls() {
		Set<String> m = new TreeSet<String>();
		for( Transport i : transports.values() )
			m.add( i.url());

		urls = "";
		for( String i : m )
			urls += i + ";" ;
		
	}
	
	public String urls() {
		return urls ;
	}

	public boolean isKnown(ID g) {
		return transports.containsKey(g);
	}
	
	public Set<ID> newNodes() {
		return Collections.unmodifiableSet( newNodes ) ;
	}

	public Map<ID, Transport> transports() {
		return Collections.unmodifiableMap( transports ) ;
	}

	public Set<ID> nodes() {
		return Collections.unmodifiableSet( transports.keySet() ) ;
	}

	public ID rendezVous(ID x) {
		if( rendezVous == null ) {
			long ref = 0x716253B1F42BDA47L ^ x.longValue() ;
			for( ID i : transports.keySet() ) {
				if( rendezVous == null || ((rendezVous.longValue() ^ ref) > (i.longValue() ^ ref))) {
					rendezVous = i ;
				}
			}
		}
		return rendezVous ;
	} 
	
	private String urls = "";
	private ID rendezVous = null ;
	private Set<ID> newNodes = new HashSet<ID>();
	private Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
	

}