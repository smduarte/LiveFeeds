package apps.nodecount;

import sensing.persistence.core.sensors.GPSReading;
import sensing.persistence.core.pipeline.Tuple;

import java.awt.geom.Rectangle2D;
import static sensing.persistence.core.logging.LoggingProvider.*;

import apps.Grid;

final int START_LEVEL = 2;

sensorInput(GPSReading)

dataSource{
	set(['mNodeId'], mode: change, ttl:5)
	process {GPSReading r -> def cell = Grid.getCell(querycontext.query.aoi,[lat: r.lat, lon: r.lon], START_LEVEL)
		return new TNodeCount(count: 1, boundingBox: cell.boundingBox, cellId: cell.cellId, level: START_LEVEL)
	}
	groupBy(['cellId']) {
		aggregate(TNodeCount) { TNodeCount c -> sum(c, 'count', 'count')}
	}
}

globalAggregation {
	set(['peerId', 'cellId'], mode: change, ttl:5)
	groupBy(['cellId']) {
		aggregate(TNodeCount) {TNodeCount c -> sum(c, 'count', 'count')}
	}
	process {TNodeCount c ->  def level = c.level > 0 ? c.level- 1 : c.level
		def cell = getCell(querycontext.query.aoi, getCentroid(c.boundingBox), level)
		c.boundingBox = cell.boundingBox; c.cellId = cell.cellId; c.level = level;
		return c
	}
}