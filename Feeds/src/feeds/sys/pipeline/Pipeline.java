package feeds.sys.pipeline;

import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.core.Router;
import feeds.sys.core.Template;
import feeds.sys.core.Transport;

public class Pipeline<E, P, F, Q> {

	Pipeline(ID channel) {
		this(channel, new OnDemandLoader<E, P, F, Q>(channel), false);
	}
	
	public Pipeline(ID channel, Template<E, P, F, Q> t, boolean isUserChannel) {
		this( channel, t, null, null, isUserChannel ) ;
	}

	public Pipeline(ID channel, Template<E, P, F, Q> t, PacketQueue pkt_iq, PacketQueue pkt_oq, boolean isUserChannel) {
		this.pkt_iq = pkt_iq;
		this.pkt_oq = pkt_oq;
		this.processor = t;
		this.channel = channel;
		this.processor.setChannel(channel);
		this.soq = FeedsNode.openTransport("soq://-/-/", "outgoing");
		this.defaultRouter = new DefaultRouter<E, P, F, Q>(processor);
		this.notifier = new Notifier<E, P, F, Q>(channel, isUserChannel);
		this.pkt_iq.setRouter(processor);
		this.pkt_oq.setRouter(notifier);
	}

	public void setQueues(PacketQueue iq, PacketQueue oq) {
		this.pkt_iq = iq;
		this.pkt_oq = oq;
	}

	synchronized public void setTemplate(Template<E, P, F, Q> t) {
		this.processor = t;
		t.init();
	}

	synchronized public void setDefaultRouter(Router<E, P, F, Q> r) {
		this.defaultRouter = r;
		r.init();
	}

	public Notifier<E, P, F, Q> notifier() {
		return notifier;
	}

	public Template<?, ?, ?, ?> processor() {
		return processor;
	}

	public PacketQueue pkt_iq() {
		return pkt_iq;
	}

	public PacketQueue pkt_oq() {
		return pkt_oq;
	}

	ID channel;
	Transport soq;
	PacketQueue pkt_iq, pkt_oq;
	Router<E, P, F, Q> defaultRouter;
	Template<E, P, F, Q> processor;
	protected Notifier<E, P, F, Q> notifier;
}
