package feeds.sys.catadupa.template;

import static feeds.sys.catadupa.Catadupa.PUBSUB_MAX_FANOUT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.catadupa.Catadupa;
import feeds.sys.catadupa.Node;
import feeds.sys.catadupa.Range;
import feeds.sys.core.Container;
import feeds.sys.core.ID;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.BasicTemplate;
import feeds.sys.tasks.Task;
import feeds.sys.util.ExpirableMap;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class CatadupaTemplate<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {

	public void init() {
		super.init();
		switch (FeedsNode.type()) {
		case cNODE:
			pipeline.setTemplate(new c_CatadupaTemplate<E, P, F, Q>(channel));
			break;
		case sNODE:
			pipeline.setTemplate(new s_CatadupaTemplate<E, P, F, Q>(channel));
			break;
		case pNODE:
			break;
		}
	}
}

// --------------------------------------------------------------------------------------------------
class CatadupaNodeTemplate<E, P, F, Q> extends ControlPacketRouter<E, P, F, Q> {

	CatadupaNodeTemplate(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
		catadupa = Container.byClass(Catadupa.class);
	}

	public void pRoute(pPacket<E, P> p) throws Exception {
		this.publish(p);
	}

	public void fRoute(fPacket<F, Q> p) throws Exception {
		if (catadupa.id.equalsIgnoreMinor(p.dst))
			loq.send(p);
		else
			this.feedback(p);
	}

	void publish(pPacket<E, P> pp) {
		CatadupaPayload<E, P> p = new CatadupaPayload<E, P>(pp.src, pp.envelope(), pp.data());
		if (!catadupa.joined) {
			new PendingTreeDispatch(new CatadupaTreePS(catadupa.thisNode, new Range(), catadupa.oView, p)).dispatch();
		} else {
			// Feeds.out.printf("Catadupa---->%s\n", new
			// Range().nodes(catadupa.db) ) ;
//			for (Node i : new Range().pNodes(catadupa.db, pp.envelope(), pp.channel))
//				if (i.key.intValue() == 39) {
//					//Feeds.out.printf("Catadupa---->%s\n", new Range().pNodes(catadupa.db, pp.envelope(), pp.channel));
//					break;
//				}
//			Feeds.err.println("[Publish thisNode:" + catadupa.thisNode);

			for (Range s : new Range().slice(PUBSUB_MAX_FANOUT, catadupa.db)) {
				new PendingTreeDispatch(new CatadupaTreePS(catadupa.thisNode, s, catadupa.oView, p)).dispatch();
			}
		}
	}

	void feedback(fPacket<F, Q> fp) {
//		while( ! catadupa.joined ) {
//			Feeds.sleep(1) ;
//		}

		//Feeds.out.printf("Feedback----> %s -> %s \n", fp.src, fp.dst) ;
		//Feeds.out.printf("---->%s\n", new Range().nodes( catadupa.db )) ;

		for (Node i : new Range().fNodes(catadupa.db, fp.envelope(), fp.channel, fp.dst)) {
			if (!i.equals(catadupa.thisNode))
				sendTo(i.url, fp);
		}
	}

	void cRoute(CatadupaTreePS x) {
//		Feeds.err.println("thisNode:" + catadupa.thisNode);
		if (!catadupa.oView.contains(x.view)) {
			pending.add(x);

			Thread.dumpStack() ; // Confirm that this is being handled...don't think so...
			queueDispatchTask.reSchedule(1);
			return;
		}
		Object envelope = x.payload.envelope;

		// feeds.api.Feeds.err.println( x.range + "->" + envelope ) ;
		Range r = x.range;

		Collection<Node> P = r.pNodes(catadupa.db, envelope, channel);
		r = r.advancePast(catadupa.key);

		Collection<Node> L = r.pNodes(catadupa.db, envelope, channel);
		assert !L.contains(catadupa.thisNode);

		P.removeAll(L);

		if (P.size() > 0) {
//			feeds.api.Feeds.err.println("Atrasados:->" + envelope + "->" + P);
			for (Node i : P)
				if (i.pAccepts(envelope, channel))
					sendTo(i.url, new CatadupaLeafPS(x.view, x.payload));
		}

		if (L.size() <= PUBSUB_MAX_FANOUT) {
//			feeds.api.Feeds.err.println("Folhas:->" + envelope + "->" + L);
			for (Node i : L)
				if (i.pAccepts(envelope, channel))
					sendTo(i.url, new CatadupaLeafPS(x.view, x.payload));

		} else {
			for (Range s : r.slice(PUBSUB_MAX_FANOUT, catadupa.db)) {
				// feeds.api.Feeds.err.println( "Filhos:->" + x.payload.envelope
				// + "->" + s.nodes(catadupa.db, envelope, channel)) ;
				new PendingTreeDispatch(new CatadupaTreePS(catadupa.thisNode, x, s)).dispatch();
			}
		}

		sendTo(x.url, new CatadupaTreePSACK(x));
	}

