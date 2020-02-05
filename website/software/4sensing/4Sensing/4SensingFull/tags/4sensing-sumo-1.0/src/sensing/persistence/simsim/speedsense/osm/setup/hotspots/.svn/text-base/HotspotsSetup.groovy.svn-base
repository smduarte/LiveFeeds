package sensing.persistence.simsim.speedsense.osm.setup.hotspots;
import sensing.persistence.core.vtable.VTableDefinition;
import sensing.persistence.simsim.map.MapView;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.setup.*;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.network.PeerDB;
import sensing.persistence.simsim.charts.*;
import sensing.persistence.simsim.speedsense.osm.*;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import sensing.persistence.simsim.Node;

import simsim.gui.canvas.*;
import simsim.gui.geom.*;
import simsim.gui.InputHandler;
import simsim.core.Displayable;
import speedsense.*;

import org.openstreetmap.josm.data.osm.visitor.Visitor;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.visitor.*;
import org.openstreetmap.josm.data.coor.CachedLatLon;

abstract class HotspotsSetup extends SpeedSenseSetup {
//	public static final int QUERY_RESULT_VALIDITY = 30;
	Visitor painter;
	MapView queryView;
	Visitor queryPainter;
	def selectedSegments;
	int totalDetections = 0;
	
	
	
	public HotspotsSetup() {
		super();	
		config.TOTAL_MNODES = 500;
		config.DENSITY_SPEED_WEIGHT = 1/5;
		config.QUERY_RESULT_VALIDITY = 10;
		config.RUN_TIME = 900;
		config.COUNT_THRESH = 15;
	}
	
	public void init(OSMSpeedSenseSim sim) {
		super.init(sim);
		sim.Gui.addDisplayable("Query",  {Canvas c -> 
			if(q && sim.display.queryResult) {
				queryView.displayOn(c);
				c.sDraw(sim.queryPen, queryView.latLonBoundsToScreen(q.aoi));
		}
		//displayQueryResultOn(c, q, queryView);
		} as Displayable, 5.0);
		sim.Gui.setFrameRectangle("Query", 820, 0, QDISPL_MAXSIZE, QDISPL_MAXSIZE);
		sim.Gui.setFrameTransform("Query", sim.sideLen, sim.sideLen, 0, false) ;
		sim.Gui.addInputHandler("Query", [onMouseMove: { XY pu, XY ps ->}, onMouseDragged: { int button, XY pu, XY ps -> },
  		    onMouseClick: { int button, XY pu, XY ps -> 
  				if(button == 1) {
  					queryView.zoomToFactor(ps.x, ps.y, 0.5);
  				} else {
  					queryView.zoomToFactor(ps.x, ps.y, 2.0);
  				}
  			}] as InputHandler);	
	}
	

	protected void  initQuery(Query q) {
		super.initQuery(q);
		initQueryResultDisplay(q);
	}
	
	protected void startQuery() {
		Query q = getQuery();
		initQuery(q);
		runQuery(q) { Hotspot h -> putResult(h.segmentId, h) }
	}
	
	protected abstract Query getQuery();
	

	static final int QDISPL_MAXSIZE = 500;
	static final int SDISPL_MAXSIZE = 500;
	static final int SDISPL_SIZE = 1000;
	
	public void initQueryResultDisplay(Query q) {
		Bounds b = new Bounds(new LatLon(q.aoi.y, q.aoi.x), new LatLon(q.aoi.maxY, q.aoi.maxX));
		queryView = sim.mapModel.newMapView(sim.sideLen, sim.sideLen, new QueryResultPaintVisitor(this));
		queryView.zoomTo(b);
	}
	

