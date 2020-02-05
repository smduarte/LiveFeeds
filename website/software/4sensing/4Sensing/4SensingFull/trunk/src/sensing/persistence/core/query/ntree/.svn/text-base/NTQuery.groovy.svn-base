package sensing.persistence.core.query.ntree;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.network.Peer;

public class NTQuery extends Query {
	final int pDBVersionN;
	
	public NTQuery(Query q, UUID rootId) {
		super(q, rootId);
	}
	
	public NTQuery(Query q, int pDBVersionN) {
		super(q, String.format("%s:%s", q.id, Integer.toString(pDBVersionN)), q.rootId);
		this.pDBVersionN = pDBVersionN;
	}

	public NTQuery(NTQuery q) {
		super(q);
		this.pDBVersionN = q.pDBVersionN;
	}
}
	
