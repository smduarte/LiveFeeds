package meeds.sys.homing.channel;

import java.util.*;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;

import meeds.sys.homing.*;
import meeds.sys.homing.containers.*;

public class HomingChannel extends BasicTemplate<Void, HomingRequest, Void, HomingReply> {

	public void init() {
		try {
			super.init();

			Container.monitor(HomebaseTargets.class, new ContainerListener<HomebaseTargets>() {
				public void handleContainerUpdate(HomebaseTargets hbt) {
					targets = hbt.servers();
				}
			});

			if (FeedsNode.isCnode()) {
				Container.monitor(HomingNodes.class, new ContainerListener<HomingNodes>() {
					public void handleContainerUpdate(HomingNodes cn) {
						clients = cn.transports();
					}
				});
			}
			switch (FeedsNode.type()) {

			case mNODE:
				pipeline.setTemplate(new BasicTemplate<Void, HomingRequest, Void, HomingReply>(channel()) {

					public void pRoute(pPacket<Void, HomingRequest> p) throws Exception {
						if (p.isLocal)
							try {
								super.send(targets, p);
							} catch (Exception x) {
								Feeds.err.printf("Transport error while binding...[%s]\n", x.getMessage());
							}
					}

					public void fRoute(fPacket<Void, HomingReply> p) throws Exception {
						if (!p.isLocal)
							loq.send(p);
					}
				});
				break;

			case cNODE:
				pipeline.setTemplate(new BasicTemplate<Void, HomingRequest, Void, HomingReply>(channel()) {

					public void pRoute(pPacket<Void, HomingRequest> p) throws Exception {
						if (p.isLocal)
							super.send(targets, p);
						else
							loq.send(p);
					}

					public void fRoute(fPacket<Void, HomingReply> p) throws Exception {
						if (p.isLocal)
							super.send(clients, p.dst.major(), p);
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

	ID thisNode = FeedsNode.id();
	private Map<ID, Transport> clients = new HashMap<ID, Transport>();
	private Collection<Transport> targets = new ArrayList<Transport>();
}
