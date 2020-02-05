package feeds.sys.catadupa.packets;

import feeds.sys.catadupa.*;

public class CatadupaTreeCast extends CatadupaControlPacket {

	public int level ;
	public Range range ;
	public JoinBatch batch ;
	
	public CatadupaTreeCast( JoinBatch b ) {
		this(0, new Range(), b ) ;
    }
	
	public CatadupaTreeCast( Node root, JoinBatch b ) {
		this(1, new Range(root.key), b ) ;
    }
	
	public CatadupaTreeCast( int l, Range r, JoinBatch b ) {
		this.level = l ;
		this.range = r ;
		this.batch = b ;
    }
	
    final public void cRoute( ControlPacketRouter cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   
    
    public String toString() {
    	return String.format("%d %s %s", level, range, batch) ;
    }
	private static final long serialVersionUID = 1L;
}
