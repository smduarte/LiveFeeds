package livefeeds.twister6.msgs;

import static livefeeds.twister6.config.Config.Config;
import static simsim.core.Simulation.currentTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.Stamp;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class Departures extends CatadupaMessage {

	public Collection<Integer> departures;
	public double timeStamp = currentTime();


	public Departures( Set<Integer> failed) {
		super(false, RGB.ORANGE);
		this.departures = new ArrayList<Integer>(failed);
	}

	public int bytes() {
		return 1 + departures .size() * Config.ENDPOINT_SIZE;
	}

	public int length() {
		if (length < 0) {
			length = accountTraffic ? bytes() : 0 ;
		}
		return length;
	}

	public String toString() {
		return String.format("Departures<%s : %s, %s>", departures);
	}

	public void deliverTo(Socket call, SocketHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead);
//			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead;

			srcNode.state.stats.traffic.departure_upload += srcUpload ;
			dstNode.state.stats.traffic.departure_download += srcUpload ;
		}
		
		((CatadupaSocketHandler) handler).onReceive(call, this);

	}

	public static boolean accountTraffic = true;
}
