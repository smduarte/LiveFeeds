package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

@SuppressWarnings("serial")
public class DbUploadEndpointsRequest extends CatadupaMessage {

	public DbUploadEndpointsRequest() {
	}
	

	public int length() {
		if (length < 0)
			length = accountTraffic ? 0 : 0;
		return length;
	}

	public void deliverTo( RpcCall call, RpcHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead ) ;
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead ;
			
			dstNode.state.stats.traffic.dbUploadRequest_download += srcUpload;
			srcNode.state.stats.traffic.dbUploadRequest_upload += srcUpload;

			dstNode.state.stats.traffic.dbUploadRequest_upload += dstUpload;
			srcNode.state.stats.traffic.dbUploadRequest_download += dstUpload;
}
		
		((CatadupaRpcHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}