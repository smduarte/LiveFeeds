package sensing.simsim.app.vtable

import static sensing.simsim.sys.Sim4Sensing.mapModel
import sensing.core.sensors.*
import sensing.simsim.app.tuples.CountedMappedSpeed
import sensing.simsim.app.tuples.MappedSpeed
import sensing.simsim.app.tuples.SGPSReading
import sensing.simsim.app.tuples.SpeedHistogram

sensorInput(SGPSReading)
dataSource {
       process { SGPSReading r ->
			   def m = new MappedSpeed(r);
			   m.boundingBox = mapModel.getSegmentExtent(r.segmentId);
               return m;
       }

       timeWindow(mode: periodic, size:15, slide:10)
       groupBy(['segmentId', 'speedClass']) {
               aggregate(CountedMappedSpeed) { MappedSpeed m ->
				   count(m, 'count')
               }			
       }
}

globalAggregation {
       timeWindow(mode: periodic, size:10, slide:10)
       aggregate( SpeedHistogram ) { CountedMappedSpeed a ->				   
		 if( a != null ) {
			 agResult.update(a);
		 }
       }
}