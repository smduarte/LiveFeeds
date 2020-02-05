package meeds.sys.tunnel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import meeds.api.Meeds;
import meeds.sys.MeedsNode;
import meeds.sys.homing.containers.Homebase;
import meeds.sys.pipeline.GenericRouter;
import meeds.sys.proxying.containers.Proxy;
import meeds.sys.proxying.containers.ProxyClients;
import feeds.api.Criteria;
import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.Dispatcher;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.membership.containers.CriteriaDB;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.ChannelSubscription;
import feeds.sys.pipeline.Pipeline;
import feeds.sys.tasks.Task;
import feeds.sys.transports.BasicTransport;
import feeds.sys.util.ExpirableMap;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class TunnelChannel<E, P, F, Q> extends meeds.sys.pipeline.BasicTemplate<E, P, F, Q> {

	public void init() {
		super.init();

		if (MeedsNode.isMnode()) {
			
			membershipUpdaterTask = new MembershipUpdaterTask();

			final CriteriaDB cdb = Container.byClass(CriteriaDB.class);

			Container.monitor(CriteriaDB.class, new ContainerListener<CriteriaDB>() {
				public void handleContainerUpdate(CriteriaDB c) {
					if (!c.isEmpty())
						membershipUpdaterTask.schedule(new MembershipPacket(c.pData(), c.fData()));
				}
			});

			Container.monitor( Proxy.class, new ContainerListener<Proxy>() {
				public void handleContainerUpdate(Proxy c) {
					membershipUpdaterTask.schedule( new MembershipPacket(cdb.pData(), cdb.fData()));
					switchComplete = false ;
					Transport t = c.closestProxy() ;
					if( t != null )
						prox.put(t.dst(), t) ;
				}
			});
			Container.monitor( Homebase.class, new ContainerListener<Homebase>() {
				public void handleContainerUpdate(Homebase c) {
					membershipUpdaterTask.schedule( new MembershipPacket(cdb.pData(), cdb.fData()));
				}
			});

		}
		if (MeedsNode.isCnode()) {
			Container.monitor(ProxyClients.class, new ContainerListener<ProxyClients>() {
				public void handleContainerUpdate(ProxyClients c) {
					mobiles = c.transports();
				}
			});
		}

		switch (FeedsNode.type()) {
		case mNODE:
			pipeline.setTemplate(new TunnelPacketRouter<E, P, F, Q>(channel()) {

				public void pRoute(pPacket<E, P> p) throws Exception {
					poq.send(new InboundPayloadPacket(p).cPacket(channel));
					loq(p.channel).send(p);
				}

				public void fRoute(fPacket<F, Q> p) throws Exception {
					if (p.dst.equalsIgnoreMinor(thisNode))
						loq(p.channel).send(p);
					else
						poq.send( new InboundPayloadPacket(p).cPacket(channel));
				}

				public void cRoute(OutboundPayloadPacket x) throws Exception {
					switchComplete |= x.src.equals( poq.dst() ) ;
					Feeds.err.printf("Got %s -> switchComplete:%s\n", x, switchComplete ) ;
					
					if( switchComplete && !x.src.equals( poq.dst() )) {
						ReleaseProxyPacket rpp = new ReleaseProxyPacket() ;
						Feeds.err.println("Sending ..." + rpp + " to " + x.src + " using:"+ prox.keySet() );
						super.send(prox, x.src, rpp.cPacket(channel));						
					} else {
						piq.dispatch(ControlPacket.decode(x.payload));
					}
				}

			});
			break;

		case cNODE:
			pipeline.setTemplate(new TunnelPacketRouter<E, P, F, Q>(channel()) {

				public void pRoute(pPacket<E, P> p) throws Exception {
					Meeds.err.println(p.envelope());
					Thread.dumpStack();
				}

				public void fRoute(fPacket<F, Q> p) throws Exception {
					Meeds.err.println(p.envelope());
					Thread.dumpStack();
				}

				public void cRoute(InboundPayloadPacket x) throws Exception {
					cPacket p = ControlPacket.decode(x.payload);
					p.route(srcTranslator);
				}

				public void cRoute(MembershipPacket x) throws Exception {
					Feeds.err.printf("Got MembershipPacket %s...\n", x);
					Mobile_TransportAdaptor mta = mob(x.src);
					if (mta != null)
						mta.addSubscriptions(x);

				}

				public void cRoute(ReleaseProxyPacket x) throws Exception {
					Feeds.err.printf("Got ReleaseProxyPacket %s--->%s\n", x, mobs);
					Mobile_TransportAdaptor mta = mob(x.src);
					if (mta != null)
						mta.cancelAllSubscriptions();
				}
			});
			break;
		}
	}

	Transport liq(final ID channel) {
		Transport res = liqs.get(channel);
		if (res == null) {
			res = new PipelineResolverTransport(channel );
			liqs.put(channel, res);
		} 
		return res;
	}

	Transport loq(ID channel) {
		Transport res = loqs.get(channel);
		if (res == null) {
			//Feeds.out.println("loqs channel:" + channel ) ;
			res = MeedsNode.plm().pipeline(channel).pkt_oq();
			assert res != null;
		}
		loqs.put(channel, res);
		return res;
	}

	Mobile_TransportAdaptor mob(ID mNode) {
		Mobile_TransportAdaptor res = mobs.get(mNode);
		if (res == null) {
			//Feeds.err.println( "Mobile: " + mNode ) ;
			res = new Mobile_TransportAdaptor(mNode);
		}
		mobs.put(mNode, res);
		return res;
	}

	MembershipUpdaterTask membershipUpdaterTask;

	ID thisNode = MeedsNode.id();
	boolean switchComplete = false;
	Dispatcher piq = MeedsNode.plm().dispatcher();
	Map<ID, Transport> mobiles = new HashMap<ID, Transport>();
	ExpirableMap<ID, Transport> liqs = new ExpirableMap<ID, Transport>(24*3600, 3600);
	ExpirableMap<ID, Transport> loqs = new ExpirableMap<ID, Transport>(24*3600, 3600);
	ExpirableMap<ID, Transport> prox = new ExpirableMap<ID, Transport>(24*3600, 3600);
	ExpirableMap<ID, Mobile_TransportAdaptor> mobs = new ExpirableMap<ID, Mobile_TransportAdaptor>(24*3600, 3600);

	ID_Translator idt = new ID_Translator();
	TC_Src_Translator srcTranslator = new TC_Src_Translator();

	class PipelineResolverTransport extends BasicTransport {
		PipelineResolverTransport( final ID channel ) {
			super("pipeline_loader://-/-", "outgoing");
			new Task(1) {
				public void run() {
					Pipeline<?, ?, ?, ?> p = MeedsNode.plm().pipeline(channel);
					if (p != null) {
						liqs.put(channel, p.pkt_iq());
						dispatchAllPendingPackets( p.pkt_iq() ) ;
						super.cancel() ;
					} else super.reSchedule(0.25) ;
				}
			};
		}

		public void send(cPacket c) {
			bufferQueue.add(c) ;
		}
		
		private void dispatchAllPendingPackets( Transport t ) {
			for( cPacket i : bufferQueue )
				t.send(i) ;		
			bufferQueue = null ;
		}
		
		List<cPacket> bufferQueue = new ArrayList<cPacket>() ;
	}
	class MembershipUpdaterTask extends Task {

		private MembershipPacket packet;

		MembershipUpdaterTask() {
			super(0);
		}

		void schedule(MembershipPacket x) {
			packet = x;
			if( ! super.isScheduled() )
				reSchedule(0.25);
		}

		public void run() {
			if (packet != null) {
				packet.setSerial() ;
				poq.send(packet.cPacket(channel));
				packet = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	class TC_Src_Translator extends GenericRouter {

		public void pRoute(pPacket p) {
			pPacket p2 = new pPacket(p.channel, idt.map(p.src), p.envelope(), p.data());
			p2.ttl(p.ttl());
			liq(p.channel).send(p2);
		}

		public void fRoute(fPacket p) {
			fPacket p2 = new fPacket(p.channel, idt.map(p.src), idt.unmap(p.dst), p.envelope(), p.data());
			p2.ttl(p.ttl());
			liq(p.channel).send(p2);
		}
	}

	@SuppressWarnings("unchecked")
	class TC_Dst_Translator extends GenericRouter {

		TC_Dst_Translator(ID dst) {
			this.dst = dst;
		}

		public void pRoute(pPacket p) {
			ID src = idt.unmap(p.src);
			pPacket p2 = new pPacket(p.channel, src, p.envelope(), p.data());
			if (!dst.equalsIgnoreMinor(src))
				send(mobiles, dst, new OutboundPayloadPacket(thisNode, p2).cPacket(channel));
		}

		public void fRoute(fPacket p) {
			ID src = idt.unmap(p.src);
			fPacket p2 = new fPacket(p.channel, src, idt.unmap(p.dst), p.envelope(), p.data());
			if (!dst.equalsIgnoreMinor(src))
				send(mobiles, dst, new OutboundPayloadPacket(thisNode, p2).cPacket(channel));
		}

		final ID dst;
	}
	
	class Mobile_TransportAdaptor extends BasicTransport {
				
		Mobile_TransportAdaptor(ID mNode) {
			super("mob://-/" + mNode, "outgoing");
			this.dstTranslator = new TC_Dst_Translator(mNode);
			this.mNode = mNode;
		}

		@SuppressWarnings("unchecked")
		void addSubscriptions(MembershipPacket x) {

			for (Map.Entry<ID, Set<Criteria<?>>> i : x.pCriteria.entrySet()) {
				ID channel = i.getKey();
				Pipeline p = MeedsNode.plm().pipeline(channel);
				if (p != null) {
					ChannelSubscription<?> cs = new ChannelSubscription(channel, x.src, new CriteriaSet(i.getValue()), this, false);
					p.notifier().pSubscribe(cs);					
					subs.add(cs);
				} else {
					Feeds.err.println("Got pSubscription for unknown channel:" + channel);
					Thread.dumpStack();
				}
			}
			for (Map.Entry<ID, Set<Criteria<?>>> i : x.fCriteria.entrySet()) {
				ID channel = i.getKey();
				Pipeline p = MeedsNode.plm().pipeline(channel);
				if (p != null) {
					ChannelSubscription<?> cs = new ChannelSubscription(channel, x.src, new CriteriaSet(i.getValue()), this, true, true);
					p.notifier().fSubscribe(cs);
					subs.add(cs);
				} else {
					Feeds.err.println("Got fSubscription for unknown channel:" + channel);
				}
			}
			Feeds.out.printf("add subs <%s>: %s\n", channel, subs ) ;
		}

		void cancelAllSubscriptions() {
			//Feeds.out.println(dst + " cancel subs:" + subs + "|" + serial + "/" + channel ) ;
			for (ChannelSubscription<?> i : subs)
				i.cancel();
		}

		public void send(cPacket p) {
			try {
				//Feeds.err.println("send subs:" + subs + "|" + serial + "/" + channel ) ;
				p.route(dstTranslator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int hashCode() {
			return mNode.hashCode();
		}

		public boolean equals(Mobile_TransportAdaptor other) {
			return mNode.equals(other.mNode);
		}

		final ID mNode;
		final TC_Dst_Translator dstTranslator;
		final List<ChannelSubscription<?>> subs = new ArrayList<ChannelSubscription<?>>();
	}

}

class CriteriaSet<E> extends Criteria<E> {

	CriteriaSet(Set<Criteria<E>> cs) {
		this.cs = cs;
	}

	public boolean accepts(E e) {
		for (Criteria<E> i : cs)
			try {
				if (i.accepts(e))
					return true;
			} catch (Exception x) {
			}
		return false;
	}

	public Criteria<E> simplify(Collection<Criteria<E>> c) {
		return new CriteriaSet<E>(new HashSet<Criteria<E>>(c));
	}

	public String toString() {
		return cs.toString();
	}

	Set<Criteria<E>> cs;
	private static final long serialVersionUID = 1L;
}

class ID_Translator {

	ID map(ID id) {
		ID res = table.get(id);
		if (res == null) {
			res = MeedsNode.id().minor();
			table.put(id, res);
			table.put(res, id);
		}
		return res;
	}

	ID unmap(ID id) {
		ID res = table.get(id);
		return res == null ? id : res;
	}

	private Map<ID, ID> table = new HashMap<ID, ID>();
}

