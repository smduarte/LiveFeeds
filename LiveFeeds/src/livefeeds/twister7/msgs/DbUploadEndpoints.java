package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.DB;
import livefeeds.twister7.GlobalDB;
import livefeeds.twister7.SlidingBitSet;
import livefeeds.twister7.View;

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

	
	public int bytes() {
		return (int)((GlobalDB.liveNodesSize() + GlobalDB.deadNodesSize())* Config.ENDPOINT_SIZE);
	}	

	public int length() {
		if (length < 0  ) 
			length = accountTraffic ? bytes() : 0 ;

		return length;
	}
	
	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
	
		srcNode.state.stats.catadupaTraffic.dbUploadEndpoints_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbUploadEndpoints_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.dbUploadEndpoints_upload += dstUpload;			
		dstNode.state.stats.catadupaTraffic.dbUploadEndpoints_download += srcUpload;			
	}

	public void deliverTo( Socket call, SocketReplyHandler handler) {

		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;

	}

	public static boolean accountTraffic = true;
}
