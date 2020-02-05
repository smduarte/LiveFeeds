package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.GlobalDB;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbUploadFilters extends CatadupaMessage {

	public long piece;

	public DbUploadFilters(long piece) {
		this.piece = piece;
	}

	public int length() {
		if (length < 0) {
			if (accountTraffic) {
				double w = Config.DB_FILTER_REDUNDANCY;
				int N = GlobalDB.size() / (1 << Config.SLICE_AGGREGATION_DEPTH) ;
				length = (int) ( N * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE) / Config.DB_FILTER_DOWNLOAD_PIECES);
			} else
				length = 0;
		}
		return length;
	}


	public void deliverTo(Socket call, SocketReplyHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			
			srcNode.state.stats.catadupaTraffic.dbUploadFilters_upload += srcUpload ;		
			dstNode.state.stats.catadupaTraffic.dbUploadFilters_download += srcUpload ;
			
			dstNode.state.stats.catadupaTraffic.dbUploadFilters_upload += dstUpload; 
			srcNode.state.stats.catadupaTraffic.dbUploadFilters_download += dstUpload ;
		}

		if (call.replyable)
			((CatadupaSocketcReplyHandler) handler).onReply(call, this);
		else
			((CatadupaSocketcReplyHandler) handler).onReply(this);
	}

	public static boolean accountTraffic = true;
}
