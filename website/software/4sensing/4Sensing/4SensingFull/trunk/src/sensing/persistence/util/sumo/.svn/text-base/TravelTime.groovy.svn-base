package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import  sensing.persistence.simsim.speedsense.sumo.*;

class TravelTime {
	SUMOVProbeSource tracesSrc
	int timestep = -1;
	int startTs = -1;
	
	def segmentData = [:]
	def vehicleData = [:]
	
	class Handler extends SUMOVProbeHandler {
		void timestepStart(double ts) {
			timestep = ts
			if(startTs == -1) {
				startTs = timestep
			}
		}
		
		void timestepEnd() {
			printf ("timestepEnd: %d\n", (int)timestep)
		}
		
		void vehicleUpdate( String vehicleId, double lat, double lon, double x, double y, double speed, String laneId, double pos) {
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
		
	}
	
	public TravelTime(String inFileName) {
		tracesSrc = new SUMOVProbeSource(inFileName, new  Handler())
		tracesSrc.init()
	}
	
	def getSegmentData(String segmentId) {
		def tt = segmentData[segmentId]
		if(!tt) {
			segmentData[segmentId] = tt = []
		}
		return tt
	}
	
	def getVehicleData(String vehicleId) {
		def vd = vehicleData[vehicleId]
		if(!vd) {
			vehicleData[vehicleId] = vd = [segmentId: null, entryTs: null, lastTs: null, first: null]
		}
		return vd
	}
	
	public void process(Closure p) {
		p.delegate = this
		while(tracesSrc.hasNextTimestep()) {
			p()
			tracesSrc.readNextTimestep()
		}
	}
	
	public void readTo(double ts) {
		while(tracesSrc.hasNextTimestep() && timestep < ts) {
			tracesSrc.readNextTimestep()
		}
	}
	
	public boolean hasNextTimestep() {
		tracesSrc.hasNextTimestep()	
	}

}
