package livefeeds.twister7.msgs;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbUploadAccept extends CatadupaMessage {
	

	public double eta ;
	
	public DbUploadAccept( double eta ){
		this.eta = eta ;
	} 
		
	public int length() {
		if( length < 0 )
			length =  0 ;
		return length ;
	}
				
	public void deliverTo( Socket call, SocketReplyHandler handler) {
		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}
}
