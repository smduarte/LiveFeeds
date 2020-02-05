package sensing.simsim.app.setup;

import static sensing.simsim.sys.Sim4Sensing.mapModel

import java.awt.geom.Rectangle2D
import java.util.HashSet

import sensing.core.query.Query
import sensing.simsim.app.tuples.AggregateSpeed
import sensing.simsim.app.tuples.SGPSReading
import sensing.simsim.app.tuples.SpeedHistogram
import sensing.simsim.sys.BasicCentralizedSetup
import sensing.simsim.sys.PipelineSimulation
import sensing.simsim.sys.Sim4Sensing

public class Test extends BasicCentralizedSetup {

	public HashSet activeSegments = new HashSet(); // segments included in current query
	public HashSet activeMobileNodes = new HashSet(); // mobile nodes participating in current query
	
	public Test() {
		super();
//		config.VTABLE = "sensing.simsim.app.vtable.TrafficRaw" ;
		config.VTABLE = "TrafficRaw" ;
	}
	
	
	protected void runQuery(Query q, Closure qListener) { 
		activeSegments = new HashSet();
		queryResult = [:];
		super.runQuery(q, qListener);
	}
	
	protected void startQuery() {
		String pipelineName = config.VTABLE;
		double centerLat = sim.world.center().y;
		double centerLon = sim.world.center().x;
		double width = 0.25 * sim.world.width;
		double height = 0.25 * sim.world.height;
		
		Query q = createQuery(pipelineName, centerLat, centerLon, width, height);
//		runQuery(q) { AggregateSpeed s -> outputResult(s)};
//		runQuery(q) { SGPSReading r -> outputResult(r)};
		runQuery(q) { SpeedHistogram s -> outputResult(s)};
	}	 

	//smd
	protected void outputResult(SGPSReading r) {
		System.out.println("%&%&%&%&%&"+ r );
	}
	
	protected void outputResult(AggregateSpeed r) {
		System.out.println( "-------------------->" + r );
	}

	protected void outputResult(SpeedHistogram r) {
		if( r != null ) {
			sensing.simsim.app.Main.histogram.init() ;
			double total = 0.0 ;
			for( int i = 0 ; i < r.histogram.size(); i++ ) {
				Double v = r.histogram.get(i) ;
				total += v == null ? 0.0 : v ;
			}
			for( int i = 0 ; i < r.histogram.size(); i++ ) {
				Double v = r.histogram.get(i) ;
				sensing.simsim.app.Main.histogram.tally( i * 10, (v == null ? 0.0 : v) / total ) ;
			}
		}
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
	
	/*
	public void storeAggregate(AggregateSpeed a) {
		aggregateStore[a.segmentId] = [a, PipelineSimulation.currentTime()]
	}
	*/
	
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
