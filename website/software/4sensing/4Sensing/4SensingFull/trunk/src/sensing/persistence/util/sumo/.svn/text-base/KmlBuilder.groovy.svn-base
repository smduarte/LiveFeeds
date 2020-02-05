package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.awt.geom.Rectangle2D;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import sensing.persistence.simsim.map.sumo.LinearProjection;
import  sensing.persistence.simsim.speedsense.sumo.SUMOEdgeStatsSource;


def mapModel = new SUMOMapModel();
mapModel.load ("koeln_bbox_net.xml");
LinearProjection proj = mapModel.getProjection();





Rectangle2D world = mapModel.bounds.asRect();
double width = 0.25 * world.width;
double height = 0.25 * world.height;
double minLat = world.centerY-height/2;
double minLon = world.centerX-width/2;
Rectangle2D qArea = new Rectangle2D.Double(minLon, minLat, width, height);
EastNorth minEN = proj.latlon2eastNorth(new LatLon(minLat, minLon))
EastNorth maxEN = proj.latlon2eastNorth(new LatLon(minLat+height, minLon+width))
Rectangle2D qProjArea = new Rectangle2D.Double(minEN.east(), minEN.north(), maxEN.east()-minEN.east(), maxEN.north()-minEN.north())


def segments = [:]
double totalLength = 0

def containedSegments = mapModel.edges.findAll {qArea.contains(it.bbox)}

HashSet sampledSegments = new HashSet();

SUMOEdgeStatsSource edgeStats= new SUMOEdgeStatsSource("sumocfg3_meandata-edge_5m_nointernal.xml", 300, mapModel);
edgeStats.init();

while(edgeStats.hasNextPeriod()) {
	edgeStats.readNextPeriod();
	containedSegments.each{ SUMOMapModel.Edge e ->
		if(e.sampledSeconds > 0 && e.length >= 100) {
			sampledSegments.add(e)
		}
	}
}

sampledSegments.each{SUMOMapModel.Edge e ->
	segments[e.id] = [seg: e, length: e.length, maxError: -1]; 
	totalLength += e.length
}

String fName = "/Users/heitor2/4Sensing/results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedSetup/run_smartnode/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/speedchange/results_100.gpd"
println fName
File f = new File(fName)
f.eachLine{ String line ->
	def fields = line.split();
	def (count, vCount, agrSpeed, vpMinSpeed, vpMaxSpeed, vpAvgSpeed, vpStdSpeed, vpSamples, sumoSpeed, avgOccupancy, avgDensity, sampledSeconds, maxSpeed, numLanes, length, hasTrafficLight, contained) =
		fields[0..-3]*.toDouble()
		
	def segmentId = fields[17];
	def time = fields[18].toDouble();
	
	if(sumoSpeed >= 0 && length >= 100) {
		double error = Math.abs(agrSpeed-sumoSpeed);
		segments[segmentId].maxError = Math.max(segments[segmentId].maxError, error);
	}
}

def lt5 = segments.values().findAll{it.maxError >= 0 && it.maxError <=5}
double lt5Length = lt5.inject(0) {sum, val -> sum + val.length}


def lt10 = segments.values().findAll{it.maxError >=0 && it.maxError <=10}
double lt10Length = lt10.inject(0) {sum, val -> sum + val.length}


printf("Total Length: %.2f Km\n", totalLength/1000)
printf("Lt5 length: %.2f Km\n", lt5Length/1000)
printf("Lt5%%: %.2f%% , %.2f%% Km\n", lt5.size()*1.0/segments.size()*100, lt5Length/totalLength*100)
printf("Lt10 length: %.2f Km\n", lt10Length/1000)
printf("Lt10%%: %.2f%% , %.2f%% Km\n", lt10.size()*1.0/segments.size()*100, lt10Length/totalLength*100)
/*
def writer = new FileWriter("out.kml")
writer.write("<?xml version='1.0' encoding='UTF-8'?>\n")


//def writer = new StringWriter();
def builder = new groovy.xml.MarkupBuilder(writer) 

def getCoordinates = {String shapeDesc, double altitude -> 
	def res = shapeDesc.split(";")[0].split().collect{ coordS ->
		def coord = coordS.split(",");
		double east = coord[0].toDouble()
		double north = coord[1].toDouble()
		EastNorth en = new EastNorth(east, north)
		LatLon ll = proj.eastNorth2latlon(en)
		"${ll.lon()},${ll.lat()},$altitude"
	}
	return res.join("\n")
}

def getStyle = {String segmentId -> 
	double error = segments[segmentId].maxError
	String style;
	switch(error) {
		case {it < 0}: 
			style="segDefault"
			break
		case {it <= 5}:
			style="segLow"
			break
		case {it <= 10}:
			style="segMed"
			break
		default:
			style="segHigh"
			break
	}
	style	
}

def result = builder.kml(xmlns: 'http://www.opengis.net/kml/2.2', 'xmlns:gx': 'http://www.google.com/kml/ext/2.2') {
	Document {
		name 'Speed Sense'
		Style(id: 'queryStyle') {
			LineStyle {
				color '7f00ffff'
				builder.'gx:physicalWidth' '10'
			}
		}
		Style(id: 'segDefault') {
			LineStyle {
				color 'ffcccccc'
				builder.'gx:physicalWidth' '2'
			}
		}
		Style(id: 'segLow') {
			LineStyle {
				color 'ff00ff00'
				builder.'gx:physicalWidth' '2'
			}
		}
		Style(id: 'segMedium') {
			LineStyle {
				color 'ff00ffff'
				builder.'gx:physicalWidth' '2'
			}
		}
		Style(id: 'segHigh') {
			LineStyle {
				color 'ff0000ff'
				builder.'gx:physicalWidth' '2'
			}
		}
		Placemark {
				name 'Query Area'
				description 'Query Area'
				styleUrl '#queryStyle'
				LineString {
					extrude '0'
					tessellate '1'
					altitudeMode 'clampToGround'
					coordinates	"$minLon,$minLat,2357\n$minLon,${minLat+height},2357\n${minLon+width},${minLat+height},2357\n${minLon+width},$minLat,2357\n$minLon,$minLat,2357\n"
				}
		}
		mapModel.getEdges(qProjArea).each{SUMOMapModel.Edge e ->
			Placemark {
				name e.id
				description e.id
				styleUrl "#${getStyle(e.id)}"
				LineString {
					extrude '0'
					tessellate '1'
					altitudeMode 'clampToGround'
					coordinates getCoordinates(e.shapeDesc,2357)
				}
			}
		}
	}
}

writer.close();
*/