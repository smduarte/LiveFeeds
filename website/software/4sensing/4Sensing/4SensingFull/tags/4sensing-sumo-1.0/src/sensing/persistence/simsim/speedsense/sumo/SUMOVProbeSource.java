package sensing.persistence.simsim.speedsense.sumo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SUMOVProbeSource {
	final static int ATTR_ID = 0;
	final static int ATTR_LANE = 1;
	final static int ATTR_POS = 2;
	final static int ATTR_X = 3;
	final static int ATTR_Y = 4;
	final static int ATTR_LAT = 5;
	final static int ATTR_LON = 6;
	final static int ATTR_SPEED = 7;
	
	final String fileName;
	final SUMOVProbeHandler handler;
	
	FileInputStream fileInputStream;
	InputStream bufferedInputStream;
	XMLStreamReader reader;

	
	public SUMOVProbeSource(String fileName, SUMOVProbeHandler handler) {
		this.fileName = fileName;
		this.handler = handler;
	}
	
	public void init() {
		try {
			fileInputStream = new FileInputStream(fileName);
			bufferedInputStream = new BufferedInputStream(fileInputStream, 256000);
			reader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
		if(reader != null) {
			bufferedInputStream.close();
			reader.close();
		}
	}
	
	public boolean hasNextTimestep() throws XMLStreamException {
		return reader.hasNext();
	}
	
	public void readNextTimestep() throws XMLStreamException {
		while(reader.hasNext()) {
			int eventCode = reader.next();
			if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("timestep")) {
				double ts = Double.parseDouble(reader.getAttributeValue(0));
				handler.timestepStart(ts);
			} else if(eventCode == XMLStreamReader.END_ELEMENT && reader.getLocalName().equals("timestep")) {
				handler.timestepEnd();
				break;
			} else if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("vehicle")) {
				String id = reader.getAttributeValue(ATTR_ID);
				double speed = Double.parseDouble(reader.getAttributeValue(ATTR_SPEED));
				double pos = Double.parseDouble(reader.getAttributeValue(ATTR_POS));
				double lat = Double.parseDouble(reader.getAttributeValue(ATTR_LAT));
				double lon = Double.parseDouble(reader.getAttributeValue(ATTR_LON));
				double x = Double.parseDouble(reader.getAttributeValue(ATTR_X));
				double y = Double.parseDouble(reader.getAttributeValue(ATTR_Y));
				String laneId = reader.getAttributeValue(ATTR_LANE);
				handler.vehicleUpdate(id, lat, lon, x, y, speed, laneId, pos);
			}
		}
	}
}
