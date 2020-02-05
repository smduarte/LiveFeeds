package livefeeds.rtrees.rpcs;

import simsim.core.Message;

public interface RpcReplyHandler {

	public void onReply( Message m ) ;

	public void onReply( RpcCall call, Message m ) ;

	public void onFailure() ;
}
