package livefeeds.sift0.msgs;

import livefeeds.sift0.CatadupaNode;

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
			length = accountTraffic ? 1 : 0 ;
		return length ;
	}
					
	public void deliverTo( Socket call, SocketReplyHandler handler) {

		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			dstNode.state.stats.catadupaTraffic.dbUploadEndpoints_download += bytes ;
			srcNode.state.stats.catadupaTraffic.dbUploadEndpoints_upload += bytes ;		
		}

		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
