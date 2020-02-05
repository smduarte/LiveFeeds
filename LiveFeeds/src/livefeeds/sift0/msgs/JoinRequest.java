package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;

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

	
	public void deliverTo( Socket call, SocketHandler handler) {

		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			double srcUpload = Config.TcpAckOverhead + bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = Config.TcpAckOverhead + bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.catadupaTraffic.joinRequest_upload += srcUpload ;		
			dstNode.state.stats.catadupaTraffic.joinRequest_download += srcUpload ;
			
			dstNode.state.stats.catadupaTraffic.joinRequest_upload += dstUpload; 
			srcNode.state.stats.catadupaTraffic.joinRequest_download += dstUpload ;
		}		
		((CatadupaSocketHandler) handler).onReceive(call, this);
	}	
	public static boolean accountTraffic = true;
}