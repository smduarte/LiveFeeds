package sensing.persistence.core.query.rndtree;

import sensing.persistence.core.query.Query;

public class RTQuery extends Query {

	public RTQuery(Query q, UUID rootId) {
		super(q, rootId);
		this.level = 0;
	}

	public RTQuery(RTQuery q) {
		super(q);
		this.level = q.level+1;
	}
}
