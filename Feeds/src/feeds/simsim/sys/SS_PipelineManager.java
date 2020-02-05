package feeds.simsim.sys;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;
import feeds.sys.directory.*;

public class SS_PipelineManager extends PipelineManager {

	// To on-demand loader ...
	public <E, P, F, Q> Pipeline<E, P, F, Q> setupNewPipeline(ChannelRecord r) {
		return super.setupNewPipeline(r, new SS_PacketQueue("piq"), new SS_PacketQueue("poq"));
	}
}

class SS_PacketQueue extends PacketQueue {

	SS_PacketQueue(String name) {
		super(name);
	}

	public cPacket enqueue(cPacket p) {
		try {
			p.ttl( p.ttl() - 1 ) ;
			p.route(super.router);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void send(cPacket p) throws FeedsException {
		try {
			p.route(router);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRouter(Router<?, ?, ?, ?> r) {
		this.router = r ;
	}
}