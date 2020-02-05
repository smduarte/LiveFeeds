package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup1 extends HotspotsSetup {

	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		double centerLat = 38.7481003;
		double centerLon = -9.1639818;
		double width = 0.25 * OSMSpeedSenseSim.world.width;
		double height = 0.25 * OSMSpeedSenseSim.world.height;
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
}
