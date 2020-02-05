package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;


class HSSetup2 extends HotspotsSetup {
	
	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		double centerLat = 38.748100;
		double centerLon = -9.1639818;
		double width = 0.5 * OSMSpeedSenseSim.world.width;
		double height = 0.5 * OSMSpeedSenseSim.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
}
