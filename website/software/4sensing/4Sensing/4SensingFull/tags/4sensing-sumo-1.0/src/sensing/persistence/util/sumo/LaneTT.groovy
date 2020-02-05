package sensing.persistence.util.sumo
import org.apache.log4j.TTCCLayout;
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import  sensing.persistence.simsim.speedsense.sumo.*;

import java.awt.geom.Rectangle2D;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import sensing.persistence.simsim.map.sumo.LinearProjection;
import  sensing.persistence.simsim.speedsense.sumo.SUMOEdgeStatsSource;

def mapModel = new SUMOMapModel()
mapModel.load ("koeln_bbox_net.xml")

Rectangle2D world = mapModel.bounds.asRect();
double width = 0.25 * world.width;
double height = 0.25 * world.height;
double minLat = world.centerY-height/2;
double minLon = world.centerX-width/2;
Rectangle2D qArea = new Rectangle2D.Double(minLon, minLat, width, height);


int timestep = -1;
int startTs = -1;

def laneData = [:]

def getLaneData = {String laneId ->
	def ld = laneData[laneId]
	if(!ld) {
		laneData[laneId] = ld = [totalSpeed:0, numSamples: 0]	
	} 	
	return ld
}


def timestepStart = {double ts -> 
	timestep = ts
	if(startTs == -1) {
		startTs = timestep
	}
	printf ("timestepStart: %d\n", (int)ts)
	if(timestep > startTs && (timestep-startTs) % 300 == 0) {
		printf ("\t\toutput %d\n", (int)timestep)
		
		segmentData = [:]
		
		laneData.each {String laneId, Map ld -> 
			String segmentId = laneId.substring(0, laneId.indexOf("_"))
			if(ld.numSamples >= 10) {
				segmentData[segmentId] = (segmentData[segmentId] ?: []) + ld.totalSpeed/ld.numSamples
			}
		}
		
		
		
		segmentData.findAll {String segmentId, List speeds -> speeds.size() > 1 && qArea.contains(mapModel.getEdge(segmentId).bbox) && (speeds.max() - speeds.min() >= 6.5)}.each {
			String segmentId, List speeds -> println "${segmentId}\t\t\t ${3.6*(speeds.max() - speeds.min())}\t${speeds.min()*3.6}\t${speeds.max()*3.6}\t${mapModel.getEdge(segmentId).length}"	
		}
		laneData = [:]
	}
	
}

def timestepEnd = {
	//printf ("timestepEnd: %d\n", (int)timestep)
}

def vehicleUpdate = { String vehicleId, double lat, double lon, double x, double y, double speed, String laneId ->
	def ld = getLaneData(laneId)
	ld.totalSpeed += speed
	ld.numSamples++
}


SUMOVProbeSource tracesSrc = new SUMOVProbeSource("sumocfg3_vtypeprobe_5s_nointernal.xml",
	[timestepStart: timestepStart, timestepEnd: timestepEnd, vehicleUpdate: vehicleUpdate] as SUMOVProbeHandler)
tracesSrc.init()


while(tracesSrc.hasNextTimestep()) {
	tracesSrc.readNextTimestep()
}
timestepStart(timestep+5)




