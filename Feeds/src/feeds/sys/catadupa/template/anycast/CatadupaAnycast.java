package feeds.sys.catadupa.template.anycast;

import feeds.sys.FeedsNode;
import feeds.sys.catadupa.Catadupa;
import feeds.sys.catadupa.Node;
import feeds.sys.catadupa.Range;
import feeds.sys.core.Container;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.BasicTemplate;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class CatadupaAnycast<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {

	public void init() {
		super.init();
		switch (FeedsNode.type()) {
		case cNODE:
			pipeline.setTemplate(new CatadupaAnycastImpl<E, P, F, Q>(channel));
			break;
		case sNODE:
			pipeline.setTemplate(new CatadupaAnycastImpl<E, P, F, Q>(channel));
			break;
		case pNODE:
			break;
		}
	}
}

// --------------------------------------------------------------------------------------------------
class CatadupaAnycastImpl<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {

	CatadupaAnycastImpl(ID channel) {
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
		for (Node i : new Range().pNodes(catadupa.db, pp.envelope(), pp.channel)) {
			if (!i.equals(catadupa.thisNode))
				sendTo(i.url, pp);
			break;
		}
	}

	void feedback(fPacket<F, Q> fp) {

		for (Node i : new Range().fNodes(catadupa.db, fp.envelope(), fp.channel, fp.dst)) {
			if (!i.equals(catadupa.thisNode))
				sendTo(i.url, fp);
		}
	}

	Catadupa catadupa = null;

	protected void sendTo(String url, cPacket p) {
		try {
			Transport t = FeedsNode.openTransport(url, "outgoing").open();
			t.send(p);
			t.dispose();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}