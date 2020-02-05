package livefeeds.rtrees.msgs;

import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

public interface CatadupaRpcReplyHandler extends RpcReplyHandler {

	void onReply( DbUploadAccept m) ;

	void onReply( DbUploadReject m) ;

	void onReply( DbUploadFilters m ) ;	

	void onReply( DbUploadEndpoints m ) ;	

	
	void onReply(DbRepairReply m ) ;

	void onReply(RpcCall call, DbRepairReply m ) ;
} 
