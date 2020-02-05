package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;
import sensing.persistence.simsim.Metric;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.Detections.SegmentStatus;

@Singleton(lazy=true)  class SegmentHistory extends Metric {
	
	List congested = []
	List clear = []
	

	public void init() {
		Detections.instance.addSegmentStatusListener { String segmentId, SegmentStatus status, double duration  ->
			if(status == Detections.SegmentStatus.CLEAR) {
				clear << (int)(duration+0.5);	
			} else {
				congested << (int)(duration+0.5);
			}
		}
	}
	
	public void stop() {
		openOutputFile("CongestedHistory.csv").append(congested.join(",") + "\n");
		openOutputFile("ClearHistory.csv").append(clear.join(",") + "\n");
	}
}
