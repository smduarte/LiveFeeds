package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;

class HSSetupF2_15_10s extends HSSetupF2 {
	
	public HSSetupF2_15_10s() {
		config.FAIL_RATE = 0.15;
		config.FAIL_STAB_PERIOD = 10;
	}
	
}