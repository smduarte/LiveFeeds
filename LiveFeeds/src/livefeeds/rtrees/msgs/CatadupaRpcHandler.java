package livefeeds.rtrees.msgs;

import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

public interface CatadupaRpcHandler extends RpcHandler {

	public void onReceive( RpcCall call, CatadupaCast m) ;

	public void onReceive( RpcCall call, NewArrivals m) ;

	public void onReceive(RpcCall call, JoinRequest m) ;
	
	public void onReceive(RpcCall call, DbRepairRequest m) ;

	public void onReceive( RpcCall call, DbUploadFiltersRequest m) ;
	
	public void onReceive( RpcCall call, DbUploadEndpointsRequest m) ;


} 
