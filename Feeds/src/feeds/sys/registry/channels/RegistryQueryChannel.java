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
import feeds.sys.registry.RegistryService;
import feeds.sys.util.ExpirableSet;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */

public class RegistryQueryChannel extends BasicTemplate<String, Void, Void, Void> {

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
					if (p.isLocal)
						soq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					loq.send(p);
				}
			});
			break;

		case sNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {
				public void pRoute(pPacket<String, Void> p) throws Exception {
					if (p.isLocal || p.isReRouted) {
						soq.send(p);
					} else
						loq.send(p);
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
					if (p.isLocal || p.isReRouted) {		
						if (!cache.contains(p.envelope())) {
							cache.add(p.envelope());
							ID target = hashesTo(p.envelope());
							if (target.equals(thisNode))
								loq.send(p);
							else
								send(backbone, target, p);
						}
					} else
						loq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					ID dst = p.dst.major();
					if (p.isLocal || p.isReRouted || !dst.equals(thisNode)) {
						if (!send(clients, dst, p))
							send(backbone, dst, p);
					} else
						loq.send(p);
				}
			});
			break;
		}
	}

	private ID hashesTo(String s) {
		return RegistryService.hashesTo(backbone.keySet(), s);
	}

	private ID thisNode = FeedsNode.id();
	private Map<ID, Transport> clients = new HashMap<ID, Transport>();
	private Map<ID, Transport> backbone = new HashMap<ID, Transport>();
	private final ExpirableSet<Object> cache = new ExpirableSet<Object>(5.0, 5.0, null);
}