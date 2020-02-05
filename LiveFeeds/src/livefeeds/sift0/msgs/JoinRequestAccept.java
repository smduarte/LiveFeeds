package livefeeds.sift0.msgs;

import livefeeds.sift0.CatadupaNode;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class JoinRequestAccept extends CatadupaMessage {
	
	public double eta ;
	
	public JoinRequestAccept( double eta ){
		this.eta = eta ;
	} 
		
	public int length() {
		if( length < 0 )
			length = accountTraffic ? 1 : 0 ;
		return length ;
	}
					
	public void deliverTo( Socket call, SocketReplyHandler handler) {

		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			dstNode.state.stats.catadupaTraffic.joinRequest_download += bytes ;
			srcNode.state.stats.catadupaTraffic.joinRequest_upload += bytes ;		
		}

		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