	public void cRoute(CatadupaLeafPS x) {
//		Feeds.err.printf("%s CatadupaLeafPS %s\n", catadupa.thisNode, x.payload.data);
		loq.send(new pPacket<E, P>(channel(), x.payload.src, x.payload.envelope, x.payload.data));
	}

	void cRoute(CatadupaTreePSACK x) {
		synchronized (treeDispatchQueue) {
			treeDispatchQueue.remove(x.id);
		}
	}

	Catadupa catadupa = null;

	Set<CatadupaTreePS> pending = new HashSet<CatadupaTreePS>();
	ExpirableMap<ID, PendingTreeDispatch> treeDispatchQueue = new ExpirableMap<ID, PendingTreeDispatch>(120, 10); // TODO
	// FIX
	// this
	// Hack,
	// not
	// all
	// packets
	// are
	// being
	// ACKed...

	Task queueDispatchTask = new Task(1) {
		public void run() {

			for (PendingTreeDispatch i : cloneQueue()) {
				i.process();
			}

			if (!treeDispatchQueue.isEmpty())
				this.reSchedule(1.0);
		}

		private Collection<PendingTreeDispatch> cloneQueue() {
			synchronized (treeDispatchQueue) {
				return new ArrayList<PendingTreeDispatch>(treeDispatchQueue.values());
			}
		}
	};

	class PendingTreeDispatch {

		double sndStamp = -1;
		final CatadupaTreePS ev;
		double ageStamp = Feeds.time();

		PendingTreeDispatch(CatadupaTreePS ev) {
			this.ev = ev;
		}

		double age() {
			return Feeds.time() - ageStamp;
		}

		void advanceRange() {
			for (Node i : ev.range.nodes(catadupa.db))
				if (i.pAccepts(ev.payload.envelope, channel())) {
					ev.range = ev.range.advancePast(i.key);
					ageStamp = Feeds.time();
					break;
				}
		}

		void dispatch() {
			for (Node i : ev.range.nodes(catadupa.db))
				if (i.pAccepts(ev.payload.envelope, channel())) {
					sendTo(i.url, ev);
					sndStamp = Feeds.time();
					synchronized (treeDispatchQueue) {
						treeDispatchQueue.put(ev.id, this);
					}
					break;
				}
		}

		void process() {
			if (age() > 120)
				advanceRange();

			double now = Feeds.time();
			if (now - sndStamp > 30) {
				sndStamp = now;
				dispatch();
			}
		}

		public int hashCode() {
			return ev.id.hashCode();
		}

		public boolean equals(Object other) {
			return this.getClass() == other.getClass() && this.equals((PendingTreeDispatch) other);
		}

		public boolean equals(PendingTreeDispatch other) {
			return this.ev.id.equals(other.ev.id);
		}

		public String toString() {
			return String.format("%s (%s)", ev.id, ev.payload.data);
		}
	}
}

class s_CatadupaTemplate<E, P, F, Q> extends CatadupaNodeTemplate<E, P, F, Q> {
	s_CatadupaTemplate(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
}

class c_CatadupaTemplate<E, P, F, Q> extends CatadupaNodeTemplate<E, P, F, Q> {

	c_CatadupaTemplate(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
}