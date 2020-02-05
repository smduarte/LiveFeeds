package sensing.persistence.simsim.speedsense.osm.qtree.continuous;
import sensing.persistence.simsim.speedsense.osm.qtree.*;
import sensing.persistence.simsim.speedsense.osm.*;
import sensing.persistence.simsim.speedsense.osm.setup.*;
import sensing.persistence.core.ServicesConfig;
import simsim.core.Globals;
import sensing.persistence.simsim.SimSetup;
import sensing.persistence.simsim.speedsense.osm.setup.hotspots.*;
import sensing.persistence.simsim.speedsense.osm.setup.nodecount.*;

public class QTCSpeedSenseSim extends QTSpeedSenseSim {

	public QTCSpeedSenseSim() {
		super();
		QUERY_IMPL_POLICY = ServicesConfig.QueryImplPolicy.QUAD_TREE;
	}
	
	public static void main(String[] args) throws Exception {
		config(args);
		QTCSpeedSenseSim sim = new QTCSpeedSenseSim();
		sim.init();
		sim.start();	
	}
}
