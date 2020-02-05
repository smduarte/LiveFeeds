package livefeeds.rtrees.rpcs;

import simsim.core.Message;

public interface RpcHandler {
	
	public void onReceive( RpcCall call, Message m ) ;

}
