package meeds.simsim.osm;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import rlc.Point;
import rlc.RLC;
import simsim.core.Displayable;
import simsim.core.Simulation;
import simsim.graphs.Graph;
import simsim.graphs.Link;
import simsim.graphs.ShortestPathsTree;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;
import simsim.utils.RandomList;
import static simsim.core.Simulation.Gui;

public class OsmMapModel implements Displayable {

	MemoryDataSet dataSet;

	public Map<Integer, OsmNode> nodes = new HashMap<Integer, OsmNode>();
	public Map<Integer, OsmWay> ways = new HashMap<Integer, OsmWay>();

	RandomList<OsmSegment> segments = new RandomList<OsmSegment>();

	OsmRouteGenerator routeCache  ;
	
	RLC srlc = new RLC(0.002);

	public OsmMapModel() {
	}

	public OsmMapModel load(String fileName) {
		try {
			OsmLoader fl = new OsmLoader(new File(fileName));
			dataSet = fl.parseOsm();
			return this;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void process(JXMapViewer map) {

		Set<String> Ways2Use = new HashSet<String>( Arrays.asList(new String[]{"highway.primary", "highway.trunk", "highway.trunk_link", "highway.secondary", "highway.tertiary", "highway.road", "highway.residential"} )) ;
		for (Iterator<Node> it = dataSet.getNodes(new Bounds()); it.hasNext();) {
			Node n = it.next();
			nodes.put((int) n.getId(), new OsmNode(n));
		}

		skip: for (Iterator<Way> it = dataSet.getWays(new Bounds()); it.hasNext();) {
			Way w = it.next();
			ArrayList<OsmNode> nl = new ArrayList<OsmNode>();
			for (WayNode i : w.getWayNodes()) {
				OsmNode j = nodes.get((int) i.getNodeId());
				if (j == null)
					continue skip;
				nl.add(j);
			}
			OsmWay ow = new OsmWay( w, nl );
			if( Ways2Use.contains( ow.type ) )
				ways.put((int) w.getId(), ow);
		}
		for (OsmWay i : ways.values()) {
			for (int j = 0; j < i.nodes.size() - 1; j++) {
				OsmNode a = i.nodes.get(j), b = i.nodes.get(j + 1);
				if (!a.pos.equals(b.pos))
					segments.add(new OsmSegment(i, a.pos, b.pos));
			}
		}

		GeoPos center = new GeoPos( map.getCenterPosition() ) ;
		for (OsmSegment i : segments)
			if( center.distance( i ) < 0.03)
				srlc.add(i, new long[1]);

		Set<String> names = new HashSet<String>();
		for (OsmWay i : ways.values()) {
			names.add(i.name);
		}
		System.out.printf(String.format("Nodes: %d Ways: %d Segments: %d\n", nodes.size(), ways.size(), segments.size()));
		
		routeCache = new OsmRouteGenerator(srlc, segments);
	}

	public OsmRoute getRoute() {
		return new OsmRoute( routeCache ) ;
	}
	
	Pen highlight = new Pen(new RGB(1.0, 0.25, 0.25, 0.5), 5.0);
	Pen highlight2 = new Pen(new RGB(1.0, 0.25, 1.0, 0.5), 5.0);

	@Override
	public void displayOn(Canvas canvas) {
//		XY p = Gui.getMouseXY();
//		GeoPos mouse = new GeoPos(canvas.map.convertPointToGeoPosition(new Point2D.Double(p.x, p.y)));		
//		
//		
//		Collection<Point> points = srlc.rangeSearch( mouse, 0.003, new long[1]) ;
//		
//		OsmSegment closest = closest( points, mouse);
//		
//		if (closest != null) {
//			Path2D shape = new Path2D.Double();
//			OsmWay w = closest.way;
//			for (int j = 0; j < w.nodes.size(); j++) {
//				GeoPosition a = w.nodes.get(j).pos;
//				Point2D p1 = canvas.map.convertGeoPositionToPoint(a);
//				//canvas.sDraw(highlight, new Circle( p1, 10));
//				if (j == 0)
//					shape.moveTo(p1.getX(), p1.getY());
//				else
//					shape.lineTo(p1.getX(), p1.getY());
//			}
//			canvas.sDraw(highlight, shape);

//			List<OsmSegment> segments = new ArrayList<OsmSegment>() ;
//			for( Point i : points )
//				segments.add( (OsmSegment) i ) ;
//
//			Set<GeoPos> nodeSet = new HashSet<GeoPos>();
//			for (OsmSegment i : segments) {
//				nodeSet.add(i.v);
//				nodeSet.add(i.w);
//			}			
//			Graph<GeoPos> G = new Graph<GeoPos>( nodeSet, segments) ;
//			ShortestPathsTree<GeoPos> spt = new ShortestPathsTree<GeoPos>(closest.v, G) ;
//			
//			for( OsmSegment l : segments ) {
//				canvas.sDraw( RGB.YELLOW, new Line( canvas.geo2point(l.v), canvas.geo2point(l.w)));
//			}
//			
//			for( Link<GeoPos> l : spt.edges() ) {
//				canvas.sDraw( RGB.MAGENTA, new Line( canvas.geo2point(l.v), canvas.geo2point(l.w)));
//			}
//			for( GeoPos i : spt.leafSet() ) {
//				canvas.sFill( new Pen(RGB.ORANGE,1), new Circle( canvas.geo2point(i), 30.0));				
//			}
//
//		}

		
		
		canvas.sFont(18);
		canvas.gs.setColor(RGB.black);
		canvas.sDraw(String.format("%.0f s / %.1f h", Simulation.currentTime(), Simulation.currentTime() / 3600), new XY(20, 20));
	}

//	public XY closestSegmentPoint(XY point) {
//		GeoPos pos = new GeoPos(point);
//		OsmSegment closest = closest(srlc.rangeSearch(pos, 0.003, new long[1]), pos);
//		return closest == null ? point : closest.projection(pos);
//	}

	OsmSegment closest(Collection<Point> l, GeoPosition p) {
		OsmSegment r = null;
		double D = Double.MAX_VALUE;
		for (Point i : l) {
			OsmSegment s = (OsmSegment) i;
			double d = s.minimum_distance(p);
			if (d < D) {
				r = s;
				D = d;
			}
		}
		return r;
	}

	long closestWay(double lat, double lon) {
		GeoPos pos = new GeoPos(lat, lon);
		OsmSegment closest = closest(srlc.rangeSearch(pos, 0.005, new long[1]), pos);
		return closest == null ? 0 : closest.way.way.getId();
	}	
}
