package livefeeds.twister7.msgs;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

public interface TurmoilSocketHandler extends SocketHandler {

	void onReceive( Socket sock, TurmoilAccept m) ;

	void onReceive( Socket sock, TurmoilReject m) ;

	void onReceive( Socket sock, TurmoilCast m) ;	

	void onReceive( Socket sock, TurmoilPayload m) ;	

}
