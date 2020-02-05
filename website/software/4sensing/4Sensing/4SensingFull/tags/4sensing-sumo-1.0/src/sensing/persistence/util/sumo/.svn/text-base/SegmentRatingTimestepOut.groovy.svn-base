package sensing.persistence.util.sumo;
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.awt.geom.Rectangle2D;

mapModel =  new SUMOMapModel()
mapModel.load ("koeln_bbox_net.xml")

def sr = new SegmentRating("sumocfg3_vtypeprobe_5s_nointernal.xml", mapModel)

Rectangle2D world = mapModel.bounds.asRect();
double width = 0.25 * world.width;
double height = 0.25 * world.height;
double minLat = world.centerY-height/2;
double minLon = world.centerX-width/2;
Rectangle2D qArea = new Rectangle2D.Double(minLon, minLat, width, height);


File f = new File("segmentRating_1800_ts.tsv")
f.write("")
def valid = mapModel.edges.findAll {SUMOMapModel.Edge e -> qArea.contains(e.bbox)}

double startTs = 21600
double endTs = 23395
double ts = startTs
double readTo = ts+295;

while(readTo <= endTs) {
	sr.reset()
	sr.readTo(readTo)
	double timestep = sr.timestep - startTs
	double total = 0;
	
	valid.each {SUMOMapModel.Edge e ->
		double traveledDistance = sr.segmentData[e.id]?.traveledDist ?: 0
		if(traveledDistance > 0) {
			total += traveledDistance
			String line =  "${timestep+5}\t${e.id}\t${traveledDistance}\n"
			//print line
			f.append(line)
		}
	}
	println "Timestep: $timestep > ${total/1000}"
	ts = sr.timestep
	readTo = ts+300
}
