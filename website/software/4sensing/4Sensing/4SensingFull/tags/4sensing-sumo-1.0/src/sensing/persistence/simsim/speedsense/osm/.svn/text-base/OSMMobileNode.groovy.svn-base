package sensing.persistence.simsim.speedsense.osm
;
import sensing.persistence.simsim.speedsense.SGPSReading;
import sensing.persistence.simsim.speedsense.map.*;
import static sensing.persistence.simsim.PipelineSimulation.display;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapView;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.network.NetworkService;
import sensing.persistence.core.sensors.GPSReading;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.simsim.Node;
import sensing.persistence.simsim.MobileNode;
import simsim.core.Simulation;
import simsim.core.PeriodicTask;
import static simsim.core.Simulation.rg;
import simsim.gui.canvas.*;
import simsim.gui.geom.*;

import org.openstreetmap.josm.data.coor.*;

import java.awt.Color;
import java.awt.Point;
import java.util.UUID;

public class OSMMobileNode extends MobileNode {

	protected TrafficSpeedModel  speedSenseModel = OSMSpeedSenseSim.speedSenseModel;
	protected route;
	protected int cSegmentIdx;  // current way segment index
	protected cSegment;			// current way segment 
	protected cSegmentId;		// current speed model segment id
	protected double cSegDist;  // current segment distance in m
	protected double p; 		// position in segment as fraction
	protected double cSpeed; 	// current speed km/h
	protected double cRealSpeed; // debug;
	protected double cDensity;  // current segment density - for debug
	protected double cTs 		// current timestamp
	CachedLatLon  cPos; 	// current position
	

	public void init() {
		setRoute();
		new PeriodicTask(rg.nextInt(OSMSpeedSenseSim.setup.SAMPLING_RATE), OSMSpeedSenseSim.setup.SAMPLING_RATE) {
			public void run() {
				reportPosition()
				//updatePosition();
			}
		}
	}
	
	
	protected static cachedRoutes = [];
	protected void setRoute() {
		if(OSMSpeedSenseSim.setup.ROUTE_CACHE_SIZE == 0) {_setRoute(); return}
		if(cachedRoutes.size() < OSMSpeedSenseSim.setup.ROUTE_CACHE_SIZE) {
			route = findRoute();
			cachedRoutes << route;
		} else {
			route = cachedRoutes[OSMSpeedSenseSim.rg.nextInt(OSMSpeedSenseSim.setup.ROUTE_CACHE_SIZE)];
		}
		assert(route);
		initRoute();
	}

	protected void _setRoute() {
		def start = route ? route.last().end : null;
		route = [];
		route = findRoute(start);
		if(!route && start) {
			route = findRoute(null);
		}
		assert(route);
		initRoute();
	}
	
	protected void initRoute() {
		cSegmentIdx = OSMSpeedSenseSim.rg.nextInt(route.size());
		initSegment();
		cTs = Simulation.currentTime();		
	}
	
	protected void initSegment() {
		cSegment = route[cSegmentIdx];
		p = 0;
		//cSegmentId = speedSenseModel.getSegmentId(cSegment, cSegment.start.coor);
		cSegmentId = speedSenseModel.getSegmentId(cSegment, 0);
		cSpeed = speedSenseModel.getSpeed(cSegment, cSegmentId, this);
		cRealSpeed =  speedSenseModel.getSegmentSpeed(cSegment.way, cSegmentId);
		cDensity = speedSenseModel.getAverageDensity(cSegmentId);
		cPos = cSegment.start.coor;
		cSegDist = -1;
	}

	protected findRoute(start) {
		int nInter = 0;
		int nTries = 0;
		def rRoute;
		while(nInter < OSMSpeedSenseSim.setup.ROUTE_MIN_SIZE && nTries<100) {
			rRoute =  OSMSpeedSenseSim.mapModel.getRandomRoute(start, OSMSpeedSenseSim.setup.ROUTE_SIZE);
			nInter = rRoute.nInter;
			nTries++;
		}
		return rRoute.route;
	}
	
	protected XY cachedPos;
	public XY getPos() {
		if(!cachedPos) {
			Point p = OSMSpeedSenseSim.mapView.getPoint(cPos.getEastNorth());
			cachedPos = new XY(p.x, p.y);
		}
		return cachedPos;
	}
	
	
	public void updatePosition() {
		double ts = Simulation.currentTime();
		if(ts-cTs > 0) {
			cachedPos=null;
			updatePositionImpl(ts - cTs, 1);
			cTs = ts;
		}
	}

	
	public void reportPosition() {
		//cPos = new CachedLatLon(cSegment.start.coor.interpolate(cSegment.end.coor, p));
                //cSegmentId = speedSenseModel.getSegmentId(cSegment, cPos);
		if(homeBase.isOnline() ) {
			SGPSReading r = new SGPSReading(lat: cPos.lat(), lon: cPos.lon(), orientation: 0, segmentId: cSegmentId, speed: speedSenseModel.getSpeed(cSegment, cSegmentId, this), realSpeed:  speedSenseModel.getSegmentSpeed(cSegment.way, cSegmentId), density: cDensity ,time: homeBase.currentTime(), mNodeId: id);
			boolean accepted = homeBase.sensorInput(r);
			if(accepted) {
				OSMSpeedSenseSim.setup.addActiveSegment(cSegmentId);
				//if(! SpeedSenseSim.speedSenseModel.getSegmentExtent(cSegmentId)) homeBase.toggleDebugging();
				OSMSpeedSenseSim.setup.activeMobileNodes.add(this);
			} else {
				OSMSpeedSenseSim.setup.activeMobileNodes.remove(this);
			}		
		} else {
			//SpeedSenseSim.registerMobileNode(this);
		}
	}

