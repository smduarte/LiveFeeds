package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.Path;
import livefeeds.sift0.Range;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class SliceCast extends CatadupaMessage {

	public int level;
	public Path path ;
	public Range range;
	public SliceCastPayload payload;

	public SliceCast(int level, Range range, Path path, SliceCastPayload payload) {
		super(false, RGB.RED);
		this.level = level;
		this.path = path ;
		this.range = range.clone();
		this.payload = payload;
	}

	public int length() {
		if (length < 0)
			if (accountTraffic) {
				length = 1 + (2 + path.size()) * 16 + payload.bytes();
			} else
				length = 0;
		return length;
	}

	public String toString() {
		return String.format("SliceCast<%d, %s>", level, range);
	}

	public void deliverTo(Socket call, SocketHandler handler) {

//		payload.stamp.deliveries++ ;
		
		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();

			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.catadupaTraffic.sliceCasting_upload += srcUpload;
			dstNode.state.stats.catadupaTraffic.sliceCasting_download += srcUpload;

			dstNode.state.stats.catadupaTraffic.sliceCasting_upload += dstUpload;
			srcNode.state.stats.catadupaTraffic.sliceCasting_download += dstUpload;

//			srcNode.state.stats.catadupaTraffic.departure_upload += payload.exits * Config.ENDPOINT_SIZE;
//			dstNode.state.stats.catadupaTraffic.departure_download += payload.exits * Config.ENDPOINT_SIZE;
		}

		((CatadupaSocketHandler) handler).onReceive(call, this);
	}
	
	public static boolean accountTraffic = true;
}
