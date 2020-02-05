package sensing.persistence.simsim.speedsense.osm.setup.nodecount;

import sensing.persistence.core.query.Query;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import sensing.persistence.simsim.charts.MsgPerSecondChart;


public class SetupNodeCount extends SpeedSenseSetup {
	public static int RUN_TIME = 7000;
	int lastCount;
	
	protected  void startQuery() {
		String vtable = "sensing.persistence.simsim.speedsense.osm.pipeline.continuous.NodeCount";
		double centerLat = 38.7481003; // high mnode density
		double centerLon = -9.1639818;
//		double centerLat=38.7383712;  // low mnode density
//		double centerLon=-9.1286197;
		double width = 0.6 * OSMSpeedSenseSim.world.width;
		double height = 0.7 * OSMSpeedSenseSim.world.height;
		
		Query q = createQuery(vtable, centerLat, centerLon, width, height);
		println "aoi: ${q.aoi}"
		runQuery(q) {}
	}
	
	public setupCharts(){
		return [MsgPerSecondChart.instance, QueryErrorChart.instance];
		//return []
	}
}
