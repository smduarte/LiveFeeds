package livefeeds.twister7.msgs;

import livefeeds.twister7.CatadupaNode;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class DbUploadFiltersRequest extends CatadupaMessage {

	public final long piece;

	public DbUploadFiltersRequest() {
		this(-1);
	}

	public DbUploadFiltersRequest(long piece) {
		super(false);
		this.piece = piece;
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? 1 : 0;
		
		return length;
	}

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
		srcNode.state.stats.catadupaTraffic.dbUploadFilters_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbUploadFilters_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.dbUploadFilters_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.dbUploadFilters_download += srcUpload;
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
