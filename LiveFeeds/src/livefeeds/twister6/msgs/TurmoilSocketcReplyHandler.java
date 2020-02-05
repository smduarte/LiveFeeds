package livefeeds.twister6.msgs;

import simsim.sockets.SocketReplyHandler;

public interface TurmoilSocketcReplyHandler extends SocketReplyHandler {

	void onReply( TurmoilAccept m) ;

	void onReply( TurmoilReject m) ;
} 
