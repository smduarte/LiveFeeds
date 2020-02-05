package sensing.persistence.simsim.speedsense.osm.setup.hotspots.nodefail;

import sensing.persistence.core.query.Query;

class HSSetupF2_05_05s extends HSSetupF2 {
	
	public HSSetupF2_05_05s() {
		config.FAIL_RATE = 0.05;
		config.FAIL_STAB_PERIOD = 5;
	}
}