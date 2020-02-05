package sensing.persistence.simsim.speedsense.sumo.setup;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import sensing.persistence.simsim.speedsense.charts.SegmentMTimeChart;
import sensing.persistence.simsim.PipelineSimulation;
import speedsense.*;

public class SUMOSegmentDetailTTSetup extends SpeedSenseSetup {
	
	public SUMOSegmentDetailTTSetup() {
		config.QUERY_RESULT_VALIDITY = 300;
		config.SIM_UPDATE_INTERVAL = 10;
		config.SAMPLING_PERIOD = 60;
		config.RUN_TIME = 1810;
		config.IDLE_TIME = 0;
		config.SIM_TIME_WARP = 5;
		config.SUMO_MNODE_RATE = 1;
		config.SIM_SEED = 5L;
		config.NET_SEED = 5L;
	}
	
	public Query getQuery() {
		String pipelineName = "speedsense.TravelTimeVT";
		double centerLat = PipelineSimulation.world.center().y;
		double centerLon = PipelineSimulation.world.center().x;
		double width 	= 0.25 * PipelineSimulation.world.width;
		double height = 0.25 * PipelineSimulation.world.height;
		
		return createQuery(pipelineName, centerLat, centerLon, width, height);
	}
	
	protected void startQuery() {
		Query q = getQuery();
		initQuery(q);
		runQuery(q) { AggregateTravelTime t -> 
			putResult("${t.segmentId}", t);
		}
	}
	
	public setupCharts() {
		SegmentMTimeChart avgTravelTime = new SegmentMTimeChart(
			"Min": {
				List s = getResult("${selectedSegmentId}");
				return (s ? s[0].minTravelTime : -1);
			}, 
			"Max": {
				List s = getResult("${selectedSegmentId}");
				return (s ? s[0].maxTravelTime : -1);
			}, 
			"Avg": {
				List s = getResult("${selectedSegmentId}");
				return (s ? s[0].avgTravelTime : -1);
			},
			"Std": {
				List s = getResult("${selectedSegmentId}");
				return (s ? s[0].stdTravelTime : -1);
			},
			"SUMO TravelTime": {
				if(selectedSegmentId) {
					double tt = sim.mapModel.getEdge(selectedSegmentId).pTravelTime;
					return tt >= 0 ? tt  : -1;
				} else {
					return -1;
				}
			},
			title: "Travel Time", yLabel: "sec"
		);
		
		SegmentMTimeChart count = new SegmentMTimeChart(
			"SpeedSense Count": {
				List s = getResult(selectedSegmentId);
				return (s ? s[0].count : -1);
			},
			"SUMO Count": {
				if(selectedSegmentId) {
					double tt = sim.mapModel.getEdge(selectedSegmentId).pVpTravelTimeCount;
					return tt >= 0 ? tt  : -1;
				} else {
					return -1;
				}
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
		
		return [avgTravelTime, avgOccupancy, count];
	}
}
