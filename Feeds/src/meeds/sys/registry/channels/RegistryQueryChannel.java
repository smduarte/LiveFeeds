package meeds.sys.registry.channels;

import java.util.*;


import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;

import meeds.sys.pipeline.*;
import meeds.sys.homing.containers.*;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */

public class RegistryQueryChannel extends BasicTemplate<String, Void, Void, Void> {

	public void init() {
		super.init();
	
		if (FeedsNode.isCnode()) {
			Container.monitor(HomingNodes.class, new ContainerListener<HomingNodes>() {
				public void handleContainerUpdate(HomingNodes hn) {
					mobiles = hn.transports();
				}
			});
		}

		switch (FeedsNode.type()) {
		case mNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {

				public void pRoute(pPacket<String, Void> p) throws Exception {
					if (p.isLocal)
						hoq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					loq.send(p);
				}
			});
			break;

		case cNODE:
			pipeline.setTemplate(new BasicTemplate<String, Void, Void, Void>(channel()) {
				public void pRoute(pPacket<String, Void> p) throws Exception {
					loq.send(p);
				}

				public void fRoute(fPacket<Void, Void> p) throws Exception {
					if (p.isLocal) {
						send(mobiles, p.dst.major(), p);
					} else
						loq.send(p);
				}
			});
			break;
		}
	}

	private Map<ID, Transport> mobiles = new HashMap<ID, Transport>();
}