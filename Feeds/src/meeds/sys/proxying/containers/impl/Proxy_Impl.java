package meeds.sys.proxying.containers.impl;

import java.util.*;

import feeds.sys.*;
import feeds.sys.core.*;

import meeds.sys.proxying.*;
import meeds.sys.proxying.containers.*;

public class Proxy_Impl extends Container<Proxy> implements Proxy, Proxy.Updater {

	public Proxy_Impl() {
		super.notifyUpdate();
	}

	
	public Transport closestProxy() {
		return closestProxy ;
	}

	public void put(ProxyBindingReply r) {
		r.setTimestamp() ;
		updateTransport(r);
	}

	private void updateTransport(ProxyBindingReply r) {
		ProxyBindingReply o = proxies.get( r.src());
		try {
			if (o != null && !r.urls().equals(o.urls())) {
				Transport t = transports.get(r.src());
				t.dispose();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}

		Transport t = transports.get(r.src());
		try {
			if (t == null) {
				Scanner s = new Scanner(r.urls()).useDelimiter(";");
				t = FeedsNode.openTransport( s.next(), "outgoing");
				transports.put(r.src(), t);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		proxies.put(t.dst(), r) ;

		List<Transport> st = sortedProxies() ;
		if( ! st.isEmpty() && closestProxy != st.get(0) ) {
			closestProxy = st.get(0) ;
			super.notifyUpdateNow() ;
		}		
	}

	private List<Transport> sortedProxies() {
		List<Transport> v = new ArrayList<Transport>();

		for (ProxyBindingReply i : new TreeSet<ProxyBindingReply>(proxies.values())) {
			v.add(transports.get(i.src()));
		}
		return v;
	}

	private Transport closestProxy = null ;
	private Map<ID, Transport> transports = new HashMap<ID, Transport>();
	private Map<ID, ProxyBindingReply> proxies = new HashMap<ID, ProxyBindingReply>();
}