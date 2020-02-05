package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;

import simsim.gui.canvas.RGB;
import livefeeds.rtrees.BroadcastNode;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;
import livefeeds.rtrees.rpcs.RpcMessage;

@SuppressWarnings("serial")
public class ChordBroadcastMessage extends RpcMessage {
		
	public int level;
	public int payload;
	public int serial ;	
	static int g_serial = 0 ;

	public ChordBroadcastMessage(int level, int payload) {
		super(false, RGB.RED);
		this.level = level;
		this.payload = payload ;
		this.serial = g_serial++ ;
	}

	public ChordBroadcastMessage(int level, int payload, int serial) {
		super(false, RGB.RED);
		this.level = level;
		this.payload = payload ;
		this.serial = serial ;
	}

	
	public int length() {
		return length = payload ;
	}

	public String toString() {
		return String.format("ChordBroadcastMessage<%d, %s>", level);
	}

	public void deliverTo(RpcCall call, RpcHandler handler) {

		if (call.src != call.dst) {
			BroadcastNode srcNode = call.srcNode(), dstNode = call.dstNode();

			double bytes = length();

			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.btraffic.broadcast_upload += srcUpload;
			dstNode.state.stats.btraffic.broadcast_download += srcUpload;

			dstNode.state.stats.btraffic.broadcast_upload += dstUpload;
			srcNode.state.stats.btraffic.broadcast_download += dstUpload;

		}

		((BroadcastRpcHandler) handler).onReceive(call, this);
	}	
}
