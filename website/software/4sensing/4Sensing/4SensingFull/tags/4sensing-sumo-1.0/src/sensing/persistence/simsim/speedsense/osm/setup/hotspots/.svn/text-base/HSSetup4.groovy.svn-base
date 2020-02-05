package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup4 extends HotspotsSetup {

	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		
		double width = 0.25 * OSMSpeedSenseSim.world.width;
		double height = 0.25 * OSMSpeedSenseSim.world.height;
		
		double centerLat = OSMSpeedSenseSim.world.y + height/2 + OSMSpeedSenseSim.rg.nextDouble()*(OSMSpeedSenseSim.world.height-height);
		double centerLon = OSMSpeedSenseSim.world.x + width/2 + OSMSpeedSenseSim.rg.nextDouble()*(OSMSpeedSenseSim.world.width-width);
	

		return new Query(pipelineName).area(minLat: centerLat-height/2, minLon: centerLon-width/2, maxLat: centerLat+height/2, maxLon:centerLon+width/2);	
	}
}
