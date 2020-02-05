package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.Stamp;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcHandler;

import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class NewArrivals extends CatadupaMessage {

	public Stamp stamp;

	public int exits;
	public Collection<Integer> joins;
	public Collection<Integer> rejoins;
	public Collection<Integer> failures;

	public double timeStamp = currentTime();

	public Collection<Long> keys;

	public NewArrivals(Stamp s, Set<Integer> joins, Set<Integer> rejoins, Set<Long> keys, Set<Integer> failed) {
		super(false, RGB.ORANGE);
		this.stamp = s;
		this.joins = new ArrayList<Integer>(joins);
		this.rejoins = new ArrayList<Integer>(rejoins);
		this.keys = new ArrayList<Long>(keys);
		this.failures = new ArrayList<Integer>(failed);
		this.exits = failed.size();
	}

	public int bytes() {
		int res = 16 + 8 + 4;
		double w = Config.DB_FILTER_REDUNDANCY;
		res += joins.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE);
		return res ;
	}

	public int length() {
		if (length < 0) {
			length = accountTraffic ? bytes() : 0 ;
		}
		return length;
	}

	public String toString() {
		return String.format("NA<%s : %s, %s>", stamp, joins, rejoins);
	}

	public void deliverTo(RpcCall call, RpcHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.traffic.catadupaCasting_upload += srcUpload;
			dstNode.state.stats.traffic.catadupaCasting_download += srcUpload;

			dstNode.state.stats.traffic.catadupaCasting_upload += dstUpload;
			srcNode.state.stats.traffic.catadupaCasting_download += dstUpload;

			srcNode.state.stats.traffic.departure_upload += exits * Config.ENDPOINT_SIZE;
			dstNode.state.stats.traffic.departure_download += exits * Config.ENDPOINT_SIZE;
		}
		
		((CatadupaRpcHandler) handler).onReceive(call, this);

	}

	public static boolean accountTraffic = true;
}
