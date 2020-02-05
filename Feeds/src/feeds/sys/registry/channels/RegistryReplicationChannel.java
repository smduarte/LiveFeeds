package feeds.sys.registry.channels;

import java.util.HashMap;
import java.util.Map;

import feeds.sys.FeedsNode;
import feeds.sys.backbone.containers.BackboneNodes;
import feeds.sys.binding.containers.ClientNodes;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.BasicTemplate;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */

public class RegistryReplicationChannel extends BasicTemplate<String, Void, Void, Void> {

	public void init() {
		super.init();

		Container.monitor(BackboneNodes.class, new ContainerListener<BackboneNodes>() {
			public void handleContainerUpdate(BackboneNodes bn) {
				backbone = bn.transports();
			}
		});

		if (FeedsNode.isServer()) {
			Container.monitor(ClientNodes.class, new ContainerListener<ClientNodes>() {
				public void handleContainerUpdate(ClientNodes cn) {
					clients = cn.transports();
				}
			});
		}

		switch (FeedsNode.type()) {
		case cNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {

				public void pRoute(pPacket<String, Void> p) throws Exception {
					if (p.isLocal) {
						soq.send(p);
					}
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					loq.send(p);
				}
			});
			break;

		case sNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {
				public void pRoute(pPacket<String, Void> p) throws Exception {					
					if (! p.isLocal)
						loq.send(p) ;
					
					soq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					if (p.isLocal || p.isReRouted) {
						send(clients, p.dst.major(), p);
					} else
						loq.send(p);
				}
			});
			break;

		case pNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {

				public void pRoute(pPacket<String, Void> p) throws Exception {
					ID target = hash(p.envelope());
					if (! target.equals(thisNode))
						send(backbone, target, p);
					
					loq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					if (p.isLocal || p.isReRouted) {
						ID dst = p.dst.major();
						if (!send(clients, dst, p))
							send(backbone, dst, p);
					} else
						loq.send(p);
				}
			});
			break;
		}
	}

	private ID hash(String s) {
		ID res = null;
		long v = s.hashCode() << 31 ^ s.hashCode();
		for (ID i : backbone.keySet())
			if (res == null || (i.longValue() ^ v) < (res.longValue() ^ v))
				res = i;

		return res;
	}

	private ID thisNode = FeedsNode.id();
	private Map<ID, Transport> clients = new HashMap<ID, Transport>();
	private Map<ID, Transport> backbone = new HashMap<ID, Transport>();
}