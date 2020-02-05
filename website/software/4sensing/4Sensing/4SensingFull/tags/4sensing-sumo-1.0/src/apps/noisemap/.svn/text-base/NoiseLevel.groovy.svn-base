package apps.noisemap;

import java.awt.geom.Rectangle2D;
import apps.Grid;
import speedsense.AggregateSpeed;
import static sensing.persistence.core.logging.LoggingProvider.*;
import sensing.persistence.core.pipeline.Tuple;
import static apps.noisemap.TNoiseLevel.NoiseClass.*;

def GRID_LEVEL = 3
def COUNT_THRESHOLD = 15

tableInput("speedsense.TrafficSpeed") 

globalAggregation {
	timeWindow(mode:periodic, size: 10, slide: 10)
	process { AggregateSpeed a -> 
		services.logging.log(DEBUG, this, "mapper", a.toString());
		a.derive(TNoiseLevel, Grid.getCell(services.config.world, Grid.getCentroid(a.boundingBox), GRID_LEVEL))
	}
	groupBy(['cellId']) {
		aggregate(TNoiseLevel) {TNoiseLevel n ->
			avg(n, 'sumSpeed', 'count', 'avgSpeed')
	}}
	classify { TNoiseLevel n ->
		if(n.count >= COUNT_THRESHOLD) {
			switch(n.sumSpeed) {
				case {it >= 2000}: n.level = HIGH; break;
				case {it >= 1000} : n.level = MED; break;
				default: n.level = LOW;
			}; return n
	}}
}
