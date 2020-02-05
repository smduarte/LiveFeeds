package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail

import sensing.persistence.simsim.PipelineSimulation;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.HotspotsSetup;
import simsim.core.Task;
import sensing.persistence.core.query.QueryService;
import sensing.persistence.core.network.PeerDB;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.*;

abstract class HSSetupNFailure extends HotspotsSetup {
	
	public HSSetupNFailure() {
		super();
		config.FAIL_TIME = 150;
		config.TREE_TRANSITION_PERIOD = 60;
		config.TOTAL_MNODES = 50000;
		config.DENSITY_SPEED_WEIGHT = 1/50;
		config.COUNT_THRESH = 60;
		config.MAIN_WAYS = false;
	}
	
	public void init(OSMSpeedSenseSim sim) {
		super.init(sim);
		setupFailureTask();
	}
	
	protected void setupFailureTask() {
		if(FAIL_TIME == 0) return;
		new Task(FAIL_TIME) {
			public void run() {
				println "### Node failure"
				int numFail = PipelineSimulation.setup.FAIL_RATE * PeerDB.peersList.size();
				def failNodes = [];
				while(failNodes.size() < numFail) {
					int nodeIdx = OSMSpeedSenseSim.rg.nextInt(PeerDB.peersList.size());
					if(failNodes.indexOf(nodeIdx) < 0) {
						failNodes << PeerDB.peersList[nodeIdx];
						//println "failure at ${nodeIdx} -> ${PipelineSimulation.nodeList[nodeIdx]}";
					}		
				}
				PipelineSimulation.putOffline(failNodes);
			};
		}
	}
	
	protected setupCharts() {
		List charts = super.setupCharts();
		// new ResultSetAverage(), new ResultSetSampler(),
		return [*charts,  ResultsPerSlot.instance, new ErrorChart(true)]

// MQ
//		ResultsPerSlot res = new ResultsPerSlot(1);
//		return [*charts, res, new ErrorChart(res)];
	}
	
	
}
