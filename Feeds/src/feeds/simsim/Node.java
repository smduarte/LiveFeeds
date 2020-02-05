package feeds.simsim;

import simsim.core.* ;
import simsim.utils.*;

abstract public class Node extends AbstractNode implements Displayable {

	public int index ;
	public static NodeDB<Node> db = new NodeDB<Node>() ;

	public Node() {
		db.store(this) ;
	}
	
	public static RandomList<Node> nodes() {
		return db.nodes ;
	}
}
