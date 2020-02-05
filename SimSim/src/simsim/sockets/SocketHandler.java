package simsim.sockets;

import simsim.core.Message;

public interface SocketHandler {
	
	public void onReceive( Socket call, Message m ) ;

}
