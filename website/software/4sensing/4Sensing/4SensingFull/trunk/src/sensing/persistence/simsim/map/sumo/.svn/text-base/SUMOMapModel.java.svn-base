package sensing.persistence.simsim.map.sumo;

import gnu.trove.TIntProcedure;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParserFactory;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

import sensing.persistence.simsim.map.MapModel;
import sensing.persistence.simsim.map.MapView;



public class SUMOMapModel extends MapModel {
	
	public class Edge {
		/* 
		 * Static edge features - read/computed from the SUMO network model
		 */
		public String id;							// The edge ID
		public int numLanes = 0;					// Number of lanes
		public double length = 0;					// Edge length in meters
		public boolean hasTrafficLight = false;		// True if edge has traffic lights (in at least one of its lanes)
		public Path2D shape;						// 2D shape of the edge for graphical representation (by union of lane shapes)
		public String shapeDesc;					// String representation of the 2D shape (for KML export - not used currently)
		public Rectangle2D bbox;					// The edge bounding box in lat/lng
		public double maxSpeed;						// The maximum edge speed in m/s (computed as the greatest lane maximum speed)
		
		/*
		 * Edge stats for the current period - updated by SUMOEdgeStatsSource using SUMO edge dump
		 * 	see http://sourceforge.net/apps/mediawiki/sumo/index.php?title=SUMO_OUTPUT_EDGELANE_TRAFFIC
		 */
		
		public double avgDensity = -1;				// Vehicle density on the edge (vehicles/km)
		public double avgOccupancy = -1;			// Edge occupancy in % 
		public double avgSpeed = -1;				// The mean speed on the edge within the reported interval  (m/s)
		public double sampledSeconds = -1;			// Sampled seconds Number seconds vehicles were measured on the edge/lane
		public double travelTime = -1;				// Time needed to cross the edge - estimation based on the mean speed (s)
		
		/*
		 *  The edge stats for the previous period
		 */
		public double pAvgDensity = -1;
		public double pAvgOccupancy = -1;
		public double pAvgSpeed = -1;
		public double pSampledSeconds = -1;	
		public double pTravelTime = -1;	
		
		/*
		 * Edge stats computed from the SUMO vehicle dump, updated by SUMO4SVProbeHandler
		 * 	Values are relative to previous edge stats period
		 */
		
		public double vProbePSamples = -1;			// Number of speed samples
		public double vProbePAvgSpeed = -1;			// Mean speed (m/s)
		public double vProbePStdSpeed = -1;			// Standard deviation (m/s)
		public double vProbePMinSpeed = -1;			// Minimum sampled speed (m/s)
		public double vProbePMaxSpeed = -1;			// Maximum sampled speed (m/s)
	
		/*
		 *  vProbeSpeed used for computation of vProbe edge stats 
		 *  	TODO: use incremental computation: http://mathcentral.uregina.ca/QQ/database/QQ.09.02/carlos1.html
		 */
		public ArrayList<Double> vProbeSpeeds = new ArrayList<Double>();
		
		/*
		 * Extended dynamic stats added to SUMO edge_dump - not currently used
		 */
		public int vpTravelTimeCount = -1;
		public double vpTravelTimeMin = -1;
		public double vpTravelTimeMax = -1;
		public double vpTravelTimeAvg = -1;
		public double vpTravelTimeStd = -1;
		
		public double pVpTravelTimeCount = -1;
		public double pVpTravelTimeMin = -1;
		public double pVpTravelTimeMax = -1;
		public double pVpTravelTimeAvg = -1;
		public double pVpTravelTimeStd = -1;		
		
	}
	
	protected HashMap<Integer,Edge> edges = new HashMap<Integer,Edge>();
//	protected RTree edgeIndex;
	protected ArrayList<Path2D> lanes = new ArrayList<Path2D>();
	protected ProjectionBounds projBounds;
	protected RTree sIndex;
	
	
	public SUMOMapModel() {
		Properties p = new Properties();
		p.setProperty("MaxNodeEntries", "10");
		p.setProperty("MinNodeEntries", "5");
		sIndex = new RTree();
		sIndex.init(p);
	}
	
	
	private class Parser extends DefaultHandler {
		Edge cEdge;
		StringBuffer shapeDesc;
		int nEdges = 0;
		double maxY;
		double netOffsetX;
		double netOffsetY;
		String succEdgeId;
		
