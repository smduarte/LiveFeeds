package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import  sensing.persistence.simsim.speedsense.sumo.*;

class SegmentRating {
	SUMOMapModel mapModel
	SUMOVProbeSource tracesSrc
	int timestep = -1
	int startTs = -1
	
	def segmentData = [:]
	def vehicleData = [:]
	
	
	class Handler implements SUMOVProbeHandler {
		
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
			if(!vd.segmentId ) { // new or teleported
				vd.segmentId = 	segmentId
				vd.pos = pos
				getSegmentData(segmentId).traveledDist += pos
			} else if(segmentId != vd.segmentId) { // crossed segment
				double length = mapModel.getEdge(vd.segmentId).length
				getSegmentData(vd.segmentId).traveledDist += (length - vd.pos)
				vd.segmentId = segmentId
				vd.pos = pos
				getSegmentData(segmentId).traveledDist += pos
			} else { // same segment
				getSegmentData(vd.segmentId).traveledDist += (pos - vd.pos)
				vd.pos = pos
			}
		}
	}
		
	
	public SegmentRating(String inFileName, SUMOMapModel mapModel) {
		this.mapModel =  mapModel
		tracesSrc = new SUMOVProbeSource(inFileName, new  Handler())
		tracesSrc.init()
	}
	
	def getSegmentData(String segmentId) {
		def tt = segmentData[segmentId]
		if(!tt) {
			segmentData[segmentId] = tt = [traveledDist:0]
		}
		return tt
	}
	
	def getVehicleData(String vehicleId) {
		def vd = vehicleData[vehicleId]
		if(!vd) {
			vehicleData[vehicleId] = vd = [segmentId: null, pos: null]
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
	
	public void readNextTimeStep() {
		tracesSrc.readNextTimestep()
	}
	
	public void readTo(double ts) {
		while(tracesSrc.hasNextTimestep() && timestep < ts) {
			tracesSrc.readNextTimestep()
		}
	}
	
	public void readAll() {
		while(tracesSrc.hasNextTimestep()) {
			tracesSrc.readNextTimestep()
		}
	}
	
	public boolean hasNextTimestep() {
		tracesSrc.hasNextTimestep()	
	}
	
	public void reset() {
		segmentData = [:]
	}
}
	
	

	
