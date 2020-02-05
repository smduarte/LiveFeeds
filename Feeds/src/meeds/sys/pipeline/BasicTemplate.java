package meeds.sys.pipeline;

import feeds.sys.FeedsNode;
import feeds.sys.core.*;

public class BasicTemplate<E, P, F, Q> extends feeds.sys.pipeline.BasicTemplate<E, P, F, Q> {

	protected BasicTemplate() {
		this.hoq = FeedsNode.openTransport("hoq://-/-/", "outgoing") ;
		this.poq = FeedsNode.openTransport("poq://-/-/", "outgoing") ;
	}

	public BasicTemplate(ID channel) {
		this.channel = channel;
		this.hoq = FeedsNode.openTransport("hoq://-/-/", "outgoing") ;
		this.poq = FeedsNode.openTransport("poq://-/-/", "outgoing") ;
	}

	protected Transport hoq, poq;
}
