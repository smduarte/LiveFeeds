package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup7 extends HSSetup6 {
	

	public Query getQuery() {
		if(queryIdx >= centerCoords.size()) return null;

		double width = 0.5 * OSMSpeedSenseSim.world.width;
		double height = 0.5 * OSMSpeedSenseSim.world.height;
		
		double centerLat = centerCoords[queryIdx].lat;
		double centerLon = centerCoords[queryIdx].lon;
		queryIdx++;

		return new Query(pipelineName).area(minLat: centerLat-height/2, minLon: centerLon-width/2, maxLat: centerLat+height/2, maxLon:centerLon+width/2);	
	}
}
