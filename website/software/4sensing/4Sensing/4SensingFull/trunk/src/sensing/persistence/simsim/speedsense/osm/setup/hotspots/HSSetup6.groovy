package sensing.persistence.simsim.speedsense.osm.setup.hotspots;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.charts.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;

public class HSSetup6 extends HotspotsSetup {
	
	static def centerCoords = [
	  [lat:38.75238626892152,lon:-9.180570256034969],
	  [lat:38.74732799262342,lon:-9.135315484624499],
	  [lat:38.72774206507317,lon:-9.212876920181845],
	  [lat:38.757817324388434,lon:-9.158111177114582],
	  [lat:38.74598755610839,lon:-9.223156276670542],
	  [lat:38.7041288146267,lon:-9.208758016305431],
	  [lat:38.74446662166502,lon:-9.117968584932754],
	  [lat:38.753412744574455,lon:-9.13429740402964],
	  [lat:38.76402400282587,lon:-9.148193135902343],
	  [lat:38.71358799474714,lon:-9.148390592030335],
	  [lat:38.72194035955837,lon:-9.171158760021752],
	  [lat:38.73771320484383,lon:-9.179208947911961],
	  [lat:38.74887110636154,lon:-9.122907875618518],
	  [lat:38.736937891987644,lon:-9.163823381852621],
	  [lat:38.74051605912868,lon:-9.144374687711414]
	];

	static int queryIdx = 0;
	static String pipelineName = "speedsense.TrafficHotspots";

	public Query getQuery() {
		if(queryIdx >= centerCoords.size()) return null;

		double width = 0.25 * OSMSpeedSenseSim.world.width;
		double height = 0.25 * OSMSpeedSenseSim.world.height;
		
		double centerLat = centerCoords[queryIdx].lat;
		double centerLon = centerCoords[queryIdx].lon;
		queryIdx++;

		return new Query(pipelineName).area(minLat: centerLat-height/2, minLon: centerLon-width/2, maxLat: centerLat+height/2, maxLon:centerLon+width/2);	
	}
}
