package feeds.sys.pipeline;

import java.util.Collection;

import feeds.api.Channel;
import feeds.api.Criteria;
import feeds.api.FeedbackSubscriber;
import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.api.Subscription;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.transports.BasicTransport;

public class ChannelStub<E, P, F, Q> implements Channel<E, P, F, Q> {

	public ChannelStub(Pipeline<E, P, F, Q> pipeline, String name) {
		this.name = name;
		this.piq = pipeline.pkt_iq;
		this.channel = pipeline.channel;
		this.notifier = pipeline.notifier;
		this.source = FeedsNode.id().minor();
	}

	public Object id() {
		return channel;
	}

	public String name() {
		return name;
	}

	synchronized public void dispose() {
		Feeds.todo() ; // TODO
	}

	public void reRoute(Receipt r) throws FeedsException {
		try {
			piq.enqueue(((cPacket) r).reRoute());
		} catch (Exception x) {
			throw new FeedsException(x.toString());
		}
	}

	public Receipt publish(P data) throws FeedsException {
		return this.publish(null, data);
	}

	public Receipt feedback(Receipt r, Q data) throws FeedsException {
		return this.feedback(r, null, data);
	}

	public Receipt publish(E envelope, P data) throws FeedsException {
		pPacket<E, P> p = new pPacket<E, P>(channel, source, envelope, data);
		piq.enqueue(p);
		bm.accountTransfer(p.packetSize());
		return p;
	}

	public Receipt feedback(Receipt r, F envelope, Q data) throws FeedsException {
		fPacket<F, Q> p = new fPacket<F, Q>(channel, source, (ID) r.source(), envelope, data);
		piq.enqueue(p);
		bm.accountTransfer(p.packetSize());
		return p;
	}
	
	public Subscription subscribe(Subscriber<E, P> l) throws FeedsException {
		return notifier.pSubscribe(new P_TransportAdaptor<E, P>(channel, source, l, null).handle);
	}

	public Subscription subscribe(Criteria<E> c, Subscriber<E, P> l) throws FeedsException {
		return notifier.pSubscribe(new P_TransportAdaptor<E, P>(channel, source, l, c).handle);
	}

	public Subscription subscribe(E e, Subscriber<E, P> l) throws FeedsException {
		return subscribe( new EqualsCriteria<E>(e), l) ;
	}
	
	public Subscription subscribeFeedback(FeedbackSubscriber<F, Q> l) throws FeedsException {
		return notifier.fSubscribe(new F_TransportAdaptor<F, Q>(channel, source, l, null).handle);
	}

	public Subscription subscribeFeedback(Criteria<F> c, FeedbackSubscriber<F, Q> l) throws FeedsException {
		return notifier.fSubscribe(new F_TransportAdaptor<F, Q>(channel, source, l, c).handle);
	}

	public Subscription subscribeFeedback(F e, FeedbackSubscriber<F, Q> l) throws FeedsException {
		return this.subscribeFeedback(new EqualsCriteria<F>(e), l) ;
	}

	public void setOutputRate(double Bps, boolean blockingFlag) {
		bm.setRate(Bps, blockingFlag);
	}

	public double getOutputRateDelay() {
		return bm.delay();
	}

	protected ID source;
	protected ID channel;
	protected String name;
	protected PacketQueue piq;
	protected Notifier<E, P, F, Q> notifier;
	protected BandwidthManager bm = new BandwidthManager(Double.MAX_VALUE);	
}

class P_TransportAdaptor<E, P> extends BasicTransport {

	P_TransportAdaptor(ID channel, ID dst, Subscriber<E, P> subscriber, Criteria<E> criteria) {
		super("stub_p://-/" + dst + '/', "outgoing");
		this.subscriber = subscriber;
		this.handle = new ChannelSubscription<E>(channel, dst, criteria, this, false );
	}

	@SuppressWarnings("unchecked")
	public void send(cPacket p) {
		pPacket<E, P> receipt = (pPacket) p.clone();
		receipt.setHandle(handle);
		try {
			subscriber.notify(receipt, receipt.envelope(), receipt);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public int hashCode() {
		return subscriber.hashCode();
	}

	public boolean equals(P_TransportAdaptor<E, P> other) {
		return subscriber.equals(other.subscriber);
	}

	final Subscriber<E, P> subscriber;
	final ChannelSubscription<E> handle;
}

class F_TransportAdaptor<F, Q> extends BasicTransport {

	F_TransportAdaptor(ID channel, ID dst, FeedbackSubscriber<F, Q> subscriber, Criteria<F> criteria) {
		super("stub_f://-/" + dst + '/', "outgoing");
		this.subscriber = subscriber;
		this.handle = new ChannelSubscription<F>(channel, dst, criteria, this, true);
	}

	@SuppressWarnings("unchecked")
	public void send(cPacket p) {
		fPacket<F, Q> receipt = (fPacket<F, Q>) p;
		receipt.setHandle(handle);
		try {
			subscriber.notifyFeedback(receipt, receipt.envelope(), receipt);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public int hashCode() {
		return subscriber.hashCode();
	}

	public boolean equals(F_TransportAdaptor<F, Q> other) {
		return subscriber.equals(other.subscriber);
	}

	final ChannelSubscription<F> handle;
	final FeedbackSubscriber<F, Q> subscriber;
}

class BandwidthManager {

	private double rate;
	private boolean block;

	BandwidthManager(double rate) {
		this.rate = rate;
		this.lastTime = now();
	}

	void setRate(double rate, boolean block) {
		this.rate = rate;
	}

	void accountTransfer(int nb) {
		total += nb;
		credit += elapsed() * rate - nb;
		lastTime = now();
		if (credit < 0 && block) {
			double t = -credit / rate;
			if (t > 1)
				Feeds.sleep(t);
		}
	}

	double delay() {
		double res = -credit / rate;
		return res > 0 ? res : 0;
	}

	private double now() {
		return Feeds.time();
	}

	private double elapsed() {
		return Feeds.time() - lastTime;
	}

	void showOutputRate() {
		System.out.printf("%.1f Bytes/s\n", total / (now() - startTime));
	}

	private double total = 0;
	private double credit = 0;
	private double lastTime = 0.0;
	private double startTime = now();
}

@SuppressWarnings("serial")
class EqualsCriteria<E> extends Criteria<E> {

	final E proto ;
	public EqualsCriteria( E e) {
		this.proto = e ;
	}
	
	public boolean accepts( E e ) {
		return proto.equals( e ) ;
    }
        
    public Criteria<E> simplify( Collection<Criteria<E>> c ) {
        return new Criteria<E>() ;
    }
    
    public int compareTo( Criteria<E> c ) {    
        return 2 ;
    }
    
    public String toString() {
        return proto.toString() ;
    }
    
    public int hashCode() {
    	return proto.hashCode() ;
    }
    
    public boolean equals( EqualsCriteria<E> other ) {
    	return proto.equals( other.proto ) ;
    }
    
    @SuppressWarnings("unchecked")
	public boolean equals( Object other ) {
    	try {
    		return proto.equals( ((EqualsCriteria<E>)other).proto) ;
    	} catch( ClassCastException x ) {
    		return false ;
    	}	
    }
}