package sensing.persistence.simsim.speedsense.sumo.setup;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import sensing.persistence.simsim.speedsense.charts.SegmentMTimeChart;
import sensing.persistence.simsim.PipelineSimulation;
import speedsense.AggregateSpeed;

public class SUMOSegmentDetailSetup extends SpeedSenseSetup {
	
	public SUMOSegmentDetailSetup() {
		config.VT_WINDOW_SIZE = 300;
		config.QUERY_RESULT_VALIDITY = 300;
		config.SIM_UPDATE_INTERVAL = 10;
		config.SAMPLING_PERIOD = 60;
		config.RUN_TIME = 1810;
		config.IDLE_TIME = 0;
		config.SIM_TIME_WARP = 1e9;
		config.SUMO_MNODE_RATE =  1;
		config.SIM_SEED = 5L;
		config.NET_SEED = 5L;
	}
	
	public Query getQuery() {
		String pipelineName = "speedsense.TrafficSpeedW";
		double centerLat = PipelineSimulation.world.center().y;
		double centerLon = PipelineSimulation.world.center().x;
		double width 	= 0.25 * PipelineSimulation.world.width;
		double height = 0.25 * PipelineSimulation.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
	protected void startQuery() {
		Query q = getQuery();
		initQuery(q);
		runQuery(q) { AggregateSpeed s -> 
			putResult("${s.segmentId}", s);
		}
	}
	
	public setupCharts() {
		SegmentMTimeChart avgSpeed = new SegmentMTimeChart(
			"SpeedSense Speed 1": {
				List s = getResult("${selectedSegmentId}");
				return (s ? s[0].avgSpeed * 3.6 : -1);
			}, 
//			"SpeedSense Speed 2": {
//				List s = getResult("${selectedSegmentId}_1");
//				return (s ? s[0].avgSpeed * 3.6 : -1);
//			},
//			"SpeedSense Speed 3": {
//				List s = getResult("${selectedSegmentId}_2");
//				return (s ? s[0].avgSpeed * 3.6 : -1);
//			},
//			"SpeedSense Speed 4": {
//				List s = getResult("${selectedSegmentId}_3");
//				return (s ? s[0].avgSpeed * 3.6 : -1);
//			},
//			"SpeedSense Speed 5": {
//				List s = getResult("${selectedSegmentId}_4");
//				return (s ? s[0].avgSpeed * 3.6 : -1);
//			},
			"SUMO Speed": {
				if(selectedSegmentId) {
					double speed = sim.mapModel.getEdge(selectedSegmentId).pAvgSpeed;
					//.getVProbeAvgSpeed(selectedSegmentId);
					//getAvgSpeed(selectedSegmentId);
					return speed >= 0 ? speed * 3.6 : -1;
				} else {
					return -1;
				}
			},
			"VProbe Speed" : {
				if(selectedSegmentId) {
					double speed = sim.mapModel.getEdge(selectedSegmentId).vProbePAvgSpeed;
					return speed >= 0 ? speed * 3.6 : -1;
				} else {
					return -1;
				}
			},
			title: "Segment Speed", yLabel: "Speed km/h");
		
		SegmentMTimeChart count = new SegmentMTimeChart(
			"Count": {
				List s = getResult(selectedSegmentId);
				return (s ? s[0].count : -1);
			},
			"V Count": {
				List s = getResult(selectedSegmentId);
				return (s ? s[0].vCount : -1);
			},
			title: "Segment Count", yLabel: "Num samples");
			
		SegmentMTimeChart avgOccupancy = new SegmentMTimeChart(
			"Occupancy": {
				if(selectedSegmentId) {
					double o = sim.mapModel.getAvgOccupancy(selectedSegmentId);
					return o >= 0 ? o * 3.6 : -1;
				} else {
					return -1;
				}
			},
			title: "Segment Occupancy", yLabel: "Occupancy %");
		
		return [avgSpeed, avgOccupancy, count];
	}
}
