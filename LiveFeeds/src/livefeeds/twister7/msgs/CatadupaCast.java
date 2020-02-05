package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.Range;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class CatadupaCast extends CatadupaMessage {

	public int level; // 1 byte
	public Range range; // 2 x 16 bytes ;
	public CatadupaCastPayload payload;

	public CatadupaCast(int level, Range range, CatadupaCastPayload payload) {
		super(false, RGB.RED);
		this.level = level;
		this.range = range.clone();
		this.payload = payload;
	}

	public int bytes() {
		return 1 + 2 * 16 + payload.bytes() ;
	}
	
	public int length() {
		if (length < 0 )
			length = accountTraffic ?  bytes() : 0 ;
			
		return length;
	}

	public String toString() {
		return String.format("CatadupaCast<%d, %s>", level, range);
	}

	public void accountTransfer(Socket call, double srcUpload, double dstUpload) {
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
//		System.out.println( "--->" + srcNode.address.uploadedBytes );

		srcNode.state.stats.catadupaTraffic.catadupaCasting_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.catadupaCasting_download += dstUpload;

		dstNode.state.stats.catadupaTraffic.catadupaCasting_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.catadupaCasting_download += srcUpload;

		int dep_bytes = payload.exits * Config.ENDPOINT_SIZE;
		srcNode.state.stats.catadupaTraffic.departure_upload += srcUploadWithOverhead(false, dep_bytes);
		dstNode.state.stats.catadupaTraffic.departure_download += srcUploadWithOverhead(false, dep_bytes);
	}

	public void deliverTo(Socket call, SocketHandler handler) {
		// payload.stamp.deliveries++ ;

		((CatadupaSocketHandler) handler).onReceive(call, this);
	}

	public static boolean accountTraffic = true;
}
