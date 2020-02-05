package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.View;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class DbRepairRequest extends CatadupaMessage {

	public View view;
	public double time2join ;
	
	public DbRepairRequest(View view) {
		super(false, RGB.GREEN);
		this.view = view.clone();
	}

	public int length() {
		if (length < 0)
			length = accountTraffic ? view.length() : 0;

		return length;
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			double srcUpload = Config.TcpHandshakeOverhead + bytes * (1 + Config.TcpAckOverhead ) ;
			double dstUpload = Config.TcpHandshakeOverhead + bytes * Config.TcpAckOverhead ;
						
			srcNode.state.stats.catadupaTraffic.dbRepairReply_upload += srcUpload;
			dstNode.state.stats.catadupaTraffic.dbRepairReply_download += srcUpload ;

			dstNode.state.stats.catadupaTraffic.dbRepairReply_upload += dstUpload;
			srcNode.state.stats.catadupaTraffic.dbRepairReply_download += dstUpload ;		
		}
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
