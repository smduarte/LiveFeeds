package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.twister7.CatadupaNode;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaUpdate extends CatadupaMessage {

	public int level ;
	public int exits;
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
		this.exits = failed.size();
	}

	protected CatadupaUpdate(boolean visible, RGB color, int level ) {
		super( visible, color) ;
		this.level = level ;
		this.keys = new ArrayList<Long>();
		this.joins = new ArrayList<Integer>();
		this.rejoins = new ArrayList<Integer>();
		this.failures = new ArrayList<Integer>();
		this.exits = 0 ;
	}
	
	public int bytes() {
		int res = 1 + 4 + 4;
		double w = Config.DB_FILTER_REDUNDANCY;
		res += joins.size() * (Config.ENDPOINT_SIZE + (1 - w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE);
		res += exits * Config.ENDPOINT_SIZE ;
		return res ;
	}

	public int length() {
		if (length < 0 )
			length = accountTraffic ? bytes() : 0 ;
		
		return length;
	}

	public String toString() {
		return String.format("Catadupa Update<%s, %s>", joins, rejoins);
	}

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

		srcNode.state.stats.catadupaTraffic.joinRequest_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.joinRequest_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.joinRequest_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.joinRequest_download += srcUpload;

		int dep_bytes = exits * Config.ENDPOINT_SIZE ;

		srcNode.state.stats.catadupaTraffic.departure_upload += srcUploadWithOverhead( false, dep_bytes );
		dstNode.state.stats.catadupaTraffic.departure_download += srcUploadWithOverhead( false, dep_bytes );
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
