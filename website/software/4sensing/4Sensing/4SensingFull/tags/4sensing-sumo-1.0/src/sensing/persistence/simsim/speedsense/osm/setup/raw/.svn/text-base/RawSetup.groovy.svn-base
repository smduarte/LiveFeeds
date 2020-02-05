package sensing.persistence.simsim.speedsense.osm.setup.raw;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.sensors.GPSReading;

class RawSetup extends SpeedSenseSetup {
	File output;
	
	public void init(OSMSpeedSenseSim sim)  {
		super.init(sim);
		config.RUN_TIME=3600;
//		output = new File(getOutputBasePath());
//		output.mkdirs();
//		output = new File(getOutputBasePath()+"TrafficRaw.csv");
//		output.write("") // clear contents
	}
	

	protected void startQuery() {
		Query q = new Query("speedsense.TrafficRaw", sim.world)
		runQuery(q) { GPSReading r ->
//			output.append([r.time, r.lat, r.lon, r.speed].join(',') + "\n");
		}
	}
	
	public List setupCharts() {
		return []
	}

}
