package sensing.simsim.app.osm;

import static sensing.simsim.sys.Sim4Sensing.mapModel;
import static sensing.core.logging.LoggingProvider.*;
import sensing.simsim.sys.MobileNode;
import sensing.simsim.sys.Node;
import sensing.simsim.sys.map.osm.OSMMapModel;
import sensing.simsim.app.osm.OSMSpeedSenseSim;
import sensing.simsim.app.tuples.MappedSpeed;
import sensing.core.sensors.GPSReading;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.coor.CachedLatLon;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

import simsim.gui.geom.*;
import simsim.gui.canvas.*;
import simsim.core.Globals;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.Random;

public class TrafficSpeedModel {
	protected static final maxSpeeds = [motorway: 120.0, motorway_link: 60.0, trunk: 50.0, trunk_link: 50.0,  primary:50.0, secondary:50.0, tertiary:30.0, residential: 30.0];
	
	def nodeSegmentLocation = [:];
	def segmentNodeDensity = [:];
	def segmentNodeDensHist = [];
	
	Random rg;
	
	public TrafficSpeedModel() {
		rg = OSMSpeedSenseSim.rg;
	}
		
	public void update() {
		if(segmentNodeDensHist.size() == OSMSpeedSenseSim.setup.DENSITY_HISTORY_SIZE ) {
			segmentNodeDensHist.pop();
		}
		segmentNodeDensHist.putAt(0, segmentNodeDensity.clone());
		speedCache = [:];
	}

	protected double getAverageDensity(String segmentId) {
		if(segmentNodeDensHist.size() > 0) {
			int totalDensity = segmentNodeDensHist.inject(0){sum, hist -> sum += hist[segmentId] ? hist[segmentId] : 0}
			return totalDensity * 1.0 / segmentNodeDensHist.size();
		} else {
			return 0;
		}
	}


	public double getSpeed(segment, String segmentId, MobileNode node) {
		getSpeedImpl(segment, segmentId, node);
	}

	protected double getSpeedImpl(segment, segmentId, MobileNode node) {
		if(nodeSegmentLocation[node.id]) {
			if(nodeSegmentLocation[node.id] != segmentId) {
				segmentNodeDensity[nodeSegmentLocation[node.id]]--;
				nodeSegmentLocation[node.id] = segmentId;
				segmentNodeDensity[segmentId] = segmentNodeDensity[segmentId] ? segmentNodeDensity[segmentId]+1 : 1;
			}
		} else {
			nodeSegmentLocation[node.id] = segmentId;
			segmentNodeDensity[segmentId] = segmentNodeDensity[segmentId] ? segmentNodeDensity[segmentId]+1 : 1;
		}
		double avgSpeed = getSegmentSpeed(segment.way, segmentId);
		double speed = Math.max(rg.nextGaussian() * OSMSpeedSenseSim.setup.SPEED_STD_DEV + avgSpeed, OSMSpeedSenseSim.setup.MIN_SPEED);
		return speed;
	}
	
	def static wayCache = [:];
	
	public Way getSegmentWay(String segmentId) {
		Way w;
		if((w = wayCache[segmentId]) == null) {
			def segmentTokens = segmentId.tokenize("_");
			 wayCache[segmentId] = w = mapModel.dataSet.getPrimitiveById(Long.parseLong(segmentTokens[0]), OsmPrimitiveType.WAY);
		}
		return w;
	}
	
	public double getSegmentSpeedImpl(Way w, String segmentId) {
		double maxSpeed = maxSpeeds[w.keys['highway']];
		assert(maxSpeed);
		int density = getAverageDensity(segmentId);
		double speed =  maxSpeed/(1.0+ density*OSMSpeedSenseSim.setup.DENSITY_SPEED_WEIGHT);
		return speed;
	}

    def static speedCache = [:];
    protected double getSegmentSpeed(Way w, String segmentId) {
            def stored = speedCache[segmentId];
            if(stored == null) {
                    speedCache[segmentId] = stored = getSegmentSpeedImpl(w, segmentId);
            }
            return stored;
    }


	protected double getDistance(nodes, org.openstreetmap.josm.data.osm.Node segmentStart, int nodeIdx) {
		if(nodes[nodeIdx] == segmentStart) { 
			return 0
		} else {
			return nodes[nodeIdx].coor.greatCircleDistance(nodes[nodeIdx+1].coor) + getDistance(nodes, segmentStart, nodeIdx+1);
		}
	}

	protected double getDistance(long wayId, nodes, org.openstreetmap.josm.data.osm.Node segmentStart, LatLon pos) {
		double distToStartNode = getDistance(nodes, segmentStart, 0);
		distToStartNode += nodes[nodes.indexOf(segmentStart)].coor.greatCircleDistance(pos);
	}

	public boolean isCongested(String segmentId, double speed) {
		Way way = getSegmentWay(segmentId);
		return speed <= maxSpeeds[way.keys.highway]*OSMSpeedSenseSim.setup.CONGESTION_FACTOR;
	}
	
	public isCongested(String segmentId) {
		Way way = getSegmentWay(segmentId);
		double speed = getSegmentSpeed(way, segmentId);
		return speed < maxSpeeds[way.keys.highway]*OSMSpeedSenseSim.setup.CONGESTION_FACTOR;
	}
	
	public getSegmentDensity(String segmentId) {
		return segmentNodeDensity[segmentId];
	}
	
	public maxSpeed(String segmentId) {
		Way way = getSegmentWay(segmentId);
		return maxSpeeds[way.keys.highway]
	}

	public String _getSegmentId(segment, LatLon pos) {
		def wayNodes = segment.way.nodes;
		String direction;
		double distance;
		if(wayNodes.indexOf(segment.end) > wayNodes.indexOf(segment.start)) {
			direction = "F";
			distance = getDistance(segment.way.id, wayNodes, segment.start, pos);
		} else {
			direction = "B";
			distance = getDistance(segment.way.id, wayNodes, segment.end, pos);
		}
		int segmentN = (int)(distance/OSMSpeedSenseSim.setup.SEGMENT_SIZE);
		return "${segment.way.id}_${segmentN}_${direction}";		
	}

	protected wayNodesCache = [:];
	protected distanceCache = [:];
	public String getSegmentId(segment, double distance) {
		//return  "${segment.way.id}_0_F";
		def wayNodes;
		if((wayNodes = wayNodesCache[segment.way.id]) == null) {
			wayNodes = segment.way.nodes;
			wayNodesCache[segment.way.id] = wayNodes;
		} 
		
		String direction;
		String key = "${segment.way.id}_${segment.start.id}";
		double distanceToStart = 0;
		def cached;
		if((cached = distanceCache[key]) != null) {
			distanceToStart = cached;
		} else {
			distanceToStart = getDistance(wayNodes, segment.start, 0);
			distanceCache[key] = distanceToStart;
		}
		double totalDistance;
		if(wayNodes.indexOf(segment.end) > wayNodes.indexOf(segment.start)) {
			direction = "F";
			totalDistance = distanceToStart + distance;
		} else {
			direction = "B";
			totalDistance = distanceToStart - distance;
		}
		int segmentN = (int)(totalDistance/OSMSpeedSenseSim.setup.SEGMENT_SIZE);
		return "${segment.way.id}_${segmentN}_${direction}";
	}
		
	public MappedSpeed map(GPSReading r) {
		return new MappedSpeed( segmentId: r.segmentId,
				speed: r.speed, 
				boundingBox: getSegmentExtent(r.segmentId),
				time: r.time);	
	}
}
