package feeds.sys.catadupa;

import feeds.sys.FeedsNode;
import feeds.sys.core.Container;
import feeds.sys.core.ID;

public class Catadupa extends Container<Catadupa> {

	public static ID CATADUPA_CHANNEL_ID = new ID(4L) ;
	
	public static final int NODE_KEY_LENGTH = 10 ; //62
	public static final int NUMBER_OF_SLICES = 2 ;
	public static final int SL_BROADCAST_PERIOD = 10 ;
	public static final int BROADCAST_MAX_FANOUT = 3 ;
	public static final double JOIN_ATTEMPT_PERIOD = 15;

	public static final int PUBSUB_MAX_FANOUT = 2;
		
	public long key ;
	public Node thisNode ;
	public boolean joined = false ;

	public NodeDB db = new NodeDB();
	public oView oView = new oView();
	public gView gView = new gView();
	public JoinBatchDB jbdb = new JoinBatchDB();	

	public ID id = FeedsNode.id() ;

	Catadupa() {
		super("Catadupa") ;
	}
	
	
	Stamp issueStamp() {
		Stamp res = new Stamp( thisNode == null ? id.longValue() : thisNode.key, serial++);
		gView.merge( res ) ;
		return res ;
	}
	
	private int serial = 0;
}
