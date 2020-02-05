package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.Stamp;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaCastPayload extends CatadupaMessage {

	public Stamp stamp;

	public Collection<Integer> joins;
	public Collection<Integer> rejoins;
	public Collection<Integer> failures;

	public double timeStamp = currentTime();

	public Collection<Long> keys; // for DEBUGGING

	public CatadupaCastPayload(Stamp s, Set<Integer> joins, Set<Integer> rejoins, Set<Long> keys, Set<Integer> failed) {
		super(false, RGB.ORANGE);
		this.stamp = s;
		this.joins = new ArrayList<Integer>(joins);
		this.rejoins = new ArrayList<Integer>(rejoins);
		this.keys = new ArrayList<Long>(keys);
		this.failures = new ArrayList<Integer>(failed);
	}

	public int bytes() {
		int res = 16 + 8 + 4;
		res += joins.size() * (Config.ENDPOINT_SIZE + Config.FILTER_KEY_SIZE);
		return res ;
	}

	public int length() {
		if (length < 0) {
			length = accountTraffic ? bytes() : 0 ;
		}
		return length;
	}

	public String toString() {
		return String.format("CC Payload<%s : %s, %s>", stamp, joins, rejoins);
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

			int exits = failures.size() ;
			srcNode.state.stats.catadupaTraffic.departure_upload += exits * Config.ENDPOINT_SIZE;
			dstNode.state.stats.catadupaTraffic.departure_download += exits * Config.ENDPOINT_SIZE;
		}
		
		((CatadupaSocketHandler) handler).onReceive(call, this);

	}

	public static boolean accountTraffic = true;
}
