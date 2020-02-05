package apps.nodecount;
import sensing.persistence.core.sensors.GPSReading;
import sensing.persistence.core.pipeline.Tuple;

sensorInput(GPSReading)
dataSource{
	set(['mNodeId'], ttl:5)
	process {GPSReading r -> return new TNodeCount(count: 1)}
	aggregate(TNodeCount) { TNodeCount c -> sum(c, 'count', 'count')}
}

globalAggregation {
	set(['peerId'], ttl:5)
	aggregate(TNodeCount) {TNodeCount c -> sum(c, 'count', 'count')}
}
