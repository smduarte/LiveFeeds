package livefeeds.sift0.stats.turmoil;

import java.util.LinkedList;

import simsim.core.Simulation;

public class TurmoilTraffic implements Cloneable {
	
	LinkedList<TurmoilTraffic> history = new LinkedList<TurmoilTraffic>() ;
	
	public TurmoilTraffic() {
		updateHistory() ;
	}
	
	public double timeStamp = Simulation.currentTime() ;
	
	public double turmoil_upload = 0, turmoil_download = 0;
	
	public double upload() {
		return turmoil_upload;
	}
	
	public double download() {
		return turmoil_download;
	}

	public double average_upload_rate() {
		
		double elapsed = Simulation.currentTime() - timeStamp ;
		return upload() / elapsed ;
	}
	
	public double average_download_rate() {
		double elapsed = Simulation.currentTime() - timeStamp ;		
		return download() / elapsed ;
	}

	public double recent_upload_rate() {
		
		TurmoilTraffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.upload() - other.upload() ;
		
		return total / elapsed ;
	}
	
	public double recent_download_rate() {
		TurmoilTraffic other = history.getLast() ;
		
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.download() - other.download() ;
		
		return total / elapsed ;
	}
	
	
	public void updateHistory() {

		history.addFirst( clone() ) ;
		while( history.size() > 3 )
			history.removeLast() ;
		
	}
	
	
	public TurmoilTraffic clone() {
		try {
			timeStamp = Simulation.currentTime() ;			
			return (TurmoilTraffic)super.clone() ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace() ;
			return null ;
		}
	}
}
