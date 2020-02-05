package meeds.sys.tunnel;

import java.util.*;

import meeds.sys.MeedsNode;

import feeds.api.*;
import feeds.sys.core.*;

public class MembershipPacket extends TunnelControlPacket {

	final ID src ;
	final Map<ID, Set<Criteria<?>>> pCriteria, fCriteria ;
	
	public MembershipPacket( Map<ID, Set<Criteria<?>>> pC, Map<ID, Set<Criteria<?>>> fC ) {
		this.pCriteria = pC ;
		this.fCriteria = fC ;
		src = MeedsNode.id() ;
	}

	
	final public void cRoute( TunnelPacketRouter<?, ?, ?, ?> r ) throws Exception {        
        r.cRoute( this ) ;
    }  
	
	public String toString() {
		return String.format("MBP src: %s (%d)", src, serial) ;
	}

	public MembershipPacket setSerial() {
		serial = g_serial++ ;
		return this ;
	}
	
	private int serial = -1 ;	
	private static int g_serial = 0 ;
	private static final long serialVersionUID = 1L;
}