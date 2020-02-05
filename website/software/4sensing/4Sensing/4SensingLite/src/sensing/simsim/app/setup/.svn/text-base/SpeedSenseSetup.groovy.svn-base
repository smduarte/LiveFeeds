package sensing.persistence.simsim.speedsense.setup;

import java.util.HashSet;

import sensing.persistence.simsim.PipelineSimulation;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import sensing.persistence.simsim.SimSetup;
import sensing.persistence.simsim.speedsense.SpeedSenseSim
import simsim.core.Task;
import sensing.persistence.core.query.Query;
import java.awt.geom.Rectangle2D;
import speedsense.AggregateSpeed;

abstract class SpeedSenseSetup extends SimSetup {

	
	public HashSet activeSegments = new HashSet(); // segments included in current query
	public HashSet activeMobileNodes = new HashSet(); // mobile nodes participating in current query
	protected static Map aggregateStore = [:]
	protected String selectedSegmentId;
	
	
	public SpeedSenseSetup() {
		super();
		config.SAMPLING_RATE = 5;
		config.TOTAL_MNODES = 500;
		config.TOTAL_NODES = 500;
		config.ROUTE_CACHE_SIZE = 1000;
		config.ROUTE_SIZE = 50;
		config.ROUTE_MIN_SIZE = 20;
		config.DENSITY_HISTORY_SIZE = 1;
		// RTree/NTree
		config.NODE_FILTER_RADIUS = 10000; //m

		// TrafficSpeedModel
		config.SEGMENT_SIZE = 1000; //m
		config.CONGESTION_FACTOR = 0.5; 	// used to evaluate if a segment is congested
		config.DENSITY_SPEED_WEIGHT = 1/5; // used to evaluate current speed on a segment based on node density
		config.SPEED_STD_DEV = 5; 
		config.MIN_SPEED = 5;	
		config.MAIN_WAYS = true;
		
		config.RUN_TIME = 900;
	}
	
	
	protected void runQuery(Query q, Closure qListener) { 
		activeSegments = new HashSet();
		queryResult = [:];
		super.runQuery(q, qListener);
	}
	
	 

	public boolean addActiveSegment(String segmentId) {
		boolean result = false
		if(q && !activeSegments.contains(segmentId) ) {
			def extent = mapModel.getSegmentExtent(segmentId);
			if(extent && ! activeSegments.contains(segmentId)) {
				Rectangle2D intersection = q.aoi.createIntersection(extent);
				if(Math.abs(intersection.width*intersection.height) >= 0.60*Math.abs(extent.width*extent.height)) {
					activeSegments.add(segmentId);
					result = true;
				}
			} else if(!extent) {
				println "segment has no extent: ${segmentId}"
			}
		}
		return result;
	}
	
	public void storeAggregate(AggregateSpeed a) {
		aggregateStore[a.segmentId] = [a, PipelineSimulation.currentTime()]
	}
	
	public void selectSegment(String id) {
		if(!selectedSegmentId.equals(id)) {
			selectedSegmentId = id;
			charts.each{ chart ->
				if(chart.respondsTo("segmentSelected")) {
					chart.segmentSelected();	
				}
			}
		}
	}
}
