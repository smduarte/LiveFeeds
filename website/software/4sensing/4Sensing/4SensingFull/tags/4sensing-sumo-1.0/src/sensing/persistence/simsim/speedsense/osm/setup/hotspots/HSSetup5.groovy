package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup5 extends HotspotsSetup {

	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		
		double centerLat = 38.7481003;
		double centerLon = -9.1639818;
		double width = 0.25 * OSMSpeedSenseSim.world.width;
		double height = 0.25 * OSMSpeedSenseSim.world.height;
		double lat;
		double lon;
		
		double deltaLat = OSMSpeedSenseSim.world.height/10;
		if(sim.rg.nextDouble() > 0.5) {
			lat = Math.min(centerLat + deltaLat, OSMSpeedSenseSim.world.maxY - height/2);
		} else {
			lat = Math.max(centerLat - deltaLat, OSMSpeedSenseSim.world.y + height/2);
		}
		double deltaLon = OSMSpeedSenseSim.world.width/10;
		if(sim.rg.nextDouble() > 0.5) {
			lon = Math.min(centerLon + deltaLon, OSMSpeedSenseSim.world.maxX - width/2);
		} else {
			lon = Math.max(centerLon - deltaLon, OSMSpeedSenseSim.world.x + width/2);
		}

		return new Query(pipelineName).area(minLat: lat-height/2, minLon: lon-width/2, maxLat: lat+height/2, maxLon:lon+width/2);	
	}
}