		 @Override public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
			 if(qName.equals("location")) {
				 String[] sNetOffset = attrs.getValue("netOffset").split(",");
				 netOffsetX = Double.parseDouble(sNetOffset[0]);
				 netOffsetY = Double.parseDouble(sNetOffset[1]);
				 
				 String[] sBounds = attrs.getValue("origBoundary").split(",");
				 double lon0 = Double.parseDouble(sBounds[0]);
				 double lat0 = Double.parseDouble(sBounds[1]);
				 double lon1 = Double.parseDouble(sBounds[2]);
				 double lat1 = Double.parseDouble(sBounds[3]);
				 bounds = new Bounds(lat0, lon0, lat1, lon1);
				 
				 String[] sProjBounds = attrs.getValue("convBoundary").split(",");
				 double bX1 = Double.parseDouble(sProjBounds[0]);
				 double bY1 = Double.parseDouble(sProjBounds[1]);
				 double bX2 = Double.parseDouble(sProjBounds[2]);
				 double bY2 = Double.parseDouble(sProjBounds[3]);
				 EastNorth minEN = new EastNorth(bX1, bY1);
				 EastNorth maxEN = new EastNorth(bX2, bY2);
				 projBounds = new ProjectionBounds(minEN,maxEN);
				 proj = new LinearProjection(bounds, projBounds);
				 System.out.println("Bounds " + bounds);
				 System.out.println("minEN " + minEN);
				 System.out.println("maxEN " + maxEN);
			 } else if(qName.equals("edge")) {
				 if(cEdge != null) {
					 Rectangle2D bounds = cEdge.shape.getBounds2D();
					 EastNorth minEN = new EastNorth(bounds.getMinX(), bounds.getMinY());
					 LatLon minLL = proj.eastNorth2latlon(minEN);
					 EastNorth maxEN = new EastNorth(bounds.getMaxX(), bounds.getMaxY());
					 LatLon maxLL = proj.eastNorth2latlon(maxEN);
					 cEdge.bbox = new Rectangle2D.Double(minLL.lon(), minLL.lat(), maxLL.lon()-minLL.lon(), maxLL.lat()-minLL.lat());
					 cEdge.shapeDesc = shapeDesc.toString();
					 edges.put(cEdge.id.hashCode(), cEdge);
					 sIndex.add(new Rectangle((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getMaxX(), (float)bounds.getMaxY()), cEdge.id.hashCode());
				 }
				 cEdge = new Edge();
				 cEdge.id = attrs.getValue("id");
				 cEdge.shape = new Path2D.Double();
				 cEdge.maxSpeed = -1;
				 shapeDesc = new StringBuffer();
				 nEdges++;
			 } else if(qName.equals("lane")) {
				 double maxSpeed = Double.parseDouble(attrs.getValue("maxspeed"));
				 cEdge.maxSpeed = maxSpeed > cEdge.maxSpeed ? maxSpeed : cEdge.maxSpeed;
				 double length = Double.parseDouble(attrs.getValue("length"));
				 cEdge.length = length > cEdge.length ? length : cEdge.length;
				 //System.out.println(String.format("edge: %s lane: %s", cEdgeId, attrs.getValue("id")));
				 String shape = attrs.getValue("shape");
				 Path2D.Double lanePath = new Path2D.Double();
				 int nPoints = 0;
				 StringTokenizer shapeT = new StringTokenizer(shape);
				 while(shapeT.hasMoreTokens()) {
				     String[] coord = shapeT.nextToken().split(",");
				     double x = Double.parseDouble(coord[0]);
				     double y = Double.parseDouble(coord[1]);
				    // System.out.println( "X: " + x + "Y:" + y);
				     if(nPoints == 0) {
				    	 lanePath.moveTo(x, y);
				    	 //System.out.println(String.format("moveTo %f,%f", x,y));
				     } else {
				    	 lanePath.lineTo(x, y);
				    	 //System.out.println(String.format("lineTo %f,%f", x,y));
				     }
				     nPoints++;
				 }
				 lanes.add(lanePath);
				 cEdge.shape.append(lanePath, false);
				 if(shapeDesc.length() > 0) {
					 shapeDesc.append(";");
				 }
				 shapeDesc.append(shape);
				 cEdge.numLanes++;
				 //edgeIndex.add(new Rectangle((float)cEdge.bbox.getX(), (float)cEdge.bbox.getY(), (float)cEdge.bbox.getMaxX(), (float)cEdge.bbox.getMaxY()), cEdge.internalId);
			 } else if(qName.equals("succ")) {
				 succEdgeId = attrs.getValue("edge");
			 } else if(qName.equals("succlane") && attrs.getValue("tl") != null) {
				 getEdge(succEdgeId).hasTrafficLight = true;
			 }
		 }
	}
	
	
	public boolean load(String fileName) {
		try {
			FileInputStream fis = new FileInputStream("koeln_bbox.net.xml");
			Parser p = new Parser();
			SAXParserFactory.newInstance().newSAXParser().parse(fis,p);
			Main.proj = proj;
			System.out.println(String.format("num edges: %d num lanes: %d", p.nEdges, lanes.size()));
			
			EastNorth minEE = proj.latlon2eastNorth(bounds.getMin());
			EastNorth maxEE = proj.latlon2eastNorth(bounds.getMax());
			System.out.println("Proj bounds - minEE: " + minEE + " maxEE: " + maxEE);
			
			LatLon minLL = proj.eastNorth2latlon(minEE);
			LatLon maxLL = proj.eastNorth2latlon(maxEE);
			System.out.println("bounds - minLL: " + minLL + " maxLL: " + maxLL);
			
			minLL = proj.eastNorth2latlon(projBounds.min);
			maxLL = proj.eastNorth2latlon(projBounds.max);
			System.out.println("bounds - minLL: " + minLL + " maxLL: " + maxLL);
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	@Override
	public MapView newMapView(double width, double height) {
		return newMapView(width, height, new WireframeRenderer());
	}

	@Override
	public ProjectionBounds getProjectionBounds() {
		return projBounds;
	}
	
	public ArrayList<Path2D> getLanes() {
		return lanes;
	}
	
	public Collection<Edge> getEdges() {
		return edges.values();
	}
	
	public Collection<Edge> getEdges(Rectangle2D bounds) {
		final ArrayList<Edge> result = new ArrayList<Edge>();
		TIntProcedure p = new TIntProcedure() {
			public boolean execute(int value) {
				result.add(edges.get(value));
				return true;
			}
		};
		sIndex.intersects(new Rectangle((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getMaxX(), (float)bounds.getMaxY()), p);
		return result;
	}
	
	public Collection<String> getClosestSegment(EastNorth en) {
		com.infomatiq.jsi.Point p = new com.infomatiq.jsi.Point((float)en.east(), (float)en.north());
		final ArrayList<String> nearest = new ArrayList<String>();
		sIndex.nearest(p, new TIntProcedure() {
			public boolean execute(int value) {
				nearest.add(edges.get(value).id);
				return true;
			}
		},
		10);
//		String result = null;
//		for(Edge e : nearest) {
//			if(e.shape.intersects(en.east(), en.north(), 5,5)) {
//				result = e.id;
//				break;
//			}
//		}
//		return result;
		return nearest;
	}
	
	public double getMaxSpeed(String segmentId) {
		Edge e = edges.get(segmentId.hashCode());
		return (e != null) ? e.maxSpeed : -1;
	}
	
	public double getAvgSpeed(String segmentId) {
		Edge e = edges.get(segmentId.hashCode());
		return (e != null) ? e.avgSpeed : -1;
	}
	
	public double getAvgDensity(String segmentId) {
		Edge e = edges.get(segmentId.hashCode());
		return (e != null) ? e.avgDensity : -1;
	}
	
	public double getAvgOccupancy(String segmentId) {
		Edge e = edges.get(segmentId.hashCode());
		return (e != null) ? e.avgOccupancy : -1;
	}
	
	public double getVProbePAvgSpeed(String edgeId) {	
		Edge e = getEdge(edgeId);
		return e.vProbePAvgSpeed;
	}
	
	
	
	public Rectangle2D getSegmentExtent(String segmentId) {
		return edges.get(segmentId.hashCode()).bbox;
	}
	
	public Edge getEdge(String id) {
		return edges.get(id.hashCode());
	}
	
	public void resetStats() {
		for(Edge e : edges.values()) {
			e.pAvgDensity = e.avgDensity;
			e.pAvgOccupancy = e.avgOccupancy;
			e.pAvgSpeed = e.avgSpeed;
			e.pSampledSeconds = e.sampledSeconds;
			e.pTravelTime = e.travelTime;
			e.pVpTravelTimeCount = e.vpTravelTimeCount;
			e.pVpTravelTimeMin = e.vpTravelTimeMin;
			e.pVpTravelTimeMax = e.vpTravelTimeMax;
			e.pVpTravelTimeAvg = e.vpTravelTimeAvg;
			e.pVpTravelTimeStd = e.vpTravelTimeStd;
			
			e.avgDensity = -1;
			e.avgOccupancy = -1;
			e.avgSpeed = -1;
			e.sampledSeconds = -1;
			e.travelTime = -1;
			e.vpTravelTimeCount = -1;
			e.vpTravelTimeMin = -1;
			e.vpTravelTimeMax = -1;
			e.vpTravelTimeAvg = -1;
			e.vpTravelTimeStd = -1;	
		}
	}
	
	
	// TODO: Incremental computation: http://mathcentral.uregina.ca/QQ/database/QQ.09.02/carlos1.html
	public void updateVProbeStats() {
		for(Edge e : edges.values()) {
			int samples = e.vProbeSpeeds.size();
			if(samples > 0) {
				e.vProbePSamples = samples;
				double sumSpeed =  0;
				e.vProbePMinSpeed = e.vProbeSpeeds.get(0);
				e.vProbePMaxSpeed= e.vProbeSpeeds.get(0);
				for(double speed : e.vProbeSpeeds) {
					sumSpeed += speed;
					if(speed < e.vProbePMinSpeed) e.vProbePMinSpeed = speed;
					if(speed > e.vProbePMaxSpeed) e.vProbePMaxSpeed = speed;
				}
				e.vProbePAvgSpeed = sumSpeed/samples;
				
				double var = 0;
				for(double speed : e.vProbeSpeeds) {
					var += (speed - e.vProbePAvgSpeed) * (speed - e.vProbePAvgSpeed) ;
				}
				var = var/(samples-1);
				e.vProbePStdSpeed = Math.sqrt(var);
				e.vProbeSpeeds.clear();
			} else {
				e.vProbePAvgSpeed = -1;
				e.vProbePSamples = -1;
				e.vProbePStdSpeed = -1;
				e.vProbePMinSpeed = -1;
				e.vProbePMaxSpeed = -1;
			}
		}		
	}
	
}