	protected setupCharts() {
//		Histogram segOcupationPerDet = new Histogram(startVal: 0, stepVal: 5, metricName: "Num Vehicles", title: "Segment Occupation Per Detection")
//		addQueryListener { Hotspot d -> segOcupationPerDet.addValue(sim.speedSenseModel.getAverageDensity(d.segmentId))}
		boolean display = Boolean.parseBoolean(sim.getConfigProperty("sim.nogui", "false")) ? false : true;
		
		
		Histogram latencyHistogramTreeLevel = new Histogram(startVal: 0, stepVal: 5,  maxVal: 120, metricName: "Latency", title: "LatencyTreeLevel", fileName: "LatencyTreeLevel", display: display)
		Detections.instance.addDetectionListener {Hotspot d, double lat, boolean isNew -> 
			if(isNew) latencyHistogramTreeLevel.addValue(d.level, lat)
		}
		
		Histogram latencyHistogramGaLevel = new Histogram(startVal: 0, stepVal: 5,  maxVal: 120, metricName: "Latency", title: "LatencyGALevel", fileName: "LatencyGALevel", display: display)
		Detections.instance.addDetectionListener {Hotspot d, double lat, boolean isNew ->
			if(isNew) latencyHistogramGaLevel.addValue(d.gaLevel, lat)
		}

		return [
			//new DetectionsPerLevelChart(),
//			new HistogramInst(startVal: 0, stepVal: 5, metricName: "Num Vehicles", title: "Segment Occupation", {
//				activeSegments.collect{ seg -> sim.speedSenseModel.getAverageDensity(seg)}	
//			}),
//			new HistogramInst(startVal: 0, stepVal: 5, metricName: "Num Vehicles", title: "Congested Segment Occupation", {
//				activeSegments.findAll{ seg -> sim.speedSenseModel.isCongested(seg)}.collect{ seg -> sim.speedSenseModel.getAverageDensity(seg)}
//			}),
//			new HistogramInst(startVal: 0, stepVal: 1, metricName: "Nodes/Segment", title: "Nodes/Segment", {
//				Map segments = [:]
//				sim.eachNode { node ->
//					node.handledSegments.each{ seg -> 
//						def count = segments[seg]
//						segments[seg] = count ? count+1 : 1
//					}
//				}
//				segments.values().collect{it}
//			}),
//			segOcupationPerDet,
			Detections.instance,
//			latencyHistogram,
			latencyHistogramTreeLevel,
			latencyHistogramGaLevel,
//			ResultsPerSlot.instance,
//			new ErrorChart(display),
				
			new NodeInfo({node -> node.getTotalProcessedTuples([VTableDefinition.DATASRC])}, "DataSource"),
			new NodeInfo({node -> node.getAcquiredCount()}, "Acquisition"),
			new NodeInfo({node -> node.getTotalProcessedTuples(["global", "speedsense.TrafficSpeed.globalAggregation0"])}, "Aggregation"),
			
	        new NodeInfo({ node -> node.getReceivedMsg("sensing.persistence.core.query.QueryData")}, "MsgReceived"),
			new NodeInfo({ node -> node.getSentMsg("sensing.persistence.core.query.QueryData")}, "MsgSent"),
        
	        new Cumulative("Messages", [
	           [name: "QUERY_DATA", fn: {
	           		PeerDB.peersList.inject(0) {sum, Node node->
	           			sum += node.getSentMsg("sensing.persistence.core.query.QueryData");
	           		}
	           }],
	          
	           [ name: "QUERY_RES", fn: {
           			PeerDB.peersList.inject(0) {sum, Node node->
       					sum += node.getSentMsg("sensing.persistence.core.query.QueryResult");
           			}
	           }],
	           [ name: "QUERY_ALL", fn: {
          			PeerDB.peersList.inject(0) {sum, Node node->
      					sum += node.getSentMsg("sensing.persistence.core.query.QueryData");
      					sum += node.getSentMsg("sensing.persistence.core.query.QueryResult");
          			}
	           }],
	           [ name: "BINDING", fn: {PeerDB.peersList.inject(0) {sum, Node node-> sum += node.getBindingCount()}}]
	        ])
		];
	}

	public  getSpeed(String segmentId) {
		def detection = getResult(segmentId)
		return detection?.avgSpeed;
	}
	


	
}
