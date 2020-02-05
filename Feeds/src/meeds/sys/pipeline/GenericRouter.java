package meeds.sys.pipeline;

import feeds.sys.core.*;
import feeds.sys.packets.*;

@SuppressWarnings("unchecked")
public class GenericRouter implements Router {
	
	private ID channel ;
	
	public GenericRouter() {
		this(null) ;
	}
	
	public GenericRouter( ID channel ) {
		this.channel = channel ;
	}
	
	public ID channel() {
		return channel ;
	}
	
	public void init() {
	}
	
	public void cRoute(cPacket p) throws Exception {
	}


	public void fRoute(fPacket p) throws Exception {
	}


	public void pRoute(pPacket p) throws Exception {
	}

}
