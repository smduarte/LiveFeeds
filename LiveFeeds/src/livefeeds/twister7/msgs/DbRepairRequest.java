package livefeeds.twister7.msgs;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.View;

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

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
		srcNode.state.stats.catadupaTraffic.dbRepairReply_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbRepairReply_download += dstUpload ;		

		dstNode.state.stats.catadupaTraffic.dbRepairReply_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.dbRepairReply_download += srcUpload ;
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
