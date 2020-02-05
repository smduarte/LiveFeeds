package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;

import java.util.Set;

import livefeeds.twister7.CatadupaNode;

import simsim.core.EndPoint;
import simsim.core.MessageHandler;
import simsim.core.Simulation;
import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class FailureNotice extends CatadupaUpdate {

	public FailureNotice(int index) {
		super(false, RGB.MAGENTA, Simulation.currentTime() < 15 * 60 ? 0 : Config.EXITS_AGGREGATION_DEPTH );
		
		super.failures.add( index ) ;
		super.exits = 1 ;
	}

	public FailureNotice(Set<Integer> failures) {
		super(false, RGB.MAGENTA, Simulation.currentTime() < 15 * 60 ? 0 : Config.EXITS_AGGREGATION_DEPTH );
		
		super.failures.addAll( failures ) ;
		super.exits = failures.size() ;
	}

	public FailureNotice(int level, Set<Integer> failures) {
		super(false, RGB.MAGENTA, level );
		
		super.failures.addAll( failures ) ;
		super.exits = failures.size() ;
	}
	
	public String toString() {
		return String.format("FailureNotice <%d>", super.exits );
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		CatadupaNode dstNode = (CatadupaNode) handler, srcNode = (CatadupaNode) src.handler;

		if (dstNode != srcNode && accountTraffic ) {
			
			double srcUpload = (accountTraffic ? 1 : 0) * ( length() + Config.UdpHeaderLength);
	
			srcNode.state.stats.catadupaTraffic.departure_upload += srcUpload;
			dstNode.state.stats.catadupaTraffic.departure_download += srcUpload;
		}
		
		dstNode.onReceive(src, (CatadupaUpdate)this);
	}

	public static boolean accountTraffic = true;
}