package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.GlobalDB;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

@SuppressWarnings("serial")
public class DbUploadFilters extends CatadupaMessage {

	public int piece;

	public DbUploadFilters(int piece) {
		this.piece = piece;
	}

	public int length() {
		if (length < 0) {
			if (accountTraffic) {
				double w = Config.DB_FILTER_REDUNDANCY;
				length = (int) (GlobalDB.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE) / Config.DB_FILTER_DOWNLOAD_PIECES);
			} else
				length = 0;
		}
		return length;
	}


	public void deliverTo(RpcCall call, RpcReplyHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			
			srcNode.state.stats.traffic.dbUploadFilters_upload += srcUpload ;		
			dstNode.state.stats.traffic.dbUploadFilters_download += srcUpload ;
			
			dstNode.state.stats.traffic.dbUploadFilters_upload += dstUpload; 
			srcNode.state.stats.traffic.dbUploadFilters_download += dstUpload ;
		}

		if (call.replyable)
			((CatadupaRpcReplyHandler) handler).onReply(call, this);
		else
			((CatadupaRpcReplyHandler) handler).onReply(this);
	}

	public static boolean accountTraffic = true;
}
