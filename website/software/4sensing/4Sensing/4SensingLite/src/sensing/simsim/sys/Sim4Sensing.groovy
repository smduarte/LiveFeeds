package sensing.simsim.sys;


import sensing.simsim.speedsense.map.*;
import sensing.core.ServicesConfig;
import sensing.core.network.*;
import sensing.core.query.Query;
import sensing.core.monitoring.MonitoringService;
import sensing.core.sensors.GPSReading;
import sensing.simsim.*;
import sensing.simsim.speedsense.map.setup.*;
import sensing.simsim.sys.map.MapModel;
import sensing.simsim.sys.map.MapView;

import simsim.core.*;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.*;
import simsim.gui.InputHandler;

import java.awt.Point;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Image;
import java.util.List;

import javax.imageio.ImageIO;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.coor.CachedLatLon;

public abstract class Sim4Sensing extends PipelineSimulation implements Displayable {
	public static MapView  mapView;
	public static MapModel mapModel;
	Rectangle2D.Double screenBounds;

	protected Sim4Sensing() {
		super();
		QUERY_IMPL_POLICY = ServicesConfig.QueryImplPolicy.CENTRALIZED;
	}

	protected void init() {
		super.init();
		mapModel = createMapModel();
		Globals.set("4sensing_MapModel", mapModel);
		world = new Rectangle(0,0,0,0);
		world.setRect(mapModel.bounds.asRect());
		
		mapView = mapModel.newMapView(1000, 1000);
		Globals.set("4sensing_MapView", mapView);
		Gui.setFrameTransform("MainFrame", 1000, 1000, 0, true) ;
		
		// Peer filter
		double[] delta = mapModel.metersToDegrees(setup.NODE_FILTER_RADIUS);
//		PeerDB.peerFilterHeight = delta[0];
//		PeerDB.peerFilterWidth  = delta[1];
		
		PeerDB.peerFilterHeight = world.height;	
		PeerDB.peerFilterWidth  = world.width;
		
		screenBounds = mapView.getScreenBounds();
		
		setup.TOTAL_NODES.times {
			if(it%100 == 0) println "initializing fixed node [${it}]"
			Sim4SensingNode node = new Sim4SensingNode();
			node.init();
		}
		
	}
	
	protected abstract MapModel createMapModel();
	

	public void center(Sim4SensingNode n) {
		mapView.center(n.fixedLatLon.eastNorth);
		invalidateDisplay();
	}
	
	public void center(Point p) {
		mapView.center(mapView.getEastNorth(p));
		invalidateDisplay();
	}
	
	protected  sensing.simsim.sys.Node getClosestNode(XY pos) {
		def closest;
		def (closestFixed, distanceFixed) = getClosestNode() {node -> 
			if(node.pos) return node.pos.distance(pos);
			return Double.NaN;
		};
		closest = closestFixed;
		def (closestMobile, distanceMobile) = getClosestNode() { node -> node.mNodes.collect{mNode -> mNode.pos.distance(pos)}.min()};
		if(closestFixed && (!closestMobile || distanceFixed < distanceMobile)) {
			closest =  closestFixed;
		} else {
			closest =  closestMobile;
		}
		return closest;
	}

	protected  getClosestNode(radius = CLICK_RADIUS, Closure getDistance) {
		Node closest = null;
		double minDistance = Double.MAX_VALUE ;
		eachNode{ node ->
			double distance = getDistance(node);
			//println "distance: $distance"
			if(!closest ||  distance < minDistance) {
				closest = node;
				minDistance = distance;
				//println "minDistance: $minDistance radius: $CLICK_RADIUS"
			}
		}
		if(radius == 0 || minDistance <= radius) {
			return [closest, minDistance];
		} else {
			return [];
		}
	}

	public void zoom(double factor) {
		mapView.zoomToFactor(factor);
		invalidateDisplay();
	}
	
	public void zoom(Rectangle2D bounds) {
		mapView.zoomTo(bounds);
		invalidateDisplay();
	}

