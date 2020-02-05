package livefeeds.sift0.stats.turmoil;

public class TurmoilFilterStats {
	
	public double accepted_events = 0 ;
	public double rejected_events = 0 ;

	
	public double filters_evaluated = 0 ;
	
	public double events_forwarded = 0 ;
	
	public double total_events() {
		return accepted_events + rejected_events ;
	}
	
	public double filter_width() {
		return accepted_events / total_events() ;
	}

	public double cpuLoad_Total() {
		return filters_evaluated / total_events() ;
	}
	
	public double cpuLoad_Accepted() {
		return filters_evaluated / accepted_events ;
	}

	public double netLoad_Total() {
		return events_forwarded / total_events() ;
	}
	
	public double netLoad_Accepted() {
		return events_forwarded / accepted_events ;
	}
	
}
