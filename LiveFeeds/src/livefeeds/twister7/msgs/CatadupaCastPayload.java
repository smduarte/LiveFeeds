package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.Stamp;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaCastPayload extends CatadupaMessage {

	public Stamp stamp;

	public int exits;
	public Collection<Integer> joins;
	public Collection<Integer> rejoins;
	public Collection<Integer> failures;

	public double timeStamp = currentTime();

	public Collection<Long> keys;

	public CatadupaCastPayload(Stamp s, Set<Integer> joins, Set<Integer> rejoins, Set<Long> keys, Set<Integer> failed) {
		super(false, RGB.ORANGE);
		this.stamp = s;
		this.joins = new ArrayList<Integer>(joins);
		this.rejoins = new ArrayList<Integer>(rejoins);
		this.keys = new ArrayList<Long>(keys);
		this.failures = new ArrayList<Integer>(failed);
		this.exits = failed.size();
	}

	public int bytes() {
		int res = 16 + 8 + 4 + 4; // stamp + #joins + #exits ;
		double w = Config.DB_FILTER_REDUNDANCY;
		res += joins.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE);
		res += exits * Config.ENDPOINT_SIZE ;
		return res ;
	}

	public int length() {
		if (length < 0 )
			length = accountTraffic ? bytes() : 0;
		
		return length;
	}

	public String toString() {
		return String.format("CC Payload<%s : %s, %s>", stamp, joins, rejoins);
	}

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

		srcNode.state.stats.catadupaTraffic.catadupaCasting_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.catadupaCasting_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.catadupaCasting_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.catadupaCasting_download += srcUpload;

		int dep_bytes = exits * Config.ENDPOINT_SIZE ;
		srcNode.state.stats.catadupaTraffic.departure_upload += srcUploadWithOverhead( false, dep_bytes );
		dstNode.state.stats.catadupaTraffic.departure_download += srcUploadWithOverhead( false, dep_bytes );
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
