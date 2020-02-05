package seeds.sensors.api.mic;

import seeds.sensors.api.gps.GpsLocation;

public class MicData {
	
	public String filename;
	public GpsLocation location; 
	
	public MicData() {
		this("no data", new GpsLocation() ) ;
	}
	
	public MicData(String filename, GpsLocation location ) {
		this.filename = filename;
		this.location = location;
	}
}