	def static segmentDistCache = [:];
	protected double getSegmentDistance(segment) {
		String key = "${segment.start.coor.lat()}_${segment.start.coor.lon()}_${segment.end.coor.lat()}_${segment.end.coor.lon()}";
		def stored = segmentDistCache[key];
		if(stored == null) {
			segmentDistCache[key] = stored = segment.start.coor.greatCircleDistance(segment.end.coor);
		}
		return stored;
	}

	protected void updatePositionImpl(double t, int n) {
		//homeBase.services.logging.log(DEBUG, this, "updatePositionImpl ${n}", "speed: ${cSpeed}");
		//println "updatePosition ${n}";
		// calc travelled distance, based on previous recorded speed cSpeed
		double speedMs = cSpeed * 0.277777778 //1000 / 3600; // speed in m/s
		double dm = t*speedMs // distance in m
		//segD = cSegment.start.coor.greatCircleDistance(cSegment.end.coor); // segment distance
		double segD;
		if(cSegDist < 0) {
			segD = cSegDist = getSegmentDistance(cSegment);
		} else {
			segD = cSegDist;
		}
		double r = (1-p)*segD; //remaining distance in current OSM segment
//		homeBase.services.logging.log(DEBUG, this, "updatePositionImpl ${n}", "way: ${cSegment.way.keys}");
//		homeBase.services.logging.log(DEBUG, this, "updatePositionImpl ${n}", "segment: ${cSegmentIdx} ${cSegment.start.id}->${cSegment.end.id}");
//		homeBase.services.logging.log(DEBUG, this, "updatePositionImpl ${n}", "speed-ms: ${speedMs} dist-m: ${dm} segment size: ${segD} remaining: ${r}");
		if(dm <= r) { // still in same OSM segment
			p = (segD-(r-dm))/segD; // update position in segment
			cPos = new CachedLatLon(cSegment.start.coor.interpolate(cSegment.end.coor, p)); 
			//cSegmentId = speedSenseModel.getSegmentId(cSegment, cPos);
			cSegmentId = speedSenseModel.getSegmentId(cSegment, p*segD);
			cSpeed = speedSenseModel.getSpeed(cSegment, cSegmentId, this);
			cRealSpeed =  speedSenseModel.getSegmentSpeed(cSegment.way, cSegmentId);
			cDensity = speedSenseModel.getAverageDensity(cSegmentId);
			return;
		} 
		double td = r/speedMs; // time taken on this segment
		cSegmentIdx++;
		if(route.size() == cSegmentIdx) {
			//homeBase.services.logging.log(DEBUG, this, "updatePositionImpl ${n}", "new route");
			setRoute();
			//TODO: return?
			return;
		}
		initSegment();
		updatePositionImpl(t-td, n+1);
	}
	
	static Pen sensorPen = new Pen(RGB.GRAY,1,5);
	static Pen segmentExtentPen = new Pen(RGB.MAGENTA, 1, 5);
	
	public void displayOn(Canvas c) {
		if(cPos) {
			RGB statusColor;
			if(speedSenseModel.isCongested(cSegmentId, cSpeed)) {
				statusColor = RGB.RED;
			} else {
				statusColor = RGB.GREEN;
			}
			XY cPosXY = getPos();
			if(homeBase.debugNode) {
				c.sFill( statusColor, new Circle(cPosXY.x, cPosXY.y, 20));
			} else {
				c.sFill( statusColor, new Rectangle(cPosXY.x, cPosXY.y, 6.0, 6.0));
			}
		}
		if(display.gps) displayGps(c);
	}

	protected void displayGps(Canvas c) {
		List sensorDest = homeBase.getBindingDestination();
		if(sensorDest) {
			XY cPosXY = getPos();
			c.sDraw(sensorPen, new Line(cPosXY.x, cPosXY.y, sensorDest[0], sensorDest[1]));
		}		
	}
	
	public void displayDetailOn(Canvas c) {
		displayGps(c);
		if(display.segmentExtent) {
			Rectangle extent = mapModel.getSegmentExtent(cSegmentId);
			if(extent) {
				Rectangle sExtent = mapView.latLonBoundsToScreen(extent);
				c.sDraw(segmentExtentPen, sExtent);	
				c.sFont(15);
				c.sDraw(homeBase.labelPen, cSegmentId, sExtent.x, sExtent.y-5);	
			}
		}
	}
	
	public void invalidateDisplay() {
		cachedPos = null;
	}
}
