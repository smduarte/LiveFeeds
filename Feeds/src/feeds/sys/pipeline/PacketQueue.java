    package feeds.sys.pipeline;

import java.util.* ;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.packets.*;
import feeds.sys.transports.*;

public class PacketQueue extends BasicTransport implements Runnable {

	protected PacketQueue( String name ) {
		super( name + "://-/-/", "?") ;		
	} 
	
	public void send( cPacket p ) throws FeedsException {
		synchronized( queue ) {
			queue.addLast( p ) ;
			queue.notify() ;
		}
	}
	
	protected cPacket enqueue( cPacket p ) {
		synchronized( queue ) {
			queue.addLast( p ) ;
			queue.notify() ;
		}
		return p ;
	}
	
	public void run() {
		
		for(;;) {
			try {
				cPacket p ;
				synchronized( queue ) {
					while( queue.isEmpty() ) queue.wait() ;
					p = queue.removeFirst() ;
				}
				p.route( router ) ;
				
			} catch( Throwable x ) {
				x.printStackTrace() ;
			}
		}
	}
	
	synchronized protected void setRouter( Router<?, ?, ?, ?> r ) {
		this.router = r ;
		if( r != null && ! hasThread ) {
			hasThread = true ;
			FeedsNode.newThread( false, this).start() ;
		}
	}
	
	protected Router<?, ?, ?, ?> router ;
	protected boolean hasThread = false ;
	protected final LinkedList<cPacket> queue = new LinkedList<cPacket>() ;
}
