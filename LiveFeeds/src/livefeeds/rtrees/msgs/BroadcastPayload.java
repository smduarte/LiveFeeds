package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.BroadcastNode;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;
import livefeeds.rtrees.rpcs.RpcMessage;

import simsim.core.Simulation;
import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class BroadcastPayload extends RpcMessage {

	public long src ;
	public int serial = g_serial++ ;
	Integer payload ;
	
	static int g_serial = 0 ;
	
	double timeStamp = Simulation.currentTime() ;
	
	public BroadcastPayload( long src, int length ) {
		super(false, RGB.RED);
		this.src = src ;
		this.payload = length ;
		this.length = length ;
	}

	public double latency() {
		return Simulation.currentTime() - timeStamp ;
	}
	
	public int length() {
		return length ;
	}
	
	public String toString() {
		return String.format("BroadcastPayload<%s>", payload);
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
