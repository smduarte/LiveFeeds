package feeds.sys.templates.centradupa.anycast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.binding.containers.ClientNodes;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.membership.containers.CriteriaDB;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.BasicTemplate;
import feeds.sys.tasks.PeriodicTask;
import feeds.sys.util.RandomList;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class CentradupaAnycastTemplate<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
	 
	public void init() {
		super.init();
		switch (FeedsNode.type()) {
		case cNODE:
			pipeline.setTemplate(new c_Centradupa<E, P, F, Q>(channel));
			break;
		case sNODE:
			pipeline.setTemplate(new s_Centradupa<E, P, F, Q>(channel));
			break;
		case pNODE:
			pipeline.setTemplate(new p_Centradupa<E, P, F, Q>(channel));
			break;
		}
	}
}

// --------------------------------------------------------------------------------------------------
class CatadupaNodeChannel<E, P, F, Q> extends ControlPacketRouter<E, P, F, Q> {

	CatadupaNodeChannel(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();

		Container.monitor(CriteriaDB.class, new ContainerListener<CriteriaDB>() {
			public void handleContainerUpdate(CriteriaDB c) {
				sd = new SubscriptionData(c.pData().get(channel), c.fData().get(channel));
				subscriptionRefresherTask.reSchedule(2);
			}
		});
		subscriptionRefresherTask = new PeriodicTask(10, 10) {
			public void run() {
				if( ! sd.isEmpty() ) {
					soq.send(new Join_Request(sd).cPacket(channel));
					Feeds.err.printf("CentradupaAnycast: refreshing subscriptions: %s]\n", sd);
				}
			}
		};
	}

	public void pRoute(pPacket<E, P> p) {
		if (p.isLocal)
			soq.send(p);
		else
			loq.send(p);
	}

	public void fRoute(fPacket<F, Q> p) {
		if (p.isLocal)
			soq.send(p);
		else
			loq.send(p);
	}

	private SubscriptionData sd;
	PeriodicTask subscriptionRefresherTask;
}

class c_Centradupa<E, P, F, Q> extends CatadupaNodeChannel<E, P, F, Q> {
	c_Centradupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
}

class s_Centradupa<E, P, F, Q> extends CatadupaNodeChannel<E, P, F, Q>{
	s_Centradupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
}

// --------------------------------------------------------------------------------------------------
class p_Centradupa<E, P, F, Q> extends CatadupaNodeChannel<E, P, F, Q> {

	p_Centradupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();

		Container.monitor(ClientNodes.class, new ContainerListener<ClientNodes>() {
			public void handleContainerUpdate(ClientNodes cn) {
				clients = cn.transports();
			}
		});
	}

	public void pRoute(pPacket<E, P> p) {
		
		ID src = p.src.major() ;
		
		RandomList<SubscriptionData> candidates = new RandomList<SubscriptionData>( sd ) ;

		while( candidates.size() > 0 ) {
			SubscriptionData i = candidates.removeRandomElement() ;
			if (! i.owner.equals( src) && i.age() < 30 && i.pAccepts(p.envelope())) {
				try {
					clients.get(i.owner).send(p);
					break ;
				} catch (Exception x) {
					x.printStackTrace() ;
				}
			}
		}
	}

	public void fRoute(fPacket<F, Q> p) {
		for (SubscriptionData i : sd)
			if (i.age() < 30 && i.fAccepts(p.envelope(), p.dst.major())) {
				try {
					clients.get(i.owner).send(p);
				} catch (Exception x) {
					x.printStackTrace() ;
				}
			}
	}

	public void cRoute(Join_Request p) {
		sd.remove( p.sd ) ;
		sd.add(p.sd.clone());
	}

	private ID thisNode = FeedsNode.id() ;
	private Set<SubscriptionData> sd = new HashSet<SubscriptionData>();
	private Map<ID, Transport> clients = new HashMap<ID, Transport>();
}