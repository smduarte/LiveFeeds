package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;

import simsim.core.EndPoint;
import simsim.core.MessageHandler;
import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class DepartureNotice extends CatadupaMessage {

	public final long key;
	public final int index;

	public DepartureNotice(long key, int index) {
		super(false, RGB.MAGENTA);
		this.index = index;
		this.key = key;
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? Config.ENDPOINT_SIZE : 0;
		return length;
	}

	public String toString() {
		return String.format("Leave <%d>", index);
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		CatadupaNode dstNode = (CatadupaNode) handler, srcNode = (CatadupaNode) src.handler;

		double bytes = length() + Config.UdpHeaderLength;

		srcNode.state.stats.traffic.departure_upload += bytes;
		dstNode.state.stats.traffic.departure_download += bytes;

		dstNode.onReceive(src, this);
	}

	public static boolean accountTraffic = true;
}