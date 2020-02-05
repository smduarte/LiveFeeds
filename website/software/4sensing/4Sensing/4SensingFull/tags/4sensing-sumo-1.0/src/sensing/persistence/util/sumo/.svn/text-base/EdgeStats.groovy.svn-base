package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import  sensing.persistence.simsim.speedsense.sumo.SUMOEdgeStatsSource;
import java.awt.geom.Rectangle2D;

def mapModel = new SUMOMapModel();
mapModel.load ("koeln_bbox_net.xml");

Rectangle2D world = mapModel.bounds.asRect();
double width = 0.25 * world.width;
double height = 0.25 * world.height;
double minLat = world.centerY-height/2;
double minLon = world.centerX-width/2;
Rectangle2D area = new Rectangle2D.Double(minLon, minLat, width, height);



SUMOEdgeStatsSource edgeStats= new SUMOEdgeStatsSource("sumocfg3_meandata-edge_5m_nointernal.xml", 300, mapModel);
edgeStats.init();

def bSpeed = [];
def bSpeedVar = [];
def bTl = [];
def bSamples = [];

def edges = mapModel.edges.findAll {area.contains(it.bbox)}

edges.each{ SUMOMapModel.Edge e ->
	if(e.hasTrafficLight) {
		int bin = (int)e.length/50.0
		if(bTl[bin] == null) {
			bTl[bin] = 1;
		} else {
			bTl[bin]++;
		}
	}
}


while(edgeStats.hasNextPeriod()) {
	edgeStats.readNextPeriod();	
	
	edges.each{ SUMOMapModel.Edge e ->
		int bin = (int)e.length/50.0
		if(e.avgSpeed >= 0) {		
			if(bSpeed[bin] == null) {
				bSpeed[bin] = [];
				bSamples[bin] = [];
			}
			bSpeed[bin] << e.avgSpeed;
			bSamples[bin] << e.sampledSeconds;
		}
		
		if(e.pAvgSpeed >= 0) {
			double sVar = Math.abs(e.pAvgSpeed - e.avgSpeed);
			if(bSpeedVar[bin] == null) {
				bSpeedVar[bin] = [];
			}
			bSpeedVar[bin] << sVar;
		}
	}
}

File outF = new File("edgeStats_length.gpd");
String head = "LEN\tFREQ\tMIN\tMAX\tAVG\tSTD\tFREQTL\tPTL\tAVG_SVAR\tAVG_SAMP\n"
print head;
outF.write(head);

bSpeed.eachWithIndex { c, idx ->
	if(c) {
		double mean = c.sum() / c.size();
		double var = (c.inject(0) {sum, val -> sum + (val - mean) * (val - mean)})/(c.size()-1)
		double std = Math.sqrt(var)
		int len = idx*50+50;
		int freqTl = bTl[idx] ? bTl[idx] : 0;
		double pTl = freqTl/c.size()*100;
		double avgSpeedVar = bSpeedVar[idx].sum()/bSpeedVar[idx].size();
		double avgSamples = bSamples[idx].sum()/bSamples[idx].size()
		def line = String.format("%d\t%d\t%.2f\t%.2f\t%.2f\t%.2f\t%d\t%.2f\t%.2f\t%.2f\n", len, c.size(), c.min()*3.6, c.max()*3.6, mean*3.6, std*3.6, freqTl, pTl,avgSpeedVar*3.6, avgSamples)
		print line
		outF.append(line)
	}
}



