package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.DB;
import livefeeds.rtrees.GlobalDB;
import livefeeds.rtrees.SlidingBitSet;
import livefeeds.rtrees.View;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

import simsim.core.Simulation;

@SuppressWarnings("serial")
public class DbUploadEndpoints extends CatadupaMessage {

	public View view;
	public SlidingBitSet knownNodes, deadNodes;

	public double timeStamp = Simulation.currentTime() ;
	public double eta ;
	
	public DbUploadEndpoints(DB db) {
		this.view = db.view.clone();
//		this.deadNodes = db.deadNodes.clone();
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


	public void deliverTo( RpcCall call, RpcReplyHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.traffic.dbUploadEndpoints_upload += srcUpload ;		
			dstNode.state.stats.traffic.dbUploadEndpoints_download += srcUpload ;
			
			dstNode.state.stats.traffic.dbUploadEndpoints_upload += dstUpload; 
			srcNode.state.stats.traffic.dbUploadEndpoints_download += dstUpload ;
		}
		
		if( call.replyable )
			((CatadupaRpcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaRpcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true;
}
