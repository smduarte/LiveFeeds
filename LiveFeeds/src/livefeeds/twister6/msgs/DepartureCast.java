package livefeeds.twister6.msgs;

import static livefeeds.twister6.config.Config.Config;
import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.Range;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class DepartureCast extends CatadupaMessage {

	public long root ;
	public int level;
	public Range range;
	public Departures payload;

	public DepartureCast(int level, long root, Range range, Departures payload) {
		super(false, RGB.RED);

		this.root = root ;
		this.level = level;
		this.range = range.clone();
		this.payload = payload;
	}

	public int length() {
		if (length < 0)
			if (accountTraffic) {
				length = 1 + 16 + 2 * 16 + payload.bytes();
			} else
				length = 0;
		return length;
	}

	public String toString() {
		return String.format("CatadupaCast<%d, %s>", level, range);
	}

	public void deliverTo(Socket call, SocketHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();

			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;
			
			srcNode.state.stats.traffic.departure_upload += srcUpload ;
			dstNode.state.stats.traffic.departure_download += srcUpload ;

			dstNode.state.stats.traffic.departure_download += dstUpload;
			srcNode.state.stats.traffic.departure_upload += dstUpload;			
		}

		((CatadupaSocketHandler) handler).onReceive(call, this);
	}
	
	public static boolean accountTraffic = true;
}
