package sensing.simsim.sys.map.osm

import static sensing.simsim.sys.PipelineSimulation.setup

import java.awt.geom.Point2D
import java.util.List

import org.openstreetmap.josm.data.Bounds
import org.openstreetmap.josm.data.ProjectionBounds
import org.openstreetmap.josm.data.coor.EastNorth
import org.openstreetmap.josm.data.coor.LatLon
import org.openstreetmap.josm.data.osm.BBox
import org.openstreetmap.josm.data.osm.DataSet
import org.openstreetmap.josm.data.osm.DataSource
import org.openstreetmap.josm.data.osm.Node
import org.openstreetmap.josm.data.osm.OsmPrimitiveType
import org.openstreetmap.josm.data.osm.Way
import org.openstreetmap.josm.data.projection.*
import org.openstreetmap.josm.gui.progress.NullProgressMonitor
import org.openstreetmap.josm.io.OsmReader

import sensing.simsim.sys.PipelineSimulation
import sensing.simsim.sys.map.MapModel
import sensing.simsim.sys.map.MapRenderer
import sensing.simsim.sys.map.MapView
import simsim.gui.geom.Rectangle

public class OSMMapModel extends MapModel {
	DataSet dataSet;
	DataSource dataSrc;
	List ways;
	List nodes;
	Random rg;
	List OSMNodePosDelta;
	

	
	public OSMMapModel() {
		rg = PipelineSimulation.rg;
		proj = new Mercator();
	}
	
