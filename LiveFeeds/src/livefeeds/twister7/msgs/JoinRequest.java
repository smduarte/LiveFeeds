package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;

import simsim.core.Simulation;
import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;

@SuppressWarnings("serial")
public class JoinRequest extends CatadupaUpdate {
	
	public JoinRequest(long key, int index) {
		this(key, index, false);
	}

	public JoinRequest(long key, int index, boolean rejoin) {
		super(false, RGB.MAGENTA, Simulation.currentTime() < 15 * 60 ? 0 : Config.JOINS_AGGREGATION_DEPTH );
		
		if( rejoin )
			super.rejoins.add( index ) ;
		else
			super.joins.add( index ) ;

		super.keys.add( key ) ;
	}

	public JoinRequest(int level, long key, int index, boolean rejoin) {
		super(false, RGB.MAGENTA, level );
		
		if( rejoin )
			super.rejoins.add( index ) ;
		else
			super.joins.add( index ) ;

		super.keys.add( key ) ;
	}

	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

		srcNode.state.stats.catadupaTraffic.joinRequest_upload += srcUpload ;		
		srcNode.state.stats.catadupaTraffic.joinRequest_download += dstUpload ;
			
		dstNode.state.stats.catadupaTraffic.joinRequest_upload += dstUpload; 
		dstNode.state.stats.catadupaTraffic.joinRequest_download += srcUpload ;
	}

	
	public void deliverTo( Socket call, SocketHandler handler) {
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}
	
	public static boolean accountTraffic = true;
}