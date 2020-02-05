package livefeeds.sift0.msgs;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;
import simsim.sockets.SocketMessage;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class TurmoilAccept extends SocketMessage {
		
	public final int id ;
	public final long key ;
	
	public TurmoilAccept( long key, int id ) {
		super(false, RGB.RED ) ;
		this.id = id ;
		this.key = key ;
	}

	public int length() {
		if (length < 0)
			length = 0;
		return length;
	}

	
	public String toString() {
		return String.format("PubSubEvent Accept<%d>", id) ;
	}
	
	public void deliverTo( Socket sock, SocketReplyHandler handler) {
		
		if( sock.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( sock, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}
	
	public void deliverTo(Socket sock, SocketHandler handler) {
		((TurmoilSocketHandler) handler).onReceive(sock, this);
	}
}
