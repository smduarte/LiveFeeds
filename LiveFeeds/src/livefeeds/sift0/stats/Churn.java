package livefeeds.sift0.stats;

import java.util.LinkedList;

import simsim.core.Simulation;

public class Churn implements Cloneable {
	
	LinkedList<Churn> history = new LinkedList<Churn>() ;
	
	Churn() {
		updateHistory() ;
	}
	
	public double timeStamp = Simulation.currentTime() ;
	
	public double total_joins = 0 ;	
	public double total_departures = 0;
	
	public double recentJoinRate() {
		Churn other = history.getLast() ;
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.total_joins - other.total_joins ;		
		return total / elapsed ;
	}

	public double recentDepartureRate() {
		Churn other = history.getLast() ;
		double elapsed = Simulation.currentTime() - other.timeStamp ;
		double total = this.total_departures - other.total_departures ;		
		return total / elapsed ;
	}
	

	
	public void updateHistory() {
		history.addFirst( clone() ) ;
		while( history.size() > 4 )
			history.removeLast() ;	
	}
	
	public Churn clone() {
		try {
			timeStamp = Simulation.currentTime() ;			
			return (Churn) super.clone() ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace() ;
			return null ;
		}
	}
}
