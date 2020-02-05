package meeds.sys.pipeline;

import feeds.sys.core.*;
import feeds.sys.packets.*;

import meeds.api.*;

public class DefaultRouter<E, P, F, Q > implements Router<E, P, F, Q> {

	public DefaultRouter( Router<?,?,?,?> owner ) {
		this.owner = owner ;
	}

	public ID channel() {
		return owner.channel() ;
	}

	public void pRoute(pPacket<E, P> p) throws Exception {
		Meeds.err.println("Meeds.pRoute()" + p ) ;
	}

	public void fRoute(fPacket<F, Q> p) throws Exception {
		Meeds.err.println("Meeds.pRoute()" + p ) ;
	}

	public void cRoute(cPacket p) throws Exception {
		Meeds.err.println("Meeds.pRoute()" + p ) ;
	}

	public void init() {
	}

	
	private Router<?,?,?,?> owner ;
}