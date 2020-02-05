package feeds.sys.pipeline;

import java.util.*;

import feeds.api.*;
import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;

public class BasicTemplate<E, P, F, Q> implements Template<E, P, F, Q> {

	protected BasicTemplate() {
	}

	public BasicTemplate(ID channel) {
		this.channel = channel;
	}

	@SuppressWarnings("unchecked")
	synchronized public void init() {
		this.pipeline = FeedsNode.plm().pipeline(channel);
		this.notifier = pipeline.notifier;
		this.pipeline.pkt_iq.setRouter(this);
		this.liq = pipeline.pkt_iq;
		this.loq = pipeline.pkt_oq;
		this.soq = pipeline.soq;
		this.psc = notifier.psc;
		this.fsc = notifier.fsc;
	}

	public ID channel() {
		return channel;
	}

	public void setChannel(ID channel) {
		this.channel = channel;
	}

	public void pRoute(pPacket<E, P> p) throws Exception {
		pipeline.defaultRouter.pRoute(p) ;
	}

	public void fRoute(fPacket<F, Q> p) throws Exception {
		pipeline.defaultRouter.fRoute(p) ;
	}

	public void cRoute(cPacket p) throws Exception {
		pipeline.defaultRouter.cRoute(p) ;
	}

	// ----------------------------------------------------------------------------------------------------------//
	protected void send(Collection<Transport> targets, cPacket p) {
		for (Transport t : targets)
			try {
				t.send(p);
			} catch (NullPointerException x) {
			} 
	}

	protected boolean send( Map<ID, Transport> transports, ID target, cPacket p) {
		try {
			transports.get(target).send(p);
			return true;
		} catch (NullPointerException x) {
			return false;
		}
	}

	protected void send( Map<ID, Transport> transports, Collection<ID> targets, cPacket p) {
		try {
			HashSet<Transport> _targets = new HashSet<Transport>();

			for (Object i : targets) {
				Transport t = transports.get(i);
				if (t != null)
					_targets.add(t);
			}
			send(_targets, p);
		} catch (NullPointerException npe) {
		}
	}

	public Object getStub(String name, Object... extraArgs)
			throws FeedsException {
		return null;
	}

	protected ID channel;
	protected Transport loq;
	protected Transport liq;
	protected Transport soq;
	protected ChannelSubscriptions<E> psc;
	protected ChannelSubscriptions<F> fsc;
	protected Notifier<E, P, F, Q> notifier;
	protected Pipeline<E, P, F, Q> pipeline;
}