	public boolean load(String fileName) {
		try {
			InputStream fis = getClass().getResourceAsStream(fileName) ;
			System.out.println(fileName + "->" + fis);
			if( fis == null )
				fis = new FileInputStream( fileName ) ;
				
			dataSet = OsmReader.parseDataSet(fis, NullProgressMonitor.INSTANCE);
			dataSrc = dataSet.dataSources.head();
	
			ways = dataSet.roads;
			nodes = [];
			nodes.addAll(dataSet.nodes);
			bounds = dataSrc.bounds;
			
			double[] delta = metersToDegrees(100);
			OSMNodePosDelta = [delta[0], delta[1]];
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	protected Bounds getRealBounds() {
		Bounds b = new Bounds(dataSrc.bounds);
		dataSet.getWays().each { w ->
			w.getNodes().each { n ->
				if(!b.contains(n.getCoor())) {
					println "out of bounds: ${n.getCoor().greatCircleDistance(b.getCenter())}"
					b.extend(n.getCoor());
				}
			}
		}
		return b;
	}
	
	
	
	public MapView newMapView(double width, double height) {
		return newMapView(width, height, new WireframePaintVisitor());
	}
	
	public ProjectionBounds getProjectionBounds() {
		//Bounds b = getRealBounds();
		Bounds b = dataSrc.bounds; 
		return new ProjectionBounds(proj.latlon2eastNorth(b.getMin()),
			proj.latlon2eastNorth(b.getMax()));
	}
	
	public boolean isInLand(double lat, double lon) {
		def(latDelta, lonDelta) = OSMNodePosDelta;
		BBox bounds = new BBox(lon-lonDelta/2,lat-latDelta/2,lon+lonDelta/2,lat+latDelta/2);
		return (dataSet.searchNodes(bounds).size() != 0);
	}

	//TODO
	public double getMaxSpeed(String segmentId) {
		return -1;
	}
	
	//TODO
	public double getAvgSpeed(String segmentId) { 
		return -1;	
	}

    public List getWayIntersections(Way w, Node start, boolean forward, boolean filter) {
    	List wayIntersections = [];
    	// if oneway, exclude intersections at way start
    	//List searchNodes = (w.keys['oneway'] == 'yes') ? w.nodes.tail() : w.nodes;
    	List searchNodes = w.nodes;
    	int startIdx = searchNodes.indexOf(start);
    	if(startIdx < 0) return [];
    	if(forward) {
    		if(startIdx==searchNodes.size()-1) return []; // if start node is the last, return
    		searchNodes = searchNodes[startIdx+1..searchNodes.size()-1];
        } else if(startIdx > 0){
        	searchNodes = searchNodes[startIdx-1..0];
        } else {
        	return [];
        }
    	searchNodes.each { node ->
    		if(node != start) { // handle circular ways
	    		def nodeWays = dataSet.getNodeWays(node);
	    		def inter = nodeWays.findAll {iWay -> 
					// exclude self and oneway streets ending at self
					(iWay != w) && (!filter || (iWay.keys['oneway'] != 'yes' || iWay.nodes.last() != node));
	    		};
	    		if(inter) {
	    			wayIntersections << [node, inter];
	        	}
    		}
        }
    	return wayIntersections;
     }

	protected static mainWays;
    public getRandomRoute(Node startNode, int minLength) {
    	if(startNode) {
			List ways = dataSet.getNodeWays(startNode);
			if(!ways) return; 
	    	getRandomRouteImpl([], startNode, ways[rg.nextInt(ways.size())], minLength, 0);
    	} else {
			//MAINWAYS
			if(!mainWays) mainWays = setup.MAIN_WAYS ?  (ways.findAll{ way -> way.keys['highway'] != 'residential' && way.keys['highway'] != 'tertiary';}) : ways;
			//if(!mainWays) mainWays = ways.findAll{ way -> way.keys['highway'] != 'residential' && way.keys['highway'] != 'tertiary';}
        	def nodes = mainWays[rg.nextInt(mainWays.size())].nodes;
        	if(!nodes) return;
        	Node start = nodes[rg.nextInt(nodes.size())];
        	getRandomRoute(start, minLength);
        }
    }
	

    private getRandomRouteImpl(route, Node startNode, Way startWay, int minLength, int nIntersections) {
    	if(nIntersections == minLength) return [nInter: nIntersections, route: addWaySegments(route, startWay, startNode, startWay.nodes.last())];
		// get way intersections 
		def intersections = getWayIntersections(startWay, startNode, true, true);
		if(startWay.keys['oneway'] != 'yes') {
			intersections.addAll(getWayIntersections(startWay, startNode, false, true));
		}
		// exclude intersections with the current route
		def newIntersections = [];
		intersections?.each{ inter -> 
			def (node, ways) = inter;
			def newWays = ways.findAll {w -> route.every {segment -> segment.way != w;}}
			if(newWays) {
				newIntersections << [node, newWays];
			}
		}
		if(!newIntersections) return [nInter: nIntersections, route: addWaySegments(route, startWay, startNode, startWay.nodes.last())];
		// choose intersection
		def(endNode, nextWay) = selectRandomIntersection(newIntersections);
		addWaySegments(route, startWay, startNode, endNode);
		nIntersections++; //???  incrementing directly in getRandomRouteImpl doesn't work ???
		getRandomRouteImpl(route, endNode, nextWay, minLength, nIntersections);

    }

    private addWaySegments(route, Way w, Node start, Node end) {
    	if(start == end) return route;
		def wayNodes = w.nodes;
		wayNodes = wayNodes[wayNodes.indexOf(start)..wayNodes.indexOf(end)];
		def segments = [];
		wayNodes[0..wayNodes.size()-2].eachWithIndex { node, idx ->
			segments << [start: node, end: wayNodes[idx+1], way: w];
		}
		route.addAll(segments);
		return route;
    }
    
//	if(highwayType.equals("motorway") || highwayType.equals("motorway_link") || highwayType.equals("trunk") || highwayType.equals("primary") ||
//			highwayType.equals("secondary") || highwayType.equals("tertiary") || highwayType.equals("residential")) {  
    private selectRandomIntersection(intersections) {
    	def intSplit = [];
    	intersections.each{ inter ->
    		def (endNode, ways) = inter;
    		ways.each{ w -> intSplit << [endNode, w]};
        }

		//MAINWAYS
	if(setup.MAIN_WAYS) {
    	def byType = [intSplit.findAll{ inter -> inter[1].keys['highway'] == 'motorway' || inter[1] == inter[1].keys['highway'] == 'motorway_link' || inter[1].keys['highway'] == 'trunk' || inter[1].keys['highway'] == 'trunk_link' || inter[1].keys['highway'] == 'primary' },
    	   intSplit.findAll{ inter -> inter[1].keys['highway'] == 'secondary'},
    	   intSplit.findAll{ inter -> inter[1].keys['highway'] == 'tertiary'}, //|| inter[1].keys['highway'] == 'residential'
    	];

    	for(inter in byType) {
    		if(inter && rg.nextDouble()<=0.6) {
    			return inter[rg.nextInt(inter.size())];
        	}
        }
	}
	
    	return intSplit[rg.nextInt(intSplit.size())];
    }
	
	public Collection<String> getClosestSegment(EastNorth en) {
		//TODO;
		return null;
	}
	
	protected static segmentExtentCache = [:];
	public Rectangle getSegmentExtent(String segmentId) {
		def extentCoords = [xmin: Double.POSITIVE_INFINITY, xmax: Double.NEGATIVE_INFINITY, ymin: Double.POSITIVE_INFINITY, ymax: Double.NEGATIVE_INFINITY];

		def segmentTokens = segmentId.tokenize("_");
		long wayId = Long.parseLong(segmentTokens[0]);
		int segmentN = Integer.parseInt(segmentTokens[1]);

		Rectangle extent;
		if((extent = segmentExtentCache[segmentId]) != null) {
			return extent;
		}

		Way way = dataSet.getPrimitiveById(wayId, OsmPrimitiveType.WAY);
		if(!way) {
			println "unknown way ${wayId} for segment ${segmentId}"
		}
		def nodes = way.nodes;
		int currentSegmentN = 0;
		int currentNodeIdx = 0;
		double currentSegmentSize = 0;
		boolean hasExtent = false;
		
		while(currentNodeIdx < nodes.size()) {
			if(currentSegmentN == segmentN) {
				addCoords(extentCoords, nodes[currentNodeIdx].coor);
				hasExtent = true;
			}
			if(currentNodeIdx < nodes.size()-1){
				double size = nodes[currentNodeIdx].coor.greatCircleDistance(nodes[currentNodeIdx+1].coor);
				if(currentSegmentSize + size >= PipelineSimulation.setup.SEGMENT_SIZE) {
					double split = PipelineSimulation.setup.SEGMENT_SIZE-currentSegmentSize;
					double r = split/size;
					LatLon splitCoor = nodes[currentNodeIdx].coor.interpolate(nodes[currentNodeIdx+1].coor, r);
					if(currentSegmentN == segmentN || currentSegmentN+1 == segmentN) {
						addCoords(extentCoords, splitCoor);
						hasExtent = true;
					}
					currentSegmentN++;
					currentSegmentSize = size - split;
				} else {
					currentSegmentSize += size;
				}
			}
			currentNodeIdx++;
		}
		if(hasExtent) {
			extent = new Rectangle(new Point2D.Double(extentCoords.xmin,extentCoords.ymin), extentCoords.xmax-extentCoords.xmin, extentCoords.ymax-extentCoords.ymin);
			segmentExtentCache[segmentId] = extent;
			return extent;
		} else {
			println "segment has no extent: ${segmentId}"
			println "currentSegmentN: ${currentSegmentN}"
			return null;
		}
	}
	

	protected addCoords(extentCoords, LatLon coor) {
		double x = coor.lon();
		double y = coor.lat();
		extentCoords.xmin = Math.min(extentCoords.xmin, x);
		extentCoords.xmax = Math.max(extentCoords.xmax, x);
		extentCoords.ymin = Math.min(extentCoords.ymin, y);
		extentCoords.ymax = Math.max(extentCoords.ymax, y);
	}
}
