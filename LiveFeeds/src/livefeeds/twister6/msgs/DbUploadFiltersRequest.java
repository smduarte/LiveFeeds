package livefeeds.twister6.msgs;

import static livefeeds.twister6.config.Config.Config;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

import livefeeds.twister6.CatadupaNode;

@SuppressWarnings("serial")
public class DbUploadFiltersRequest extends CatadupaMessage {

	public final int piece;

	public DbUploadFiltersRequest() {
		this(-1);
	}

	public DbUploadFiltersRequest(int piece) {
		super(false);
		this.piece = piece;
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? 0 : 0;
		return length;
	}

	public void deliverTo(Socket call, SocketHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
			
			int bytes = length();
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead ) ;
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead ;
	
			srcNode.state.stats.traffic.dbUploadRequest_upload += srcUpload;
			dstNode.state.stats.traffic.dbUploadRequest_download += srcUpload;

			dstNode.state.stats.traffic.dbUploadRequest_upload += dstUpload;
			srcNode.state.stats.traffic.dbUploadRequest_download += dstUpload;
}
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
