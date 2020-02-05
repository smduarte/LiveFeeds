package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;

File outF = new File("travel_time.gpd")
outF.write("#TIME\tSEGID\tLEN\tMAXSP\tMETIME\tTMIN\tTMAX\tTAVG\tTSTD\tTCOUNT\n");

def mapModel = new SUMOMapModel()
mapModel.load ("koeln_bbox_net.xml")


TravelTime t = new TravelTime("sumocfg3_vtypeprobe_5s_nointernal.xml")

int interval = 21895

while(t.hasNextTimestep()) {
	t.readTo(interval)
	if(t.segmentData.size()>0) {
		printf ("\t\toutput %d\n", (int)t.timestep+5)
		t.segmentData.each{ String segmentId, data ->
			int time = t.timestep-t.startTs+5
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
		t.segmentData.clear()
	}
	interval += 300
}