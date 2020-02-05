package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import  sensing.persistence.simsim.speedsense.sumo.*;

def mapModel = new SUMOMapModel()
mapModel.load ("koeln_bbox_net.xml")


int timestep = -1;
int startTs = -1;

def segmentData = [:]
def vehicleData = [:]

File outF = new File("travel_time.gpd")
outF.write("#TIME\tSEGID\tLEN\tMAXSP\tMETIME\tTMIN\tTMAX\tTAVG\tTSTD\tTCOUNT\n");

def getSegmentData = {String segmentId ->
	def tt = segmentData[segmentId]
	if(!tt) {
		segmentData[segmentId] = tt = []	
	} 	
	return tt
}

def getVehicleData = {String vehicleId ->
	def vd = vehicleData[vehicleId]
	if(!vd) {
		vehicleData[vehicleId] = vd = [segmentId: null, entryTs: null, lastTs: null, first: null]	
	} 	
	return vd
}

def timestepStart = {double ts -> 
	timestep = ts
	if(startTs == -1) {
		startTs = timestep
	}
}

def timestepEnd = {
	printf ("timestepEnd: %d\n", (int)timestep)
}

def vehicleUpdate = { String vehicleId, double lat, double lon, double x, double y, double speed, String laneId ->
	String segmentId = laneId.substring(0, laneId.indexOf("_"))

	def vd = getVehicleData(vehicleId)
	if(!vd.segmentId || (timestep - vd.lastTs > 5) ) { // new or teleported
//		if(vd.segmentId && timestep - vd.lastTs > 5)  {
//			println("$vehicleId] teleported")
//		}
		vd.segmentId = 	segmentId
		vd.entryTs = timestep
		vd.first = true
	} else if(segmentId != vd.segmentId) {
		double tt = timestep - vd.entryTs
		if(!vd.first) {
			getSegmentData(vd.segmentId) << tt
		}
		vd.segmentId = segmentId
		vd.entryTs = timestep
		vd.first = false
	}
	vd.lastTs = timestep
}


SUMOVProbeSource tracesSrc = new SUMOVProbeSource("sumocfg3_vtypeprobe_5s_nointernal.xml",
	[timestepStart: timestepStart, timestepEnd: timestepEnd, vehicleUpdate: vehicleUpdate] as SUMOVProbeHandler)
tracesSrc.init()

def process = { 
	if(startTs > -1 && (timestep+5-startTs) % 300 == 0) {
		printf ("\t\toutput %d\n", (int)timestep+5)
		segmentData.each{String segmentId, data ->
			int time = timestep-startTs+5
			double min = data.min()
			double max = data.max()
			double mean = data.sum()*1.0/data.size()
			double std = Math.sqrt(data.inject(0) {acc, val -> acc + (val-mean)*(val-mean)}/(data.size()-1))
			SUMOMapModel.Edge e = mapModel.getEdge(segmentId)
			double length = e.length
			double maxSpeed = e.maxSpeed
			double meTime = length/maxSpeed
			outF.append("$time\t $segmentId\t$length\t$maxSpeed\t$meTime\t$min\t$max\t$mean\t$std\t${data.size()}\n")
			//printf("%s\t%.1f\t%.2f\n", segmentId, length, mean)
		}
		segmentData = [:]
	}
}

while(tracesSrc.hasNextTimestep()) {
	process()
	tracesSrc.readNextTimestep()
}