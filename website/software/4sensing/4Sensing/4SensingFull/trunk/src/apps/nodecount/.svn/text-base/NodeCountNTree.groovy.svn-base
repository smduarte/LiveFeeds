package apps.nodecount
;
import sensing.persistence.core.sensors.GPSReading;
import sensing.persistence.core.pipeline.Tuple;

import java.awt.geom.Rectangle2D;
import static sensing.persistence.core.logging.LoggingProvider.*;



def getQuadrant(Rectangle2D area, double lat, double lon) {
	double cellW = area.width/2
	double cellH = area.height/2
	int cellX = (int)((lon-area.x)/cellW)
	int cellY = (int)((lat-area.y)/cellH)
	//logger.log(DEBUG, this, "getBoundingBox","lat: ${lat} lon:${lon} cellW: ${cellW} cellH: ${cellH} cellX: ${cellX} cellY: ${cellY} areaX: ${area.x} areaY: ${area.y}");
	Rectangle2D quad =  new Rectangle2D.Double(cellX*cellW+area.x, cellY*cellH+area.y, cellW, cellH);
	return [bounds: quad, name: "${cellX}_${cellY}"]
}


sensorInput(GPSReading)
dataSource{
	set(['mNodeId'], ttl:5)
	process {GPSReading r -> def quad = getQuadrant(querycontext.query.aoi, r.lat, r.lon)
		return new TNodeCount(count: 1, boundingBox: quad.bounds, mapId: quad.name)
	}
	groupBy(['mapId']) {
		aggregate(TNodeCount) { TNodeCount c -> sum(c, 'count', 'count')}
	}
}

globalAggregation {
	set(['peerId', 'mapId'], ttl:5)
	groupBy(['mapId']) {
		aggregate(TNodeCount) {TNodeCount c -> sum(c, 'count', 'count')}
	}
	process {TNodeCount c -> c.mapId ="queryarea"; return c}
}