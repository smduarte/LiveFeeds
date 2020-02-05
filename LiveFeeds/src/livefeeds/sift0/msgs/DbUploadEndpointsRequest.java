package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class DbUploadEndpointsRequest extends CatadupaMessage {

	public DbUploadEndpointsRequest() {
	}
	

	public int length() {
		if (length < 0)
			length = accountTraffic ? 0 : 0;
		return length;
	}

	public void deliverTo( Socket call, SocketHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead ) ;
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead ;
			
			dstNode.state.stats.catadupaTraffic.dbUploadRequest_download += srcUpload;
			srcNode.state.stats.catadupaTraffic.dbUploadRequest_upload += srcUpload;

			dstNode.state.stats.catadupaTraffic.dbUploadRequest_upload += dstUpload;
			srcNode.state.stats.catadupaTraffic.dbUploadRequest_download += dstUpload;
}
		
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
