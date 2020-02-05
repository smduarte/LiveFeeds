package livefeeds.rtrees.msgs;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

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
					
	public void deliverTo( RpcCall call, RpcReplyHandler handler) {

		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			dstNode.state.stats.traffic.dbUploadEndpoints_download += bytes ;
			srcNode.state.stats.traffic.dbUploadEndpoints_upload += bytes ;		
		}

		if( call.replyable )
			((CatadupaRpcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaRpcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
