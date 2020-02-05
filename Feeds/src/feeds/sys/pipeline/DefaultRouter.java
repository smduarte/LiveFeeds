package feeds.sys.pipeline;

import feeds.api.Feeds;
import feeds.sys.core.ID;
import feeds.sys.core.Router;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;

public class DefaultRouter<E,P,F,Q> implements Router<E,P,F,Q > {

	public DefaultRouter( Router<?,?,?,?> owner ) {
		this.owner = owner ;
	}
	
	public ID channel() {
		return owner.channel() ;
	}


	public void pRoute(pPacket<E, P> p) throws Exception {
		Feeds.out.println("pRoute()@" + channel());
	}

	public void fRoute(fPacket<F, Q> p) throws Exception {
		Feeds.out.println("fRoute()@" + channel());
	}

	public void cRoute(cPacket p) throws Exception {
		Feeds.out.println("cRoute()@" + channel());
	}

	public void init() {
	}

	private Router<?,?,?,?> owner ;
}
