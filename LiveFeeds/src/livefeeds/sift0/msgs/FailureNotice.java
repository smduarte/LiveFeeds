package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;

import java.util.Set;

import livefeeds.sift0.CatadupaNode;

import simsim.core.EndPoint;
import simsim.core.MessageHandler;
import simsim.core.Simulation;
import simsim.gui.canvas.RGB;

@SuppressWarnings("serial")
public class FailureNotice extends CatadupaUpdate {

	public FailureNotice(int index) {
		super(false, RGB.MAGENTA, Simulation.currentTime() < 15 * 60 ? 0 : Config.EXITS_AGGREGATION_DEPTH );
		super.failures.add( index ) ;
	}

	public FailureNotice(Set<Integer> failures) {
		super(false, RGB.MAGENTA, Simulation.currentTime() < 15 * 60 ? 0 : Config.EXITS_AGGREGATION_DEPTH );
		
		super.failures.addAll( failures ) ;
	}

	public FailureNotice(int level, Set<Integer> failures) {
		super(false, RGB.MAGENTA, level );		
		super.failures.addAll( failures ) ;
	}
	
	public String toString() {
		return String.format("FailureNotice <%d>", super.failures.size() );
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		CatadupaNode dstNode = (CatadupaNode) handler, srcNode = (CatadupaNode) src.handler;

		double bytes = length() + Config.UdpHeaderLength;

		srcNode.state.stats.catadupaTraffic.departure_upload += bytes;
		dstNode.state.stats.catadupaTraffic.departure_download += bytes;

		dstNode.onReceive(src, (CatadupaUpdate)this);
	}

	public static boolean accountTraffic = true;
}