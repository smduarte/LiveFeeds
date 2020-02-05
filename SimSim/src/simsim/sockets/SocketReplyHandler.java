package simsim.sockets;

import simsim.core.Message;

public interface SocketReplyHandler {

	public void onReply( Message m ) ;

	public void onReply( Socket call, Message m ) ;

	public void onFailure() ;
}
