package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.sift0.CatadupaNode;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaUpdate extends CatadupaMessage {

	public int level ;
	public Collection<Integer> joins;
	public Collection<Integer> rejoins;
	public Collection<Integer> failures;

	public double timeStamp = currentTime();

	public Collection<Long> keys;

	public CatadupaUpdate(int level, Set<Integer> joins, Set<Integer> rejoins, Set<Long> keys, Set<Integer> failed) {
		super(false, RGB.ORANGE);
		this.level = level ;
		this.joins = new ArrayList<Integer>(joins);
		this.rejoins = new ArrayList<Integer>(rejoins);
		this.keys = new ArrayList<Long>(keys);
		this.failures = new ArrayList<Integer>(failed);
	}

	protected CatadupaUpdate(boolean visible, RGB color, int level ) {
		super( visible, color) ;
		this.level = level ;
		this.keys = new ArrayList<Long>();
		this.joins = new ArrayList<Integer>();
		this.rejoins = new ArrayList<Integer>();
		this.failures = new ArrayList<Integer>();
	}
	
	public int bytes() {
		int res = 4 + 6 ;
		if( level > Config.SLICE_AGGREGATION_DEPTH ) {
			double w = Config.DB_FILTER_REDUNDANCY;
			res += joins.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE);
			return res ;			
		} else {
			res += joins.size() * (Config.ENDPOINT_SIZE + Config.FILTER_KEY_SIZE);
			return res ;						
		}
	}

	public int length() {
		if (length < 0) {
			length = accountTraffic ? bytes() : 0 ;
		}
		return length;
	}

	public String toString() {
		return String.format("Catadupa Update<%s, %s>", joins, rejoins);
	}

	public void deliverTo(Socket call, SocketHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.catadupaTraffic.joinRequest_upload += srcUpload;
			dstNode.state.stats.catadupaTraffic.joinRequest_download += srcUpload;

			dstNode.state.stats.catadupaTraffic.joinRequest_upload += dstUpload;
			srcNode.state.stats.catadupaTraffic.joinRequest_download += dstUpload;

			int exits = failures.size() ;
			srcNode.state.stats.catadupaTraffic.departure_upload += exits * Config.ENDPOINT_SIZE;
			dstNode.state.stats.catadupaTraffic.departure_download += exits * Config.ENDPOINT_SIZE;
		}
		
		((CatadupaSocketHandler) handler).onReceive(call, this);

	}

	public static boolean accountTraffic = true;
}
