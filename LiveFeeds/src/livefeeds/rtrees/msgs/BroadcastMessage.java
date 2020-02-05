package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;

import simsim.gui.canvas.RGB;
import livefeeds.rtrees.BroadcastNode;
import livefeeds.rtrees.Range;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;
import livefeeds.rtrees.rpcs.RpcMessage;

@SuppressWarnings("serial")
public class BroadcastMessage extends RpcMessage {
		
	public int level;
	public Range range;
	public BroadcastPayload payload;

	public BroadcastMessage(int level, Range range, BroadcastPayload payload ) {
		super(false, RGB.RED);
		this.level = level;
		this.range = range.clone();
		this.payload = payload;
	}

	public int length() {
		return length = payload.length() ;
	}

	public String toString() {
		return String.format("BroadcastMessage<%d, %s>", level, range);
	}

	public void deliverTo(RpcCall call, RpcHandler handler) {

		if (call.src != call.dst) {
			BroadcastNode srcNode = call.srcNode(), dstNode = call.dstNode();

			double bytes = length();
			double overhead = 0 ; // bytes > 1500 ? Config.TcpAckOverhead : 0 ;
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + overhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * overhead;
			
			srcNode.state.stats.btraffic.broadcast_upload += srcUpload;
			dstNode.state.stats.btraffic.broadcast_download += srcUpload;

			dstNode.state.stats.btraffic.broadcast_upload += dstUpload;
			srcNode.state.stats.btraffic.broadcast_download += dstUpload;

		}

		((BroadcastRpcHandler) handler).onReceive(call, this);
	}	
}
