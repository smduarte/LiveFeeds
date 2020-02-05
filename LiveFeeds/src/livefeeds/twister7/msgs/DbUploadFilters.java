package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.GlobalDB;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbUploadFilters extends CatadupaMessage {

	public long piece;

	public DbUploadFilters(long piece) {
		this.piece = piece;
	}

	public int bytes() {
		final double w = Config.DB_FILTER_REDUNDANCY;
		return (int) (GlobalDB.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE) / Config.DB_FILTER_DOWNLOAD_PIECES);		
	}
	
	public int length() {
		if (length < 0)
			length = accountTraffic ? bytes() : 0 ;

			return length;
	}

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
		srcNode.state.stats.catadupaTraffic.dbUploadFilters_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbUploadFilters_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.dbUploadFilters_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.dbUploadFilters_download += srcUpload;
	}


	public void deliverTo(Socket call, SocketReplyHandler handler) {
		if (call.replyable)
			((CatadupaSocketcReplyHandler) handler).onReply(call, this);
		else
			((CatadupaSocketcReplyHandler) handler).onReply(this);
	}

	public static boolean accountTraffic = true;
}
