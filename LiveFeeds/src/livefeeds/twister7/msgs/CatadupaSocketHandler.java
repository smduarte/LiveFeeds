package livefeeds.twister7.msgs;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

public interface CatadupaSocketHandler extends SocketHandler {

	public void onReceive( Socket call, CatadupaCast m) ;

	public void onReceive( Socket call, CatadupaCastPayload m) ;

	public void onReceive(Socket call, JoinRequest m) ;
	
	public void onReceive(Socket call, DbRepairRequest m) ;

	public void onReceive( Socket call, DbUploadFiltersRequest m) ;
	
	public void onReceive( Socket call, DbUploadEndpointsRequest m) ;
		
	public void onReceive( Socket call, CatadupaUpdate m) ;

} 
