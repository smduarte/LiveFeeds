package feeds.sys.catadupa.packets;

import java.util.*;

import feeds.sys.catadupa.*;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class Repair_Reply extends CatadupaControlPacket {    	


    public Set<JoinBatch> jbs = new HashSet<JoinBatch>() ;

    public Repair_Reply( Set<JoinBatch> s ) {
    	for( JoinBatch i : s )
    		jbs.add( i.clone() ) ;
    }
    
    public void cRoute( ControlPacketRouter cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   

    private static final long serialVersionUID = 1L;
}
