package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.DB;
import livefeeds.sift0.GlobalDB;
import livefeeds.sift0.SlidingBitSet;
import livefeeds.sift0.View;

import simsim.core.Simulation;
import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbUploadEndpoints extends CatadupaMessage {

	public View view;
	public SlidingBitSet knownNodes, deadNodes;

	public double timeStamp = Simulation.currentTime() ;
	public double eta ;
	
	public DbUploadEndpoints(DB db) {
		this.view = db.view.clone();
		this.deadNodes = db.deadNodes.clone();
		this.knownNodes = db.knownNodes.clone();
	}

	
	public int length() {
		if (length < 0) {
			if( accountTraffic ) {
				length = (int)(GlobalDB.size() * Config.ENDPOINT_SIZE);
			} else
				length = 0;
		}
		return length;
	}	


	public void deliverTo( Socket call, SocketReplyHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.catadupaTraffic.dbUploadEndpoints_upload += srcUpload ;		
			dstNode.state.stats.catadupaTraffic.dbUploadEndpoints_download += srcUpload ;
			
			dstNode.state.stats.catadupaTraffic.dbUploadEndpoints_upload += dstUpload; 
			srcNode.state.stats.catadupaTraffic.dbUploadEndpoints_download += dstUpload ;
		}
		
		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true;
}
