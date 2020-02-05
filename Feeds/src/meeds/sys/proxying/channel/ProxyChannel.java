package meeds.sys.proxying.channel;

import java.util.*;


import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;

import meeds.sys.proxying.containers.*;

public class ProxyChannel extends BasicTemplate0 {

	public void init() {
		try {
			super.init();

			Container.monitor( ProxyTargets.class, new ContainerListener<ProxyTargets>() {
				public void handleContainerUpdate(ProxyTargets pt) {
					closestProxy = pt.closestProxy();
				}
			});

			if (FeedsNode.isCnode()) {
				Container.monitor(ProxyClients.class, new ContainerListener<ProxyClients>() {
					public void handleContainerUpdate(ProxyClients pc) {
						mobiles = pc.transports();
					}
				});
			}
			switch (FeedsNode.type()) {

			case mNODE:
				pipeline.setTemplate(new BasicTemplate0(channel()) {
					
					public void pRoute(pPacket<Void,Void> p) throws Exception {
						if (p.isLocal)
							try {
								if( closestProxy != null )
									closestProxy.send( p ) ;
								
							} catch (Exception x) {
								Feeds.err.printf("Transport error while binding...[%s]\n", x.getMessage());
							}
					}

					public void fRoute(fPacket<Void, Void> p) throws Exception {
						if (!p.isLocal)
							loq.send(p);
					}
				});
				break;

			case cNODE:
				pipeline.setTemplate(new BasicTemplate0(channel()) {

					public void pRoute( pPacket<Void, Void> p) throws Exception {
							loq.send(p);
					}

					public void fRoute(fPacket<Void, Void> p) throws Exception {
						if (p.isLocal)
							super.send(mobiles, p.dst.major(), p);
						else
							loq.send(p);
					}

				});
				break;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	Transport closestProxy = null ;
	private Map<ID, Transport> mobiles = new HashMap<ID, Transport>();
}
