package livefeeds.twister6.msgs;

import static livefeeds.twister6.config.Config.Config;
import livefeeds.twister6.CatadupaNode;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class JoinRequest extends CatadupaMessage {

	final public long key;
	final public int index;
	final public boolean rejoin;

	public JoinRequest(long key, int index) {
		this(key, index, false);
	}

	public JoinRequest(long key, int index, boolean rejoin) {
		super(false, RGB.MAGENTA);
		this.rejoin = rejoin;
		this.index = index;
		this.key = key;
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? (rejoin ? Config.FILTER_KEY_SIZE: Config.FILTER_DATA_SIZE) : 0;
		return length;
	}

	public String toString() {
		return String.format("Join <%d>", index);
	}

	public void deliverTo( Socket call, SocketHandler handler) {

		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			double srcUpload = Config.TcpAckOverhead + bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = Config.TcpAckOverhead + bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.traffic.joinRequest_upload += srcUpload ;		
			dstNode.state.stats.traffic.joinRequest_download += srcUpload ;
			
			dstNode.state.stats.traffic.joinRequest_upload += dstUpload; 
			srcNode.state.stats.traffic.joinRequest_download += dstUpload ;
		}		
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}	
	public static boolean accountTraffic = true;
}