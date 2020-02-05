package livefeeds.rtrees.msgs;

import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

public interface BroadcastRpcHandler extends RpcHandler {

	public void onReceive( RpcCall call, BroadcastMessage m) ;
	
	public void onReceive( RpcCall call, BroadcastPayload m) ;


	public void onReceive( RpcCall call, ChordBroadcastMessage m) ;

} 
