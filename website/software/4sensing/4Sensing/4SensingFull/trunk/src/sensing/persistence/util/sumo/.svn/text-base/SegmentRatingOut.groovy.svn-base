package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.awt.geom.Rectangle2D;

String mapData = "koeln_bbox_net.xml"
String vProbeData = "sumocfg3_vtypeprobe_5s_nointernal.xml"
int endTime = 23400
String outFile = "segmentRating_1800.tsv"

mapModel =  new SUMOMapModel()
mapModel.load (mapData)

Rectangle2D world = mapModel.bounds.asRect();
double width = 0.25 * world.width;
double height = 0.25 * world.height;
double minLat = world.centerY-height/2;
double minLon = world.centerX-width/2;
Rectangle2D qArea = new Rectangle2D.Double(minLon, minLat, width, height);

def sr = new SegmentRating(vProbeData, mapModel)
sr.readTo(endTime)

File f = new File(outFile)
f.write("")
def valid = mapModel.edges.findAll {SUMOMapModel.Edge e -> qArea.contains(e.bbox)}

valid.each {SUMOMapModel.Edge e ->
	double traveledDistance = sr.segmentData[e.id] ? sr.segmentData[e.id].traveledDist: 0
	if(traveledDistance > 0) {
		String line =  "${e.id}\t${traveledDistance}\n"
		print line
		f.append(line)
	}
}