package sensing.persistence.simsim.speedsense.sumo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import simsim.core.PeriodicTask;


import sensing.persistence.simsim.map.sumo.SUMOMapModel;

public class SUMOEdgeStatsSource {
	final static int ATTR_INTERVAL_BEGIN = 0;
	final static int ATTR_INTERVAL_END = 1;
	final static int ATTR_EDGE_ID = 0;
	final static int ATTR_EDGE_SAMPLEDSECONDS = 1;
	final static int ATTR_EDGE_TRAVELTIME = 2;
	final static int ATTR_EDGE_DENSITY = 3;
	final static int ATTR_EDGE_OCCUPANCY = 4;
	final static int ATTR_EDGE_SPEED = 6;
	final static int ATTR_EDGE_VP_TRAVELTIME_COUNT = 13;
	final static int ATTR_EDGE_VP_TRAVELTIME_MIN = 14;
	final static int ATTR_EDGE_VP_TRAVELTIME_MAX= 15;
	final static int ATTR_EDGE_VP_TRAVELTIME_AVG = 16;
	final static int ATTR_EDGE_VP_TRAVELTIME_STD = 17;
	
	final static int MIN_SAMPLES = 0;
	

	final String fileName;
	final double period;
	FileInputStream fileInputStream;
	InputStream bufferedInputStream;
	XMLStreamReader reader;
	PeriodicTask tStep;
	SUMOMapModel mapModel;
	
	SUMOEdgeStatsSource(String fileName, double period, SUMOMapModel mapModel) {
		this.mapModel = mapModel;
		this.fileName = fileName;
		this.period = period;
	}
	
	public void init() {
		try {
			fileInputStream = new FileInputStream(fileName);
			bufferedInputStream = new BufferedInputStream(fileInputStream, 256000);
			reader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		tStep = new PeriodicTask(0, period) {
//			public void run() {
//			 try {
//					readNextPeriod();
//				} catch (XMLStreamException e) {
//					e.printStackTrace();
//				}
//			}
//		};
	}
	
	static double maxAvgDensity = -1;
	static double maxAvgOccupancy = -1;
	
	public boolean hasNextPeriod() throws XMLStreamException {
		return reader.hasNext();
	}
	
	public void readNextPeriod() throws XMLStreamException {
		mapModel.resetStats();
		
		while(reader.hasNext()) {
			int eventCode = reader.next();
			if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("interval")) {
				System.out.printf("Updating edge stats for period %d to %d\n", (int)Double.parseDouble(reader.getAttributeValue(ATTR_INTERVAL_BEGIN)), (int)Double.parseDouble(reader.getAttributeValue(ATTR_INTERVAL_END)));
			} else if(eventCode == XMLStreamReader.END_ELEMENT && reader.getLocalName().equals("interval")) {
				break;
			} else if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("edge")) {
				double samples = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_SAMPLEDSECONDS));
				if(samples < MIN_SAMPLES) continue;
				String id = reader.getAttributeValue(ATTR_EDGE_ID);
				SUMOMapModel.Edge edge = mapModel.getEdge(id);
				edge.sampledSeconds = samples;
				edge.avgDensity = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_DENSITY));
				edge.avgOccupancy = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_OCCUPANCY));
				edge.avgSpeed = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_SPEED));
				edge.travelTime = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_TRAVELTIME));
				if(ATTR_EDGE_VP_TRAVELTIME_COUNT < reader.getAttributeCount()) {
					edge.vpTravelTimeCount =  Integer.parseInt(reader.getAttributeValue(ATTR_EDGE_VP_TRAVELTIME_COUNT));
					edge.vpTravelTimeMin = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_VP_TRAVELTIME_MIN));
					edge.vpTravelTimeMax = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_VP_TRAVELTIME_MAX));
					edge.vpTravelTimeAvg = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_VP_TRAVELTIME_AVG));
					edge.vpTravelTimeStd = Double.parseDouble(reader.getAttributeValue(ATTR_EDGE_VP_TRAVELTIME_STD));
				
				}
				
				if(edge.avgDensity > maxAvgDensity) 	maxAvgDensity 	= edge.avgDensity;
				if(edge.avgOccupancy > maxAvgOccupancy) maxAvgOccupancy = edge.avgOccupancy;
			}
		}
		System.out.format("Max density: %.2f\t Max occupancy: %.2f\n", maxAvgDensity, maxAvgOccupancy);
	}
}
