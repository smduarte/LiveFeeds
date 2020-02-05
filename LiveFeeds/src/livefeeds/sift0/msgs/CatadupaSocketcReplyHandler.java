package livefeeds.sift0.msgs;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

public interface CatadupaSocketcReplyHandler extends SocketReplyHandler {

	void onReply( DbUploadAccept m) ;

	void onReply( DbUploadReject m) ;

	void onReply( DbUploadFilters m ) ;	

	void onReply( DbUploadEndpoints m ) ;	

	void onReply( JoinRequestAccept m ) ;
	
	void onReply(DbRepairReply m ) ;

	void onReply(Socket call, DbRepairReply m ) ;
} 
