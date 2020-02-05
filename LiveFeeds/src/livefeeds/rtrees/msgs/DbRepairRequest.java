package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.View;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class DbRepairRequest extends CatadupaMessage {

	public View view;

	public DbRepairRequest(View view) {
		super(false, RGB.GREEN);
		this.view = view.clone();
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? view.length() : 0;

		return length;
	}

	public void deliverTo(RpcCall call, RpcHandler handler) {
		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead ) ;
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead ;
						
			srcNode.state.stats.traffic.dbRepairReply_upload += srcUpload;
			dstNode.state.stats.traffic.dbRepairReply_download += srcUpload ;

			dstNode.state.stats.traffic.dbRepairReply_upload += dstUpload;
			srcNode.state.stats.traffic.dbRepairReply_download += dstUpload ;		
		}
		((CatadupaRpcHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
