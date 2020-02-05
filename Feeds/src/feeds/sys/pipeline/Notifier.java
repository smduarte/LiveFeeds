package feeds.sys.pipeline;

import java.util.HashSet;
import java.util.Set;

import feeds.api.Subscription;
import feeds.sys.core.ID;
import feeds.sys.core.Router;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;

public class Notifier<E, P, F, Q> implements Router<E, P, F, Q> {

	Notifier(ID channel, boolean isUserChannel) {
		this.channel = channel;
		this.isUserChannel = isUserChannel ;
		this.fsc = new ChannelSubscriptions<F>(channel, "fsc-" + channel, isUserChannel);
		this.psc = new ChannelSubscriptions<E>(channel, "psc-" + channel, isUserChannel);	
	}

	public ID channel() {
		return channel;
	}
	
	
	public Subscription pSubscribe( ChannelSubscription<E> cs) {
		cs.setOwner(psc);
		return psc.add(cs);
	}

	public Subscription fSubscribe( ChannelSubscription<F> cs) {
		cs.setOwner(fsc);
		return fsc.add(cs);
	}

	public void unsubscribe(ChannelSubscription<?> cs) {
		if (cs.isFeedbackSubscription)
			fsc.remove(cs);
		else
			psc.remove(cs);
	}

	public void cRoute(cPacket p) throws Exception {
		throw new RuntimeException(String.format("Notifier<%s>: discarding control packet", channel));
	}

	public void pRoute(pPacket<E, P> p) {

		Set<Transport> targets = new HashSet<Transport>();
		for (ChannelSubscription<E> i : psc.subscriptions())
			try {
				if (i.criteria.accepts(p.envelope()))
					targets.add(i.transport);
			} catch (Exception x) {
				x.printStackTrace();
			}	
			
		for (Transport t : targets) {
			try {
				t.send(p);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	public void fRoute(fPacket<F, Q> p) {
		
		for (ChannelSubscription<F> i : fsc.subscriptions())
			try {
				if (( i.ignoreDestination || i.subscriber.equals(p.dst)) && i.criteria.accepts(p.envelope()))
					i.transport.send(p);
			} catch (Exception x) {
				x.printStackTrace();
			}
	}

	public void init() {
	}

	
	ID channel;
	boolean isUserChannel ;
	protected ChannelSubscriptions<E> psc;
	protected ChannelSubscriptions<F> fsc;
}
