package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.Range;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class CatadupaCast extends CatadupaMessage {

	public int level;
	public Range range;
	public NewArrivals payload;

	public CatadupaCast(int level, Range range, NewArrivals payload) {
		super(false, RGB.RED);
		this.level = level;
		this.range = range.clone();
		this.payload = payload;
	}

	public int length() {
		if (length < 0)
			if (accountTraffic) {
				length = 1 + 2 * 16 + payload.bytes();
			} else
				length = 0;
		return length;
	}

	public String toString() {
		return String.format("CatadupaCast<%d, %s>", level, range);
	}

	public void deliverTo(RpcCall call, RpcHandler handler) {

//		payload.stamp.deliveries++ ;
		
		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();

			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.traffic.catadupaCasting_upload += srcUpload;
			dstNode.state.stats.traffic.catadupaCasting_download += srcUpload;

			dstNode.state.stats.traffic.catadupaCasting_upload += dstUpload;
			srcNode.state.stats.traffic.catadupaCasting_download += dstUpload;

			srcNode.state.stats.traffic.departure_upload += payload.exits * Config.ENDPOINT_SIZE;
			dstNode.state.stats.traffic.departure_download += payload.exits * Config.ENDPOINT_SIZE;
		}

		((CatadupaRpcHandler) handler).onReceive(call, this);
	}
	
	public static boolean accountTraffic = true;
}