package livefeeds.rtrees.stats.broadcast;

import java.util.LinkedList;

import simsim.core.Simulation;

public class BroadcastTraffic implements Cloneable {
	
	LinkedList<BroadcastTraffic> history = new LinkedList<BroadcastTraffic>() ;
	
	public BroadcastTraffic() {
		updateHistory() ;
	}
	
	public double timeStamp = Simulation.currentTime() ;
	public double broadcast_upload = 0, broadcast_download = 0;
		
	public double upload() {
		return broadcast_upload ;
	}
	
	public double download() {
		return broadcast_download;
	}
	
	public double recent_upload_rate() {
		
		BroadcastTraffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.upload() - other.upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_download_rate() {
		BroadcastTraffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.download() - other.download() ;
		
		return total / elapsed ;
	}
		
	public void updateHistory() {

		history.addFirst( clone() ) ;
		while( history.size() > 3 )
			history.removeLast() ;
		
	}
	
	public BroadcastTraffic clone() {
		try {
			timeStamp = Simulation.currentTime() ;			
			return (BroadcastTraffic)super.clone() ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace() ;
			return null ;
		}
	}
}
