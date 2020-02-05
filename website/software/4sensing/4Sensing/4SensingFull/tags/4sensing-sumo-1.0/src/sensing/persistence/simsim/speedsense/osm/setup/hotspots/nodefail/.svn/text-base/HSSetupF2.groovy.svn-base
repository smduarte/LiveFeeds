package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;

class HSSetupF2 extends HSSetupNFailure {
	
	public Query getQuery() {
		String pipelineName = "speedsense.TrafficHotspots";
		double centerLat = 38.748100;
		double centerLon = -9.1639818;
		double width = 0.5 * sim.world.width;
		double height = 0.5 * sim.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
}
