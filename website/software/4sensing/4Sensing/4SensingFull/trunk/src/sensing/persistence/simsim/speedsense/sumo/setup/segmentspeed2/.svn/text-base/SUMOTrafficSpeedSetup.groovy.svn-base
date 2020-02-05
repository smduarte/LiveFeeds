package sensing.persistence.simsim.speedsense.sumo.setup.segmentspeed2

import sensing.persistence.simsim.speedsense.sumo.setup.*;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.map.MapModel;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import  sensing.persistence.simsim.PipelineSimulation;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;

import speedsense.AggregateSpeed;

class SUMOTrafficSpeedSetup extends SpeedSenseSetup {
	LineOutputMetric lineOut;

	public SUMOTrafficSpeedSetup() {
		config.RUN_TIME = 1810;
		config.IDLE_TIME = 0;
		config.VT_WINDOW_SIZE = 300;
		config.SAMPLING_PERIOD = 60;
		config.SUMO_MAP_MODEL = "koeln_bbox_net.xml";
		config.SUMO_VPROBE_DATA = "sumocfg3_vtypeprobe_5s_nointernal.xml"
		config.SUMO_EDGE_DATA = "sumocfg3_meandata-edge_5m_nointernal.xml"
		config.SUMO_MNODE_CLASS_NAME = "sensing.persistence.simsim.speedsense.sumo.SUMOMNodeSegmentSpeed2"
		config.SUMO_MNODE_RATE = 1;
		config.SUMO_MNODE_WINDOWSIZE = 300;
		config.SUMO_VPROBE_SAMPLING_PERIOD = 5;
		config.SUMO_EDGESTATS_PERIOD = 300;
		config.SIM_SEED = 5L;
		config.NET_SEED = 5L;	
		config.SIM_TIME_WARP = 1e9;
	}
	
	
	protected void outputResult(AggregateSpeed s) {
		def edge = mapModel.getEdge(s.segmentId)
		lineOut.newLine([	s.count,
							s.vCount,
							s.minSpeed * 3.6,
							s.maxSpeed * 3.6,
							s.avgSpeed * 3.6,
							s.stdSpeed * 3.6,
							edge.vProbePMinSpeed * 3.6,
							edge.vProbePMaxSpeed * 3.6,
							edge.vProbePAvgSpeed * 3.6,
							edge.vProbePStdSpeed * 3.6,
							edge.vProbePSamples,
							edge.pAvgSpeed * 3.6,
							edge.pAvgOccupancy,
							edge.pAvgDensity,
							edge.pSampledSeconds,
							edge.maxSpeed,
							edge.numLanes,
							edge.length,
							edge.hasTrafficLight ? 1 : 0,
							q.aoi.contains(edge.bbox) ? 1 : 0,
							s.segmentId,
							sim.currentTime()])
	}
	
	protected void startQuery() {
		String pipelineName = "speedsense.TrafficSpeedW";
		double centerLat = sim.world.center().y;
		double centerLon = sim.world.center().x;
		double width = 0.25 * sim.world.width;
		double height = 0.25 * sim.world.height;
		
		Query q = createQuery(pipelineName, centerLat, centerLon, width, height);
		runQuery(q) { AggregateSpeed s -> outputResult(s)};
	}
	
	public setupCharts() {
		lineOut = new LineOutputMetric("results.gpd", "");
		return [lineOut];
	}
	
	
}
