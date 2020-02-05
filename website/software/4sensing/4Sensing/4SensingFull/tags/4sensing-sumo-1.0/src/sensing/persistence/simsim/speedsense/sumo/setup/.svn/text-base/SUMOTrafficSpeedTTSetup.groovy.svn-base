package sensing.persistence.simsim.speedsense.sumo.setup


import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.map.MapModel;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import  sensing.persistence.simsim.PipelineSimulation;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;

import speedsense.*;

class SUMOTrafficSpeedTTSetup extends SpeedSenseSetup {
	LineOutputMetric speedMetric;
	//LineOutputMetric speedMetric2;

	public SUMOTrafficSpeedTTSetup() {
		config.VT_WINDOW_SIZE = 300;
		config.QUERY_RESULT_VALIDITY = 300;
		config.SIM_UPDATE_INTERVAL = 10;
		config.SAMPLING_PERIOD = 60;
		config.RUN_TIME = 1810;
		config.IDLE_TIME = 0;
		config.SIM_TIME_WARP = 1e9;
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
			putResult(t.segmentId, t);
			def edge = mapModel.getEdge(t.segmentId)
				speedMetric.newLine([	t.count,
										t.realCount,
										t.minTravelTime,
										t.maxTravelTime,
										t.avgTravelTime,
										t.stdTravelTime,
										edge.length / t.avgTravelTime* 3.6, 
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
										t.segmentId,
										sim.currentTime()])
		}
	}
	
	public setupCharts() {
		speedMetric = new LineOutputMetric("results.gpd", "COUNT, MINTT, MAXTT, AVGTT, STDTT, AVGSPEED, VPMINSPEED, VPMAXSPEED, VPAVGSPEED, VPSTDSPEED, VPCOUNT, EDGESPEED, EDGEOCCUP, EDGEDENSITY, EDGECOUNT, MAXSPEED, NUMLANES, LENGTH, TL, CONTAINED, EDGEID, TIME");
		//speedMetric2 = new LineOutputMetric("SpeedError.gpd", "SUMO_SPEED, 4S_SPEED, DENSITY, OCCUPANCY, MAXSPEED, NUMLANES, LENGTH, SAMPLES, TL");
//		speedMetric2.metaClass.update = {
//			if(sim.currentTime() < 300) return
//			int numMiss = 0;
//			int numHit = 0;
//			mapModel.getEdges().each{ edge ->
//				if(edge.avgSpeed >= 0 ) {
//					def agr = getResult(edge.id)
//					
//					double ssSpeed = agr ? agr[0].avgSpeed : -1;
//					int count = agr ? agr[0].count : -1;
//					
//					if(edge.sampledSeconds >= 500 && count == -1) numMiss++
//					if(edge.sampledSeconds >= 500 && count > -1) numHit++
//
//					
//					newLine([	ssSpeed * 3.6,  // SpeedSense speed
//								count,
//								q.aoi.contains(edge.bbox) ? 1 : 0,
//								edge.avgSpeed* 3.6, // edge speed
//								edge.avgDensity,
//								edge.avgOccupancy,
//								edge.maxSpeed,
//								edge.numLanes,
//								edge.length,
//								edge.sampledSeconds,
//								edge.hasTrafficLight ? 1 : 0])
//					}
//					
//			}
//			println "hit: $numHit miss: $numMiss"
//		}
	

		
//		speedMetric = new LineOutputMetric("4SSpeedScatterPlot.csv", "COUNT, 4S_SPEED, SPEED, OCCUPANCY, DENSITY, MAXSPEED, NUMLANES, LENGTH, SAMPLES, TL");
//		speedMetric2 = new LineOutputMetric("SUMOSpeedDensityScatterPlot.csv", "SPEED, DENSITY, OCCUPANCY, MAXSPEED, NUMLANES, LENGTH, SAMPLES, TL");
//		
//		speedMetric2.metaClass.update = {
//			println "${PipelineSimulation.currentTime()}] wee"
//			mapModel.getEdges().each{ edge ->
//				if(edge.count > 10) {
//					newLine([edge.count, edge.sumSpeed/edge.count* 3.6, edge.avgSpeed* 3.6])
//				}
//			}
//		}
//		
//		speedMetric2.metaClass.update = {
//	
//			mapModel.getEdges().each{ edge ->
//				if(edge.avgSpeed >= 0) {
//				newLine([	edge.avgSpeed* 3.6, 
//							edge.avgDensity, 
//							edge.avgOccupancy, 
//							edge.maxSpeed, 
//							edge.numLanes, 
//							edge.length, 
//							edge.sampledSeconds, 
//							edge.hasTrafficLight ? 1 : 0])
//				}
//			}
//		}
		return [speedMetric]
	}
	
	
}
