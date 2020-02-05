package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.Path;
import livefeeds.sift0.Range;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaCast extends CatadupaMessage {

	public int level;
	public Path path;
	public Range range;
	public CatadupaCastPayload payload;
	
	public CatadupaCast(int level, Range range, Path path, CatadupaCastPayload payload) {
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
		return String.format("CatadupaCast<%d, %s>", level, range);
	}

	public void deliverTo(Socket call, SocketHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();

			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.catadupaTraffic.catadupaCasting_upload += srcUpload;
			dstNode.state.stats.catadupaTraffic.catadupaCasting_download += srcUpload;

			dstNode.state.stats.catadupaTraffic.catadupaCasting_upload += dstUpload;
			srcNode.state.stats.catadupaTraffic.catadupaCasting_download += dstUpload;

			int exits = payload.failures.size() ;
			srcNode.state.stats.catadupaTraffic.departure_upload += exits * Config.ENDPOINT_SIZE;
			dstNode.state.stats.catadupaTraffic.departure_download += exits * Config.ENDPOINT_SIZE;
		}

		((CatadupaSocketHandler) handler).onReceive(call, this);
	}
	
	public static boolean accountTraffic = true;
}
