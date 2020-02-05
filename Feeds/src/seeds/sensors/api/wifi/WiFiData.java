package seeds.sensors.api.wifi;

import java.util.ArrayList;
import java.util.Collection;

import seeds.sensors.api.gps.GpsLocation;

public class WiFiData {
	
	public GpsLocation location ;
	public Collection<HotSpot> hotspots;
	
	public WiFiData() {
		this( new ArrayList<HotSpot>(), new GpsLocation() ) ;
	}
	
	public WiFiData( Collection<HotSpot> hotspots, GpsLocation location ) {
		this.hotspots = hotspots;
		this.location = location;
	}

	
	public String toString() {
		return String.format("%s <%s>", hotspots, location ) ;
	}
}
