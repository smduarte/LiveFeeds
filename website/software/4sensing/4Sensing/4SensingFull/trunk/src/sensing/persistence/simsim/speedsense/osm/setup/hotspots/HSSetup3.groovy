package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

class HSSetup3 extends HotspotsSetup {
	
	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		double centerLat = OSMSpeedSenseSim.world.y + OSMSpeedSenseSim.world.height/2;
		double centerLon = OSMSpeedSenseSim.world.x + OSMSpeedSenseSim.world.width/2;
		double width = OSMSpeedSenseSim.world.width;
		double height = OSMSpeedSenseSim.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
}