	public void zoomReset() {
		mapView.zoomTo(mapModel.bounds);
		invalidateDisplay();
	}
	
	protected void invalidateDisplay() {
		eachNode {it.invalidateDisplay()};
	}
	
	public void stopActiveQuery() {
		queryResult = [:];
	}
	
	static List selectedSegments = [];
	
	def static lastClosest  = [pos: null, segments: null, idx: 0];
	
	public static String getClosestSegment(Point p) {
		if(lastClosest.pos?.x == p.x && lastClosest.pos?.y == p.y) {
			lastClosest.idx = (lastClosest.idx + 1) % lastClosest.size();
		} else {
			EastNorth en = mapView.getEastNorth(p)
			lastClosest.segments = mapModel.getClosestSegment(en);
			lastClosest.pos = p;
			lastClosest.idx = 0;
		}
		return lastClosest.segments[lastClosest.idx]	
	}
	
	public static void selectSegment(String id) {
		selectedSegments = [id];
		mapView.selectSegment(id);
	}
	
	public static void selectSegment(List ids) {
		selectedSegments = ids;
	}
		
	
	static Pen segmentExtentPen = new Pen(RGB.MAGENTA, 2, 5);
	static final Pen gridPen = new Pen(RGB.gray, 1, 10);
	static final Pen queryPen = new Pen(RGB.ORANGE,5,5);
	
	public void displayOn( Canvas c ) {
		if(display.map) {
			mapView.displayOn(c);
		}
	/*	if(display.grid) {
			drawGrid(display.grid, gridPen, c)
		}
	*/

		super.displayOn(c);

		if(display.query) displayQueriesOn(c);

		if(display.workLoad) {
			double minTWL = MonitoringService.getMinTotalWorkLoad();
			double maxTWL =  MonitoringService.getMaxTotalWorkLoad();
			if(MonitoringService.wlQuery && maxTWL > minTWL) {
				MonitoringService.gridLen.times{int x->
					MonitoringService.gridLen.times{int y -> 
						Rectangle2D cell = MonitoringService.getGridCellBounds(x,y);
						int totalWLoad = MonitoringService.workLoad[x][y];
						float f = (totalWLoad-minTWL)/(maxTWL-minTWL) //0.67*
						c.sFill(new RGB(Color.getHSBColor(0, 1.0f, f), 0.30), mapView.latLonBoundsToScreen(cell))
					}
				}
			}
		}
		if(selectedSegments) {
			selectedSegments.each { id ->
				Rectangle2D extent = mapModel.getSegmentExtent(id);
				c.sDraw(segmentExtentPen, mapView.latLonBoundsToScreen(extent));
				//println "Segment $id occupation: ${speedSenseModel.getAverageDensity(id)}"
			}
		}
	}

	public static void displayQueriesOn(Canvas c) {
		if(PipelineSimulation.q)  {
			c.sDraw(queryPen, mapView.latLonBoundsToScreen(PipelineSimulation.q.aoi));
		}		
	}

	public displayQueryResultOn(Canvas c, Query q, MapView qView) {
		queryResult.each{ GPSReading r ->
			Point p = qView.getPoint(new LatLon(r.lat, r.lon));		
			double alpha = (currentTime() > r.time) ? 1.0/Math.max(1.0,(currentTime() - r.time)/10) : 1.0;
			c.sFill(new RGB(Color.GREEN, alpha), new Circle(p.x, p.y, 5));
		}
	}

	protected void drawGrid(int quadDiv, Pen pen, Canvas canvas) {
		
		int numCels = (int) Math.pow(quadDiv, 2);
		double hSize = screenBounds.width/numCels;
		double vSize = screenBounds.height/numCels;
		for(int i=0; i<numCels+1; i++) {
			canvas.sDraw(pen, new Line(i*hSize+screenBounds.x,screenBounds.y, i*hSize+screenBounds.x, screenBounds.maxY));
			canvas.sDraw(pen, new Line(screenBounds.x, i*vSize+screenBounds.y, screenBounds.maxX, i*vSize+screenBounds.y));
		}
	}
}
