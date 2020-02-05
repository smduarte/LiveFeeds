package livefeeds.twister7.msgs;

import livefeeds.twister7.CatadupaNode;

import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class DbUploadEndpointsRequest extends CatadupaMessage {

	public DbUploadEndpointsRequest() {
	}
	

	public int length() {
		if (length < 0)
			length = accountTraffic ? 1 : 0;
		return length;
	}

	
	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

		srcNode.state.stats.catadupaTraffic.dbUploadRequest_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbUploadRequest_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.dbUploadRequest_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.dbUploadRequest_download += srcUpload;
	}

	public void deliverTo( Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
